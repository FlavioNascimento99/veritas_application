package br.edu.ifpb.veritas.repositories;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.models.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    // Busca reunião ativa no momento (lembrando que apenas uma pode estar ativa)
    Optional<Meeting> findByActiveTrue();

    // REQFUNC 9: Busca reuniões de um colegiado ordenadas por data agendada
    List<Meeting> findByCollegiateIdOrderByScheduledDateDesc(Long collegiateId);

    // Busca reuniões que contenham um processo específico na pauta
    @Query("SELECT m FROM Meeting m JOIN m.processes p WHERE p.id = :processId")
    List<Meeting> findByProcessInAgenda(@Param("processId") Long processId);
}
