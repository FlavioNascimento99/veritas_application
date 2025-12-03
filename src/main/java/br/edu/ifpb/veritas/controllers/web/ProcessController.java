package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.services.ProcessService;
import br.edu.ifpb.veritas.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/processes")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;
    private final StudentService studentService;


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
    public String showProcess(@PathVariable("id") Long id, Model model) {
        var process = processService.findById(id);
        model.addAttribute("pageTitle", "Processo - " + (process.getTitle() != null ? process.getTitle() : process.getNumber()));
        model.addAttribute("activePage", "process");
        model.addAttribute("process", process);
        model.addAttribute("mainContent", "pages/process-detail :: content");
        return "home";
    }
}