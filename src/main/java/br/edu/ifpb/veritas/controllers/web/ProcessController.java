package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.enums.VoteType;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.services.ProcessService;
import br.edu.ifpb.veritas.services.ProfessorService;
import br.edu.ifpb.veritas.services.StudentService;
import br.edu.ifpb.veritas.services.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/processes")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;
    private final StudentService studentService;
    private final VoteService voteService;
    private final ProfessorService professorService;

    @PostMapping("/create")
    public String createProcess(
            @ModelAttribute Process process,
            @RequestParam Long subjectId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String studentLogin = authentication.getName();
        var studentOpt = studentService.findByLogin(studentLogin);
        var student = studentOpt.orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado para o usuário autenticado."));

        processService.createProcess(process, student.getId(), subjectId);
        redirectAttributes.addFlashAttribute("successMessage", "Processo criado com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String showProcess(@PathVariable("id") Long id, Model model, Authentication authentication) {
        var process = processService.findById(id);
        model.addAttribute("pageTitle", "Processo - " + (process.getTitle() != null ? process.getTitle() : process.getNumber()));
        model.addAttribute("activePage", "process");
        model.addAttribute("process", process);

        // ========== REQFUNC 5: Lógica de votação ==========
        // Verifica se o usuário logado é um professor
        var professorOpt = professorService.findByLogin(authentication.getName());
        if (professorOpt.isPresent()) {
            Professor professor = professorOpt.get();
            model.addAttribute("loggedProfessor", professor);

            // Verifica se é o relator do processo
            boolean isRapporteur = process.getProcessRapporteur() != null &&
                    process.getProcessRapporteur().getId().equals(professor.getId());
            model.addAttribute("isRapporteur", isRapporteur);

            // Verifica se o processo está em análise (pode ser votado)
            boolean canVote = process.getStatus() == StatusProcess.UNDER_ANALISYS;
            model.addAttribute("canVote", canVote);

            // Verifica se o professor já votou neste processo
            boolean hasVoted = voteService.hasVoted(id, professor.getId());
            model.addAttribute("hasVoted", hasVoted);

            // Adiciona os tipos de voto para o formulário
            model.addAttribute("voteTypes", VoteType.values());
        }

        model.addAttribute("mainContent", "pages/process-detail :: content");
        return "home";
    }

    /**
     * REQFUNC 5: Endpoint para registrar voto do professor no processo
     */
    @PostMapping("/{id}/vote")
    public String voteOnProcess(
            @PathVariable("id") Long processId,
            @RequestParam("voteType") VoteType voteType,
            @RequestParam("justification") String justification,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Busca o professor logado
            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));

            // Busca o processo
            Process process = processService.findById(processId);

            // Verifica se é o relator
            if (process.getProcessRapporteur() == null ||
                    !process.getProcessRapporteur().getId().equals(professor.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Apenas o relator pode votar neste processo.");
                return "redirect:/processes/" + processId;
            }

            // Verifica se o processo está sob análise
            if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
                redirectAttributes.addFlashAttribute("errorMessage", "Este processo não está disponível para votação.");
                return "redirect:/processes/" + processId;
            }

            // Verifica se já votou
            if (voteService.hasVoted(processId, professor.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Você já votou neste processo.");
                return "redirect:/processes/" + processId;
            }

            // Valida justificativa
            if (justification == null || justification.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "A justificativa é obrigatória.");
                return "redirect:/processes/" + processId;
            }

            // 4. Registra o voto
            voteService.registerVote(processId, professor.getId(), voteType, justification.trim());

            // 5. Mensagem de sucesso e redirect
            redirectAttributes.addFlashAttribute("successMessage",
                    "Voto registrado com sucesso! Tipo: " + voteType.getStatus());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao registrar voto: " + e.getMessage());
        }

        return "redirect:/processes/" + processId;
    }
}