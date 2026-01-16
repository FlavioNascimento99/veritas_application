package br.edu.ifpb.veritas.repositories;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.models.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByStatus(MeetingStatus status);

    // Busca todas as reuniões associadas a um colegiado específico
    List<Meeting> findByCollegiateId(Long collegiateId);

    // Filtra reuniões do colegiado por status
    List<Meeting> findByCollegiateIdAndStatus(Long collegiateId, MeetingStatus status);

    // Busca reuniões onde um determinado professor está escalado como participante
    List<Meeting> findByParticipantsId(Long professorId);

    // Busca reuniões AGENDADAS onde o professor está escalado
    List<Meeting> findByParticipantsIdAndStatus(Long professorId, MeetingStatus status);

    // Busca reuniões de um colegiado onde o professor está escalado
    List<Meeting> findByCollegiateIdAndParticipantsId(Long collegiateId, Long professorId);
}
