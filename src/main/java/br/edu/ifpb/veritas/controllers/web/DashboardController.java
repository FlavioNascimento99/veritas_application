package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.models.Collegiate;
import br.edu.ifpb.veritas.models.Meeting;
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

import java.util.Collections;
import java.util.List;

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

        } else if (roles.contains("ROLE_STUDENT")) {
            Student student = studentService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado."));
            model.addAttribute("student", student);
            model.addAttribute("processes", processService.listByStudentFiltered(student.getId(), status, null));
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("filterStatus", status);

        } else {
            return "redirect:/login?error";
        }

        if (roles.contains("ROLE_ADMIN")) return "pages/dashboard-admin";
        if (roles.contains("ROLE_COORDINATOR")) return "pages/dashboard-coordinator";
        if (roles.contains("ROLE_PROFESSOR")) return "pages/dashboard-professor";
        return "pages/dashboard-student";
    }

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

    @PostMapping("/assign")
    public String assignProfessorToProcess(@RequestParam("processId") Long processId,
                                           @RequestParam("professorId") Long professorId,
                                           RedirectAttributes redirectAttributes) {
        processService.distribute(processId, professorId);
        redirectAttributes.addFlashAttribute("successMessage", "Processo distribuído com sucesso.");
        return "redirect:/dashboard";
    }
}