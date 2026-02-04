package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.enums.VoteType;
import br.edu.ifpb.veritas.models.Collegiate;
import br.edu.ifpb.veritas.models.Meeting;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final ProcessService processService;
    private final SubjectService subjectService;
    private final MeetingService meetingService;
    private final VoteService voteService;
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
                               @RequestParam(required = false) List<Long> processIds,
                               @RequestParam(required = false) List<Long> participantIds,
                               @RequestParam(required = false) String description,
                               RedirectAttributes redirectAttributes) {
        try {
            log.info("=== INICIANDO CRIAÇÃO DE REUNIÃO ===");
            log.info("Processos selecionados: {}", processIds);
            log.info("Participantes selecionados: {}", participantIds);
            log.info("Descrição fornecida: {}", description != null && !description.isEmpty() ? "Sim" : "Não (será gerada automaticamente)");

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                log.warn("Acesso negado: usuário não é COORDINATOR");
                redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
                return "redirect:/dashboard";
            }

            Professor coordinator = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("Coordenador não encontrado para login: {}", authentication.getName());
                        return new IllegalArgumentException("Coordenador não encontrado.");
                    });

            log.info("Coordenador autenticado: {} (ID: {})", coordinator.getName(), coordinator.getId());

            // Usar data/hora atual para a reunião
            LocalDateTime scheduledDateTime = LocalDateTime.now();

            // Busca o colegiado do coordenador
            Collegiate collegiate;
            try {
                collegiate = collegiateService.findByProfessorId(coordinator.getId());
                log.info("Colegiado encontrado: {} (ID: {})", collegiate.getDescription(), collegiate.getId());
            } catch (Exception e) {
                log.error("Erro ao buscar colegiado do coordenador: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Coordenador não pertence a nenhum colegiado. Entre em contato com um administrador.");
                return "redirect:/dashboard";
            }

            // Validação de processos e participantes
            if (processIds == null || processIds.isEmpty()) {
                log.warn("Erro: Nenhum processo selecionado");
                redirectAttributes.addFlashAttribute("errorMessage", "Selecione ao menos um processo.");
                return "redirect:/dashboard/meetings/new";
            }

            if (participantIds == null || participantIds.isEmpty()) {
                log.warn("Erro: Nenhum participante selecionado");
                redirectAttributes.addFlashAttribute("errorMessage", "Selecione ao menos um participante.");
                return "redirect:/dashboard/meetings/new";
            }

            log.info("Validações passadas. Criando reunião com {} processos e {} participantes", 
                    processIds.size(), participantIds.size());

            // Cria a reunião com descrição
            Meeting meeting = meetingService.createMeetingWithAgenda(
                    collegiate.getId(),
                    scheduledDateTime,
                    processIds,
                    participantIds,
                    description
            );

            log.info("=== REUNIÃO CRIADA COM SUCESSO ===");
            log.info("Meeting ID: {} | Status: {} | Abertura: {} | Agendada: {}", 
                meeting.getId(), meeting.getStatus(), meeting.getOpenedAt(), meeting.getScheduledDate());
            log.info("Processos: {} | Participantes: {}", 
                meeting.getProcesses() != null ? meeting.getProcesses().size() : 0,
                meeting.getParticipants() != null ? meeting.getParticipants().size() : 0);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Reunião criada com sucesso! ID: " + meeting.getId());
        } catch (IllegalStateException e) {
            log.error("Erro de validação ao criar reunião: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/meetings/new";
        } catch (IllegalArgumentException e) {
            log.error("Argumento inválido ao criar reunião: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/meetings/new";
        } catch (Exception e) {
            log.error("=== ERRO INESPERADO AO CRIAR REUNIÃO ===", e);
            log.error("Tipo de erro: {}", e.getClass().getSimpleName());
            log.error("Mensagem: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erro inesperado ao criar reunião: " + e.getMessage());
            return "redirect:/dashboard/meetings/new";
        }

        return "redirect:/dashboard/meetings";
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
     * GET: Página de Professores do Colegiado
     */
    @GetMapping("/professors")
    public String viewProfessors(Model model, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (!roles.contains("ROLE_COORDINATOR")) {
            return "redirect:/dashboard";
        }

        List<Professor> professors = professorService.findAll();
        model.addAttribute("professors", professors);
        model.addAttribute("mainContent", "pages/dashboard-coordinator-professors :: content");
        return "home";
    }

    /**
     * GET: Página de Reuniões do Colegiado
     */
    @GetMapping("/meetings")
    public String viewMeetings(Model model, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (!roles.contains("ROLE_COORDINATOR")) {
            return "redirect:/dashboard";
        }

        Professor professor = professorService.findByLogin(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado."));

        try {
            Collegiate collegiate = collegiateService.findByProfessorId(professor.getId());
            List<Meeting> collegiateMeetings = meetingService.findByCollegiateId(collegiate.getId());
            model.addAttribute("collegiateMeetings", collegiateMeetings);
        } catch (Exception e) {
            model.addAttribute("collegiateMeetings", Collections.emptyList());
        }

        model.addAttribute("mainContent", "pages/dashboard-coordinator-meetings :: content");
        return "home";
    }

    /**
     * GET: Página para criar nova reunião
     */
    @GetMapping("/meetings/new")
    public String newMeeting(Model model, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (!roles.contains("ROLE_COORDINATOR")) {
            return "redirect:/dashboard";
        }

        professorService.findByLogin(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado."));

        // Carregar processos elegíveis:
        // - UNDER_ANALISYS (em análise)
        // - rapporteurVote != null (relator votou)
        // - processRapporteur != null (tem relator designado)
        // - meeting == null (não estão em outras reuniões)
        List<Process> eligibleProcesses = processService.findAllProcesses().stream()
                .filter(p -> p.getStatus() == StatusProcess.UNDER_ANALISYS)
                .filter(p -> p.getRapporteurVote() != null)
                .filter(p -> p.getProcessRapporteur() != null)
                .filter(p -> p.getMeeting() == null)  // NÃO estar em outra reunião
                .toList();

        model.addAttribute("allProcesses", eligibleProcesses);
        model.addAttribute("professors", professorService.findAll());
        model.addAttribute("mainContent", "pages/dashboard-coordinator-new-meeting :: content");
        return "home";
    }

    /**
     * GET: Editar reunião existente (REQFUNC 9 - edit)
     * Apenas para reuniões em status DISPONÍVEL
     */
    @GetMapping("/meetings/{id}/edit")
    public String editMeeting(@PathVariable("id") Long meetingId,
                             Model model,
                             Authentication authentication) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                return "redirect:/dashboard";
            }

            Meeting meeting = meetingService.findById(meetingId);

            // Validar que a reunião está em DISPONÍVEL
            if (meeting.getStatus() != MeetingStatus.DISPONIVEL) {
                model.addAttribute("errorMessage", "Apenas reuniões em status DISPONÍVEL podem ser editadas.");
                model.addAttribute("mainContent", "pages/error :: content");
                return "home";
            }

            // Carregar processos elegíveis (os não vinculados a outras reuniões + os já na pauta)
            List<Process> allEligibleProcesses = processService.findAllProcesses().stream()
                    .filter(p -> p.getStatus() == StatusProcess.UNDER_ANALISYS)
                    .filter(p -> p.getRapporteurVote() != null)
                    .filter(p -> p.getProcessRapporteur() != null)
                    .filter(p -> p.getMeeting() == null || p.getMeeting().getId().equals(meetingId))
                    .toList();

            model.addAttribute("meeting", meeting);
            model.addAttribute("allProcesses", allEligibleProcesses);
            model.addAttribute("professors", professorService.findAll());
            model.addAttribute("mainContent", "pages/dashboard-coordinator-new-meeting :: content");
            return "home";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao editar reunião: " + e.getMessage());
            model.addAttribute("mainContent", "pages/error :: content");
            return "home";
        }
    }

    /**
     * POST: Atualizar reunião existente (REQFUNC 9 - update)
     */
    @PostMapping("/meetings/{id}")
    public String updateMeeting(@PathVariable("id") Long meetingId,
                               @RequestParam(value = "processIds", required = false) List<Long> processIds,
                               @RequestParam(value = "professorIds", required = false) List<Long> professorIds,
                               @RequestParam(value = "description", required = false) String description,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                return "redirect:/dashboard";
            }

            Meeting meeting = meetingService.findById(meetingId);

            // Validar que a reunião está em DISPONÍVEL
            if (meeting.getStatus() != MeetingStatus.DISPONIVEL) {
                redirectAttributes.addFlashAttribute("errorMessage", "Apenas reuniões em status DISPONÍVEL podem ser editadas.");
                return "redirect:/dashboard/meetings";
            }

            // Atualizar descrição se fornecida
            if (description != null && !description.isEmpty()) {
                meeting.setDescription(description);
            }

            // Atualizar processos se fornecidos
            if (processIds != null && !processIds.isEmpty()) {
                List<Process> updatedProcesses = new ArrayList<>();
                for (Long processId : processIds) {
                    Process process = processService.findById(processId);
                    

                    // Validar processo
                    if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
                        throw new IllegalStateException("Processo ID " + processId + " não está em análise.");
                    }
                    if (process.getRapporteurVote() == null) {
                        throw new IllegalStateException("Relator do processo ID " + processId + " ainda não votou.");
                    }

                    updatedProcesses.add(process);
                }
                meeting.setProcesses(updatedProcesses);
            }

            // Atualizar participantes se fornecidos
            if (professorIds != null && !professorIds.isEmpty()) {
                Collegiate collegiate = meeting.getCollegiate();
                List<Professor> updatedParticipants = new ArrayList<>();
                
                for (Long professorId : professorIds) {
                    Professor professor = professorService.findById(professorId);
                    
   

                    if (!collegiate.getCollegiateMemberList().contains(professor)) {
                        throw new IllegalArgumentException("Professor ID " + professorId + " não pertence ao colegiado.");
                    }

                    updatedParticipants.add(professor);
                }
                meeting.setParticipants(updatedParticipants);
            }

            // Salvar alterações
            meetingService.update(meetingId, meeting);

            redirectAttributes.addFlashAttribute("successMessage", "Reunião atualizada com sucesso!");
            return "redirect:/dashboard/meetings";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar reunião: " + e.getMessage());
            return "redirect:/dashboard/meetings";
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

    /**
     * POST: Iniciar reunião (REQFUNC 10)
     * Muda status para EM_ANDAMENTO e desabilita edição
     */
    @PostMapping("/meetings/{id}/start")
    public String startMeeting(@PathVariable("id") Long meetingId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Apenas coordenadores podem iniciar reuniões.");
                return "redirect:/dashboard/meetings";
            }

            meetingService.startMeeting(meetingId);
            redirectAttributes.addFlashAttribute("successMessage", "Reunião iniciada com sucesso! Status: EM ANDAMENTO");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao iniciar reunião: " + e.getMessage());
        }

        return "redirect:/dashboard/meetings";
    }

    /**
     * POST: Finalizar reunião (REQFUNC 12)
     * Muda status para FINALIZADA (apenas se todos processos apregoados)
     */
    @PostMapping("/meetings/{id}/finalize")
    public String finalizeMeeting(@PathVariable("id") Long meetingId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Apenas coordenadores podem finalizar reuniões.");
                return "redirect:/dashboard/meetings";
            }

            meetingService.finalizeMeeting(meetingId);
            redirectAttributes.addFlashAttribute("successMessage", "Reunião finalizada com sucesso! Todas as alterações foram travadas.");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao finalizar reunião: " + e.getMessage());
        }

        return "redirect:/dashboard/meetings";
    }

    /**
     * GET: Página de votação de membros para uma reunião ativa (REQFUNC 11)
     */
    @GetMapping("/meetings/{id}/vote")
    public String voteMeetingProcesses(@PathVariable("id") Long meetingId,
                                      Model model,
                                      Authentication authentication) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_PROFESSOR")) {
                return "redirect:/dashboard";
            }

            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));

            Meeting meeting = meetingService.findById(meetingId);

            // Valida se reunião está EM_ANDAMENTO
            if (meeting.getStatus() != MeetingStatus.EM_ANDAMENTO) {
                model.addAttribute("errorMessage", "Esta reunião não está em andamento.");
                model.addAttribute("mainContent", "pages/error :: content");
                return "home";
            }

            // Valida se professor é participante
            boolean isParticipant = meeting.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(professor.getId()));

            if (!isParticipant) {
                model.addAttribute("errorMessage", "Você não é participante desta reunião.");
                model.addAttribute("mainContent", "pages/error :: content");
                return "home";
            }

            model.addAttribute("meeting", meeting);
            model.addAttribute("professor", professor);
            model.addAttribute("mainContent", "pages/meeting-vote :: content");
            return "home";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar página de votação: " + e.getMessage());
            model.addAttribute("mainContent", "pages/error :: content");
            return "home";
        }
    }

    /**
     * POST: Registrar voto de membro em um processo da reunião (REQFUNC 11)
     */
    @PostMapping("/meetings/{meetingId}/processes/{processId}/vote")
    public String voteMeetingProcess(@PathVariable("meetingId") Long meetingId,
                                    @PathVariable("processId") Long processId,
                                    @RequestParam("voteType") VoteType voteType,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Professor professor = professorService.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));

            Meeting meeting = meetingService.findById(meetingId);

            // Valida se reunião está EM_ANDAMENTO
            if (meeting.getStatus() != MeetingStatus.EM_ANDAMENTO) {
                redirectAttributes.addFlashAttribute("errorMessage", "Esta reunião não está em andamento.");
                return "redirect:/dashboard/meetings/" + meetingId + "/vote";
            }

            // Valida se professor é participante
            boolean isParticipant = meeting.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(professor.getId()));

            if (!isParticipant) {
                redirectAttributes.addFlashAttribute("errorMessage", "Você não é participante desta reunião.");
                return "redirect:/dashboard/meetings/" + meetingId + "/vote";
            }

            // Registra o voto de membro
            voteService.registerMemberVote(processId, professor.getId(), voteType);
            redirectAttributes.addFlashAttribute("successMessage", "Voto registrado com sucesso!");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao registrar voto: " + e.getMessage());
        }

        return "redirect:/dashboard/meetings/" + meetingId + "/vote";
    }

    /**
     * GET: Página para apregoar processos (REQFUNC 11)
     * Exibe os processos da reunião ativa e permite apregoá-los
     */
    @GetMapping("/meetings/{id}/apregoar")
    public String announceProcesses(@PathVariable("id") Long meetingId,
                                   Model model,
                                   Authentication authentication) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                model.addAttribute("errorMessage", "Apenas coordenadores podem apregoar processos.");
                model.addAttribute("mainContent", "pages/error :: content");
                return "home";
            }

            Meeting meeting = meetingService.findById(meetingId);

            // Valida se reunião está EM_ANDAMENTO
            if (meeting.getStatus() != MeetingStatus.EM_ANDAMENTO) {
                model.addAttribute("errorMessage", "Esta reunião não está em andamento. Status: " + meeting.getStatus().getStatus());
                model.addAttribute("mainContent", "pages/error :: content");
                return "home";
            }

            model.addAttribute("meeting", meeting);
            model.addAttribute("mainContent", "pages/meeting-announce :: content");
            return "home";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar página de apregoação: " + e.getMessage());
            model.addAttribute("mainContent", "pages/error :: content");
            return "home";
        }
    }

    /**
     * POST: Apregoar um processo específico (REQFUNC 11)
     */
    @PostMapping("/meetings/{meetingId}/processes/{processId}/announce")
    public String announceProcess(@PathVariable("meetingId") Long meetingId,
                                 @PathVariable("processId") Long processId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (!roles.contains("ROLE_COORDINATOR")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Apenas coordenadores podem apregoar processos.");
                return "redirect:/dashboard/meetings";
            }

            voteService.announceProcess(processId);
            redirectAttributes.addFlashAttribute("successMessage", "Processo apregoado com sucesso!");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao apregoar processo: " + e.getMessage());
        }

        return "redirect:/dashboard/meetings/" + meetingId + "/apregoar";
    }
}


