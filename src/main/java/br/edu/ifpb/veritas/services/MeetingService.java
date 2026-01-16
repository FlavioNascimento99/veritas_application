package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Meeting;
import br.edu.ifpb.veritas.repositories.MeetingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

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
        return meetingRepository. findByParticipantsId(professorId);
    }

    // Busca reuniões AGENDADAS onde o professor está escalado
    public List<Meeting> findScheduledMeetingsByParticipant(Long professorId) {
        return meetingRepository.findByParticipantsIdAndStatus(professorId, MeetingStatus. AGENDADA);
    }

    // Busca reuniões de um colegiado específico onde o professor está escalado
    public List<Meeting> findByCollegiateAndParticipant(Long collegiateId, Long professorId) {
        return meetingRepository.findByCollegiateIdAndParticipantsId(collegiateId, professorId);
    }
}
