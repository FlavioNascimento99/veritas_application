package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Collegiate;
import br.edu.ifpb.veritas.models.Meeting;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.CollegiateRepository;
import br.edu.ifpb.veritas.repositories.MeetingRepository;
import br.edu.ifpb.veritas.repositories.ProcessRepository;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final CollegiateRepository collegiateRepository;
    private final ProcessRepository processRepository;
    private final ProfessorRepository professorRepository;

    @Transactional
    public Meeting create(Meeting meeting) {
        // Seta a data de criação se não estiver definida
        if (meeting.getCreatedAt() == null) {
            meeting.setCreatedAt(LocalDateTime.now());
        }
        return meetingRepository.save(meeting);
    }

    public List<Meeting> findAll() {
        return meetingRepository.findAll();
    }

    public Meeting findById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reunião não encontrada."));
    }

    @Transactional
    public Meeting update(Long id, Meeting payload) {
        Meeting currentMeeting = findById(id);
        currentMeeting.setCollegiate(payload.getCollegiate());
        currentMeeting.setProcesses(payload.getProcesses());
        currentMeeting.setStatus(payload.getStatus());
        return meetingRepository.save(currentMeeting);
    }

    @Transactional
    public Meeting updateStatus(Long id, MeetingStatus status) {
        Meeting meeting = findById(id);
        meeting.setStatus(status);
        return meetingRepository.save(meeting);
    }

    public List<Meeting> findByStatus(MeetingStatus status) {
        return meetingRepository.findByStatus(status);
    }

    // Retorna todas as reuniões associadas a um colegiado específico
    public List<Meeting> findByCollegiateId(Long collegiateId) {
        return meetingRepository.findByCollegiateId(collegiateId);
    }

    // Filtra por colegiado e status
    public List<Meeting> findByCollegiateIdAndStatus(Long collegiateId, MeetingStatus status) {
        return meetingRepository.findByCollegiateIdAndStatus(collegiateId, status);
    }

    // Busca todas as reuniões onde o professor está escalado como participante
    public List<Meeting> findByParticipantId(Long professorId) {
        return meetingRepository.findByParticipantsId(professorId);
    }

    // Busca reuniões AGENDADAS onde o professor está escalado
    public List<Meeting> findScheduledMeetingsByParticipant(Long professorId) {
        return meetingRepository.findByParticipantsIdAndStatus(professorId, MeetingStatus. AGENDADA);
    }

    // Busca reuniões de um colegiado específico onde o professor está escalado
    public List<Meeting> findByCollegiateAndParticipant(Long collegiateId, Long professorId) {
        return meetingRepository.findByCollegiateIdAndParticipantsId(collegiateId, professorId);
    }

    // REQFUNC 9: Coordenador cria uma reunião definindo data, pauta e participantes
    @Transactional
    public Meeting createMeetingWithAgenda(Long collegiateId,
                                           LocalDateTime scheduledDate,
                                           List<Long> processIds,
                                           List<Long> participantIds) {

        // 1. Valida se o colegiado existe
        Collegiate collegiate = collegiateRepository.findById(collegiateId)
                .orElseThrow(() -> new ResourceNotFoundException("Colegiado não encontrado com ID: " + collegiateId));

        // 2. Valida e carrega os processos da pauta
        List<Process> processes = new ArrayList<>();
        if (processIds != null && !processIds.isEmpty()) {
            for (Long processId : processIds) {
                Process process = processRepository.findById(processId)
                        .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));
                processes.add(process);
            }
        }

        // 3. Valida e carrega os professores participantes
        List<Professor> participants = new ArrayList<>();
        if (participantIds != null && !participantIds.isEmpty()) {
            for (Long professorId : participantIds) {
                Professor professor = professorRepository.findById(professorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

                // Verifica se o professor pertence ao colegiado
                if (!collegiate.getCollegiateMemberList().contains(professor)) {
                    throw new IllegalArgumentException("Professor ID " + professorId + " não pertence ao colegiado.");
                }

                participants.add(professor);
            }
        }

        // 4. Cria a reunião
        Meeting meeting = new Meeting();
        meeting.setCollegiate(collegiate);
        meeting.setScheduledDate(scheduledDate);
        meeting.setProcesses(processes);
        meeting.setParticipants(participants);
        meeting.setCreatedAt(LocalDateTime.now());
        meeting.setStatus(MeetingStatus.AGENDADA);
        meeting.setActive(false);

        return meetingRepository.save(meeting);
    }

    // REQFUNC 9: Adiciona processos à pauta de uma reunião existente
    @Transactional
    public Meeting addProcessesToAgenda(Long meetingId, List<Long> processIds) {
        Meeting meeting = findById(meetingId);

        if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível alterar a pauta de uma reunião finalizada.");
        }

        for (Long processId : processIds) {
            Process process = processRepository.findById(processId)
                    .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

            if (!meeting.getProcesses().contains(process)) {
                meeting.getProcesses().add(process);
            }
        }
        return meetingRepository.save(meeting);
    }

    // REQFUNC 9: Adiciona participantes a uma reunião existente
    @Transactional
    public Meeting addParticipants(Long meetingId, List<Long> participantIds) {
        Meeting meeting = findById(meetingId);

        if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível alterar participantes de uma reunião finalizada.");
        }

        for (Long professorId : participantIds) {
            Professor professor = professorRepository.findById(professorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

            if (!meeting.getParticipants().contains(professor)) {
                meeting.getParticipants().add(professor);
            }
        }

        return meetingRepository.save(meeting);
    }

    /**
     * REQFUNC 10: Inicia uma reunião (apenas uma pode estar ativa por vez).
     */
    @Transactional
    public Meeting startMeeting(Long meetingId) {
        // Verifica se já existe reunião ativa
        meetingRepository.findByActiveTrue().ifPresent(activeMeeting -> {
            throw new IllegalStateException("Já existe uma reunião ativa (ID: " + activeMeeting.getId() + "). Finalize-a antes de iniciar outra.");
        });

        Meeting meeting = findById(meetingId);

        if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível iniciar uma reunião já finalizada.");
        }

        meeting.setActive(true);
        meeting.setStatus(MeetingStatus.AGENDADA); // Mantém como agendada enquanto está acontecendo

        return meetingRepository.save(meeting);
    }

    /**
     * REQFUNC 12: Finaliza uma reunião.
     */
    @Transactional
    public Meeting finalizeMeeting(Long meetingId) {
        Meeting meeting = findById(meetingId);

        if (!meeting.isActive()) {
            throw new IllegalStateException("Esta reunião não está ativa.");
        }

        meeting.setActive(false);
        meeting.setStatus(MeetingStatus.FINALIZADA);

        return meetingRepository.save(meeting);
    }

    /**
     * Busca a reunião atualmente ativa.
     */
    public Meeting findActiveMeeting() {
        return meetingRepository.findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma reunião está ativa no momento."));
    }
}