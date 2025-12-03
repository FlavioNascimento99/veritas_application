package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final ProcessService processService;
    private final SubjectService subjectService;
    private final MeetingService meetingService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");

        // Obtém todas as roles do usuário autenticado
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (roles.contains("ROLE_ADMIN")) {
            // Admin overview metrics
            model.addAttribute("studentsCount", studentService.findAll().size());
            model.addAttribute("professorsCount", professorService.findAll().size());
            model.addAttribute("subjectsCount", subjectService.findAll().size());
            model.addAttribute("processesCount", processService.findAllProcesses().size());
            // Recent processes (last 5)
            var recent = processService.findAllProcesses().stream()
                    .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(5)
                    .toList();
            model.addAttribute("recentProcesses", recent);
            model.addAttribute("mainContent", "pages/dashboard-admin :: content");
        
        } else if (roles.contains("ROLE_COORDINATOR")) {
            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado."));
            model.addAttribute("professor", professor);
            model.addAttribute("allProcesses", processService.findAllProcesses());
            model.addAttribute("waitingProcesses", processService.findWaitingProcesses());
            model.addAttribute("professors", professorService.findAll());
            model.addAttribute("mainContent", "pages/dashboard-coordinator :: content");
        
        } else if (roles.contains("ROLE_PROFESSOR")) {
            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
            model.addAttribute("professor", professor);
            model.addAttribute("processes", processService.listByProfessor(professor.getId()));
            model.addAttribute("meetings", meetingService.findAll());
            model.addAttribute("mainContent", "pages/dashboard-professor :: content");
        
        } else if (roles.contains("ROLE_STUDENT")) {
            Student student = studentService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado."));
            model.addAttribute("student", student);
            model.addAttribute("processes", processService.listByStudent(student.getId()));
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("mainContent", "pages/dashboard-student :: content");
        
        } else {
            // Se o usuário não tiver nenhuma role conhecida, redireciona para o login
            return "redirect:/login?error";
        }

        return "home";
    }

    // Coordinator action to assign a professor to a process (form POST)
    @PostMapping("/assign")
    public String assignProfessorToProcess(@RequestParam("processId") Long processId,
                                           @RequestParam("professorId") Long professorId,
                                           RedirectAttributes redirectAttributes) {
        processService.distribute(processId, professorId);
        redirectAttributes.addFlashAttribute("successMessage", "Processo distribuído com sucesso.");
        return "redirect:/dashboard";
    }

}
