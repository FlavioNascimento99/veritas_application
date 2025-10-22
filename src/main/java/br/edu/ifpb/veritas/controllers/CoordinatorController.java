package br.edu.ifpb.veritas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ifpb.veritas.services.AcademicCaseService;
import br.edu.ifpb.veritas.services.BoardMeetingService;
import br.edu.ifpb.veritas.services.dto.AcademicCaseDTO;
import br.edu.ifpb.veritas.services.mapper.DTOMapper;
import org.springframework.ui.Model;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/coordinator")
public class CoordinatorController {

    private final AcademicCaseService academicCaseService;
    private final BoardMeetingService boardMeetingService;

    @Autowired
    public CoordinatorController(AcademicCaseService academicCaseService, BoardMeetingService boardMeetingService) {
        this.academicCaseService = academicCaseService;
        this.boardMeetingService = boardMeetingService;
    }

    // REQFUNC 7: Consultar todos os processos do colegiado
    @GetMapping("/cases")
    public String listAllCases(Model model) {
        // Recupera casos via serviço e converte para DTOs para a view
        var cases = academicCaseService.findAll();
        var dtos = cases.stream().map(DTOMapper::toAcademicCaseDTO).collect(Collectors.toList());
        model.addAttribute("cases", dtos);
        return "coordinator/all-cases";
    }

    // REQFUNC 8: Distribuir um processo para um professor
    @PostMapping("/cases/{id}/distribute")
    public String distributeCase(@PathVariable Long id) {
        // Lógica para atribuir um professor relator a um processo
        return "redirect:/coordinator/cases";
    }

    // REQFUNC 9: Criar uma sessão do colegiado
    @GetMapping("/meetings/new")
    public String showNewMeetingForm() {
        // Exibe o formulário para criar uma nova reunião (data, membros)
        return "coordinator/new-meeting-form";
    }

    @PostMapping("/meetings")
    public String createMeeting() {
        // Salva a nova reunião e redireciona para a página de gerenciamento da pauta
        // Long newMeetingId = ...
        Long newMeetingId = 1L; // Placeholder
        return "redirect:/coordinator/meetings/" + newMeetingId + "/agenda";
    }

    // REQFUNC 9: Definir pauta da reunião
    @GetMapping("/meetings/{id}/agenda")
    public String manageAgenda(@PathVariable Long id) {
        // Página para adicionar/remover processos da pauta da reunião
        return "coordinator/manage-agenda";
    }

    @PostMapping("/meetings/{id}/agenda")
    public String updateAgenda(@PathVariable Long id) {
        // Salva as alterações na pauta
        return "redirect:/coordinator/meetings";
    }

    // REQFUNC 10: Iniciar uma sessão de julgamento
    @PostMapping("/meetings/{id}/start")
    public String startMeeting(@PathVariable Long id) {
        // Altera o status da reunião para "EM ANDAMENTO"
        // Apenas uma sessão pode estar iniciada por vez (requer validação)
        return "redirect:/coordinator/meetings/" + id + "/session";
    }

    // REQFUNC 11: Conduzir a sessão (apregoar, registrar votos)
    @GetMapping("/meetings/{id}/session")
    public String conductMeetingSession(@PathVariable Long id) {
        // Tela principal da condução da reunião, mostrando a pauta,
        // o processo em julgamento e os votos
        return "coordinator/meeting-session";
    }

    // REQFUNC 5 & 11: Votar e registrar votos
    @PostMapping("/meetings/{meetingId}/cases/{caseId}/vote")
    public String recordVote(@PathVariable Long meetingId, @PathVariable Long caseId) {
        // Lógica para o coordenador registrar o voto de cada membro (incluindo o do relator)
        // O sistema calcula o resultado automaticamente
        return "redirect:/coordinator/meetings/" + meetingId + "/session";
    }

    // REQFUNC 12: Finalizar uma sessão
    @PostMapping("/meetings/{id}/finish")
    public String finishMeeting(@PathVariable Long id) {
        // Altera o status da reunião para "FINALIZADA" e bloqueia alterações
        return "redirect:/coordinator/meetings";
    }
}
