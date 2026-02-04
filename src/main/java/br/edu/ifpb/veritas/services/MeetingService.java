package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.enums.StatusProcess;
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

    /**
     * REQFUNC 12: Impede alteração de reunião finalizada
     */
    @Transactional
    public Meeting update(Long id, Meeting payload) {
        Meeting currentMeeting = findById(id);

        // Impede alteração de reunião finalizada
        if (currentMeeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível alterar uma reunião finalizada.");
        }

        currentMeeting.setCollegiate(payload.getCollegiate());
        currentMeeting.setProcesses(payload.getProcesses());
        currentMeeting.setScheduledDate(payload.getScheduledDate());
        currentMeeting.setStatus(payload.getStatus());

        return meetingRepository.save(currentMeeting);
    }

    @Transactional
    public Meeting updateStatus(Long id, MeetingStatus status) {
        Meeting meeting = findById(id);

        // Impede alteração de status de reunião já finalizada
        if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível alterar o status de uma reunião já finalizada.");
        }

        meeting.setStatus(status);
        return meetingRepository.save(meeting);
    }

    public List<Meeting> findByStatus(MeetingStatus status) {
        return meetingRepository.findByStatus(status);
    }

    public List<Meeting> findByCollegiateId(Long collegiateId) {
        return meetingRepository.findByCollegiateId(collegiateId);
    }

    public List<Meeting> findByCollegiateIdAndStatus(Long collegiateId, MeetingStatus status) {
        return meetingRepository.findByCollegiateIdAndStatus(collegiateId, status);
    }

    public List<Meeting> findByParticipantId(Long professorId) {
        return meetingRepository.findByParticipantsId(professorId);
    }

    public List<Meeting> findScheduledMeetingsByParticipant(Long professorId) {
        return meetingRepository.findByParticipantsIdAndStatus(professorId, MeetingStatus.AGENDADA);
    }

    public List<Meeting> findByCollegiateAndParticipant(Long collegiateId, Long professorId) {
        return meetingRepository.findByCollegiateIdAndParticipantsId(collegiateId, professorId);
    }

    @Transactional
    public Meeting createMeetingWithAgenda(Long collegiateId,
                                           LocalDateTime scheduledDate,
                                           List<Long> processIds,
                                           List<Long> participantIds) {

        Collegiate collegiate = collegiateRepository.findById(collegiateId)
                .orElseThrow(() -> new ResourceNotFoundException("Colegiado não encontrado com ID: " + collegiateId));

        List<Process> processes = new ArrayList<>();
        if (processIds != null && !processIds.isEmpty()) {
            for (Long processId : processIds) {
                Process process = processRepository.findById(processId)
                        .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

                // VALIDAÇÃO ADICIONAL: Processo deve estar EM_ANALISE
                if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
                    throw new IllegalStateException("Apenas processos em análise podem ser adicionados à pauta. Processo ID " + processId + " está com status: " + process.getStatus().getStatus());
                }

                // VALIDAÇÃO ADICIONAL: Relator deve ter votado
                if (process.getRapporteurVote() == null) {
                    throw new IllegalStateException("Processo ID " + processId + " não pode ser adicionado à pauta pois o relator ainda não registrou sua decisão.");
                }

                processes.add(process);
            }
        }

        List<Professor> participants = new ArrayList<>();
        if (participantIds != null && !participantIds.isEmpty()) {
            for (Long professorId : participantIds) {
                Professor professor = professorRepository.findById(professorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

                if (!collegiate.getCollegiateMemberList().contains(professor)) {
                    throw new IllegalArgumentException("Professor ID " + professorId + " não pertence ao colegiado.");
                }

                participants.add(professor);
            }
        }

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

    @Transactional
    public Meeting addProcessesToAgenda(Long meetingId, List<Long> processIds) {
        Meeting meeting = findById(meetingId);

        if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível alterar a pauta de uma reunião finalizada.");
        }

        for (Long processId : processIds) {
            Process process = processRepository.findById(processId)
                    .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

            // VALIDAÇÃO: Processo deve estar EM_ANALISE
            if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
                throw new IllegalStateException("Apenas processos em análise podem ser adicionados à pauta. Status atual: " + process.getStatus().getStatus());
            }

            // VALIDAÇÃO: Relator deve ter votado
            if (process.getRapporteurVote() == null) {
                throw new IllegalStateException("O relator do processo (ID: " + processId + ") ainda não registrou sua decisão.");
            }

            if (!meeting.getProcesses().contains(process)) {
                meeting.getProcesses().add(process);
            }
        }
        return meetingRepository.save(meeting);
    }

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

    @Transactional
    public Meeting startMeeting(Long meetingId) {
        meetingRepository.findByActiveTrue().ifPresent(activeMeeting -> {
            throw new IllegalStateException("Já existe uma reunião ativa (ID: " + activeMeeting.getId() + "). Finalize-a antes de iniciar outra.");
        });

        Meeting meeting = findById(meetingId);

        if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
            throw new IllegalStateException("Não é possível iniciar uma reunião já finalizada.");
        }

        meeting.setActive(true);
        meeting.setStatus(MeetingStatus.AGENDADA);

        return meetingRepository.save(meeting);
    }

    /**
     * REQFUNC 12: Finaliza uma reunião, travando todas as alterações.
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

    public Meeting findActiveMeeting() {
        return meetingRepository.findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma reunião está ativa no momento."));
    }
}