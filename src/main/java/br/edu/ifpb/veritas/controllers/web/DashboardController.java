package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.models.Collegiate;
import br.edu.ifpb.veritas.models.Meeting;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final ProcessService processService;
    private final SubjectService subjectService;
    private final MeetingService meetingService;
    private final CollegiateService collegiateService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) Long studentId,
                            @RequestParam(required = false) Long professorId,
                            @RequestParam(required = false) String meetingStatus) {

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (roles.contains("ROLE_ADMIN")) {
            model.addAttribute("studentsCount", studentService.findAll().size());
            model.addAttribute("professorsCount", professorService.findAll().size());
            model.addAttribute("subjectsCount", subjectService.findAll().size());
            model.addAttribute("processesCount", processService.findAllProcesses().size());

            var recent = processService.findAllProcesses().stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(10)
                    .toList();
            model.addAttribute("recentProcesses", recent);
            model.addAttribute("mainContent", "pages/dashboard-admin :: content");

        } else if (roles.contains("ROLE_COORDINATOR")) {

            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado."));

            model.addAttribute("professor", professor);
            model.addAttribute("professors", professorService.findAll());
            model.addAttribute("students", studentService.findAll());

            var filtered = processService.findAllFiltered(status, studentId, professorId);
            model.addAttribute("allProcesses", filtered);
            model.addAttribute("filterStatus", status);
            model.addAttribute("filterStudentId", studentId);
            model.addAttribute("filterProfessorId", professorId);
            model.addAttribute("waitingProcesses", processService.findWaitingProcesses());
            
            // Carregar reuniões do colegiado
            try {
                Collegiate collegiate = collegiateService.findByProfessorId(professor.getId());
                List<Meeting> collegiateMeetings = meetingService.findByCollegiateId(collegiate.getId());
                model.addAttribute("collegiateMeetings", collegiateMeetings);
            } catch (Exception e) {
                model.addAttribute("collegiateMeetings", Collections.emptyList());
            }
            
            model.addAttribute("mainContent", "pages/dashboard-coordinator :: content");

        } else if (roles.contains("ROLE_PROFESSOR")) {
            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));

            model.addAttribute("professor", professor);
            model.addAttribute("processes", processService.listByProfessor(professor.getId()));
            model.addAttribute("meetingStatuses", MeetingStatus.values());

            List<Meeting> collegiateMeetings = findCollegiateMeetingsForProfessor(professor.getId(), meetingStatus);
            model.addAttribute("collegiateMeetings", collegiateMeetings);
            model.addAttribute("filterMeetingStatus", meetingStatus);

            List<Meeting> scheduledParticipations = meetingService.findScheduledMeetingsByParticipant(professor.getId());
            model.addAttribute("scheduledMeetings", scheduledParticipations);
            model.addAttribute("mainContent", "pages/dashboard-professor :: content");

        } else if (roles.contains("ROLE_STUDENT")) {
            Student student = studentService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado."));
            model.addAttribute("student", student);
            model.addAttribute("processes", processService.listByStudentFiltered(student.getId(), status, null));
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("mainContent", "pages/dashboard-student :: content");
            model.addAttribute("filterStatus", status);

        } else {
            return "redirect:/login?error";
        }

        return "home";
    }

    /**
     * API: Retorna processos elegíveis para pauta (UNDER_ANALISYS com relator que votou)
     */
    @GetMapping("/api/eligible-processes")
    public ResponseEntity<List<Map<String, Object>>> getEligibleProcesses(Authentication authentication) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                return ResponseEntity.status(403).build();
            }

            List<Process> eligible = processService.findAllProcesses().stream()
                    .filter(p -> p.getStatus() == StatusProcess.UNDER_ANALISYS)
                    .filter(p -> p.getRapporteurVote() != null)
                    .collect(Collectors.toList());

            List<Map<String, Object>> result = eligible.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("number", p.getNumber());
                map.put("title", p.getTitle());
                map.put("status", p.getStatus().toString());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * API: Retorna lista de professores
     */
    @GetMapping("/api/professors")
    public ResponseEntity<List<Map<String, Object>>> getProfessors(Authentication authentication) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                return ResponseEntity.status(403).build();
            }

            List<Map<String, Object>> result = professorService.findAll().stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("login", p.getLogin());
                map.put("coordinator", p.getCoordinator());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * POST: Criar nova reunião
     */
    @PostMapping("/create-meeting")
    public String createMeeting(Authentication authentication,
                               @RequestParam String scheduledDate,
                               @RequestParam(required = false) List<Long> processIds,
                               @RequestParam(required = false) List<Long> participantIds,
                               RedirectAttributes redirectAttributes) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
                return "redirect:/dashboard";
            }

            Professor coordinator = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado."));

            // Parse da data
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime scheduledDateTime = LocalDateTime.parse(scheduledDate, formatter);

            // Busca o colegiado do coordenador
            Collegiate collegiate = collegiateService.findByProfessorId(coordinator.getId());

            // Validação de processos e participantes
            if (processIds == null || processIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selecione ao menos um processo.");
                return "redirect:/dashboard";
            }

            if (participantIds == null || participantIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selecione ao menos um participante.");
                return "redirect:/dashboard";
            }

            // Cria a reunião
            Meeting meeting = meetingService.createMeetingWithAgenda(
                    collegiate.getId(),
                    scheduledDateTime,
                    processIds,
                    participantIds
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                "Reunião criada com sucesso! ID: " + meeting.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erro ao criar reunião: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    /**
     * Método auxiliar para buscar reuniões do colegiado do professor (REQFUNC 4).
     */
    private List<Meeting> findCollegiateMeetingsForProfessor(Long professorId, String meetingStatus) {
        try {
            Collegiate collegiate = collegiateService.findByProfessorId(professorId);

            if (meetingStatus != null && !meetingStatus.isBlank()) {
                MeetingStatus statusEnum = MeetingStatus.valueOf(meetingStatus);
                return meetingService.findByCollegiateIdAndStatus(collegiate.getId(), statusEnum);
            } else {
                return meetingService.findByCollegiateId(collegiate.getId());
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * POST: Atribuir processo a professor
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
