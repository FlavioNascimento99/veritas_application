package br.edu.ifpb. veritas.controllers.web;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.models. Collegiate;
import br.edu.ifpb.veritas.models. Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core. GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web. bind.annotation.PostMapping;
import org.springframework. web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService    studentService;
    private final ProfessorService  professorService;
    private final ProcessService    processService;
    private final SubjectService    subjectService;
    private final MeetingService    meetingService;
    private final CollegiateService collegiateService;  // Adicionado para buscar o colegiado do professor

    @GetMapping
    public String dashboard(Model model, Authentication authentication,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) Long studentId,
                            @RequestParam(required = false) Long professorId,
                            @RequestParam(required = false) String meetingStatus) {  // Novo parâmetro para filtro de reuniões

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        /**
         * Em caso de Acesso Administrativo, vamos entregar todos os dados existentes em Banco. 
         */
        if (roles.contains("ROLE_ADMIN")) {
            model.addAttribute("studentsCount", studentService.findAll().size());
            model. addAttribute("professorsCount", professorService.findAll().size());
            model. addAttribute("subjectsCount", subjectService. findAll().size());
            model.addAttribute("processesCount", processService.findAllProcesses().size());

            /**
             * Ordenação de Serviços à acessos de cunho Administrativo 
             */
            var recent = processService.findAllProcesses().stream()
                    .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(10)
                    .toList();
            model.addAttribute("recentProcesses", recent);
            model.addAttribute("mainContent", "pages/dashboard-admin ::  content");
        
        /**
         * Renderizaç��o de Serviços à acessos de cunho Coordenativo 
         */  
        } else if (roles. contains("ROLE_COORDINATOR")) {

            Professor professor = professorService.findByLogin(authentication.getName())
                . orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado. "));

            model.addAttribute("professor", professor);
            // listas auxiliares para os filtros
            model.addAttribute("professors", professorService.findAll());
            model.addAttribute("students", studentService.findAll());

            // aplica filtros opcionais (status, studentId, professorId)
            var filtered = processService.findAllFiltered(status, studentId, professorId);
            model.addAttribute("allProcesses", filtered);
            // expose current filter values to the template to keep selections
            model.addAttribute("filterStatus", status);
            model. addAttribute("filterStudentId", studentId);
            model.addAttribute("filterProfessorId", professorId);
            model.addAttribute("waitingProcesses", processService.findWaitingProcesses());
            model.addAttribute("mainContent", "pages/dashboard-coordinator :: content");
        
        /**
         * Renderização de Serviços à acessos de cunho Mestre (Professor)
         */  
        } else if (roles. contains("ROLE_PROFESSOR")) {
            Professor professor = professorService. findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
            model.addAttribute("professor", professor);
            model.addAttribute("processes", processService.listByProfessor(professor. getId()));
            
            // ========== INÍCIO: Lógica para REQFUNC 4 ==========
            // Busca as reuniões do colegiado ao qual o professor pertence
            List<? > meetings = findMeetingsForProfessor(professor.getId(), meetingStatus);
            model. addAttribute("meetings", meetings);
            model.addAttribute("filterMeetingStatus", meetingStatus);  // Mantém o filtro selecionado no template
            // ========== FIM: Lógica para REQFUNC 4 ==========
            
            model.addAttribute("mainContent", "pages/dashboard-professor :: content");
        
        /**
         * Renderização de Serviços à acessos de cunho Estudantil 
         */  
        } else if (roles.contains("ROLE_STUDENT")) {
            Student student = studentService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado."));
            model.addAttribute("student", student);
            model.addAttribute("processes", processService.listByStudentFiltered(student.getId(), status, null));
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("mainContent", "pages/dashboard-student :: content");
            model.addAttribute("filterStatus", status);
        
        } else {
            /**
             * Usuário não apresenta nenhum perfil de Acesso == Erro. 
             */
            return "redirect:/login? error";
        }

        return "home";
    }

    // Método auxiliar para buscar reuniões do colegiado do professor. Aplica filtro de status se informado
    private List<?> findMeetingsForProfessor(Long professorId, String meetingStatus) {
        try {
            // Busca o colegiado ao qual o professor pertence
            Collegiate collegiate = collegiateService. findByProfessorId(professorId);
            
            // Se há filtro de status, aplica o filtro
            // caso contrário, retorna todas as reuniões do colegiado
            if (meetingStatus != null && !meetingStatus.isBlank()) {
                MeetingStatus statusEnum = MeetingStatus.valueOf(meetingStatus);
                return meetingService.findByCollegiateIdAndStatus(collegiate.getId(), statusEnum);
            } else {
                return meetingService.findByCollegiateId(collegiate.getId());
            }
        } catch (Exception e) {
            // Se professor não pertence a nenhum colegiado ou outro erro
            // retorna lista vazia
            return Collections.emptyList();
        }
    }

    // Rota para assinatura de Processo à Professores
    @PostMapping("/assign")
    public String assignProfessorToProcess(@RequestParam("processId") Long processId,
                                           @RequestParam("professorId") Long professorId,
                                           RedirectAttributes redirectAttributes) {
        processService.distribute(processId, professorId);
        redirectAttributes.addFlashAttribute("successMessage", "Processo distribuído com sucesso.");
        return "redirect:/dashboard";
    }
}