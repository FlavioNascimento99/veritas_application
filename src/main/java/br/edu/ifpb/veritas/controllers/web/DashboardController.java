package br.edu. ifpb.veritas.controllers.web;

import br. edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas. models. Collegiate;
import br.edu.ifpb.veritas. models.Meeting;
import br.edu. ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br. edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core. GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation. GetMapping;
import org.springframework.web.bind.annotation. PostMapping;
import org.springframework.web. bind.annotation.RequestMapping;
import org.springframework.web. bind.annotation.RequestParam;
import org.springframework.web. servlet.mvc.support.RedirectAttributes;

import java.util. Collections;
import java. util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService    studentService;
    private final ProfessorService  professorService;
    private final ProcessService    processService;
    private final SubjectService    subjectService;
    private final MeetingService    meetingService;
    private final CollegiateService collegiateService;  // Necessário para buscar o colegiado do professor

    @GetMapping
    public String dashboard(Model model, Authentication authentication,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) Long studentId,
                            @RequestParam(required = false) Long professorId,
                            @RequestParam(required = false) String meetingStatus) {  // Parâmetro para filtro de reuniões (REQFUNC 4)

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority:: getAuthority)
                .toList();

        /**
         * Em caso de Acesso Administrativo, vamos entregar todos os dados existentes em Banco. 
         */
        if (roles.contains("ROLE_ADMIN")) {
            model.addAttribute("studentsCount", studentService.findAll().size());
            model.addAttribute("professorsCount", professorService.findAll().size());
            model.addAttribute("subjectsCount", subjectService.findAll().size());
            model.addAttribute("processesCount", processService.findAllProcesses().size());

            var recent = processService.findAllProcesses().stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(10)
                    . toList();
            model.addAttribute("recentProcesses", recent);
            model.addAttribute("mainContent", "pages/dashboard-admin ::  content");

        /**
         * Renderização de Serviços à acessos de cunho Coordenativo
         */
        } else if (roles. contains("ROLE_COORDINATOR")) {

            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado. "));

            model.addAttribute("professor", professor);
            model.addAttribute("professors", professorService. findAll());
            model.addAttribute("students", studentService.findAll());

            var filtered = processService.findAllFiltered(status, studentId, professorId);
            model.addAttribute("allProcesses", filtered);
            model.addAttribute("filterStatus", status);
            model.addAttribute("filterStudentId", studentId);
            model. addAttribute("filterProfessorId", professorId);
            model.addAttribute("waitingProcesses", processService. findWaitingProcesses());
            model.addAttribute("mainContent", "pages/dashboard-coordinator :: content");

        /**
         * Renderização de Serviços à acessos de cunho Mestre (Professor)
         * Implementa REQFUNC 4 e REQFUNC 6
         */
        } else if (roles.contains("ROLE_PROFESSOR")) {

            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));

            // Colegiado do professor (ajuste conforme sua modelagem de relação)
            Collegiate collegiate = collegiateService.findByProfessorId(professor.getId());
            if (collegiate == null) {
                throw new IllegalArgumentException("Colegiado não encontrado para o professor.");
            }


            // Filtro de reuniões por status
            List<Meeting> collegiateMeetings;
            if (meetingStatus != null && !meetingStatus.isBlank()) {
                MeetingStatus statusEnum = MeetingStatus.valueOf(meetingStatus);
                collegiateMeetings = meetingService.findByCollegiateIdAndStatus(collegiate.getId(), statusEnum);
            } else {
                collegiateMeetings = meetingService.findByCollegiateId(collegiate.getId());
            }

            // Reuniões nas quais o professor está escalado e agendadas
            List<Meeting> scheduledMeetings = meetingService.findScheduledMeetingsByParticipant(professor.getId());

            model.addAttribute("professor", professor);
            model.addAttribute("collegiateMeetings", collegiateMeetings);
            model.addAttribute("scheduledMeetings", scheduledMeetings);

            // Popula o dropdown de status e devolve o valor selecionado
            model.addAttribute("meetingStatuses", MeetingStatus.values());
            model.addAttribute("filterMeetingStatus", meetingStatus);

            model.addAttribute("mainContent", "pages/dashboard-professor :: content");
        } else if (roles.contains("ROLE_STUDENT")) {
            Student student = studentService.findByLogin(authentication. getName())
                    .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado. "));
            model.addAttribute("student", student);
            model.addAttribute("processes", processService.listByStudentFiltered(student.getId(), status, null));
            model.addAttribute("subjects", subjectService. findAll());
            model.addAttribute("mainContent", "pages/dashboard-student :: content");
            model.addAttribute("filterStatus", status);

        } else {
            return "redirect:/login? error";
        }

        return "home";
    }

    /**
     * Método auxiliar para buscar reuniões do colegiado do professor (REQFUNC 4).
     * Aplica filtro de status se informado.
     *
     * @param professorId ID do professor logado
     * @param meetingStatus Status de filtro (AGENDADA, FINALIZADA ou null para todos)
     * @return Lista de reuniões do colegiado
     */
    private List<Meeting> findCollegiateMeetingsForProfessor(Long professorId, String meetingStatus) {
        try {
            // Busca o colegiado ao qual o professor pertence
            Collegiate collegiate = collegiateService.findByProfessorId(professorId);

            // Se há filtro de status, aplica; caso contrário, retorna todas as reuniões do colegiado
            if (meetingStatus != null && !meetingStatus. isBlank()) {
                MeetingStatus statusEnum = MeetingStatus.valueOf(meetingStatus);
                return meetingService.findByCollegiateIdAndStatus(collegiate.getId(), statusEnum);
            } else {
                return meetingService. findByCollegiateId(collegiate. getId());
            }
        } catch (Exception e) {
            // Professor não pertence a nenhum colegiado ou outro erro
            // Retorna lista vazia para não quebrar o fluxo
            return Collections.emptyList();
        }
    }

    /**
     * Rota para assinatura de Processo à Professores
     */
    @PostMapping("/assign")
    public String assignProfessorToProcess(@RequestParam("processId") Long processId,
                                           @RequestParam("professorId") Long professorId,
                                           RedirectAttributes redirectAttributes) {
        processService.distribute(processId, professorId);
        redirectAttributes.addFlashAttribute("successMessage", "Processo distribuído com sucesso.");
        return "redirect:/dashboard";
    }
}