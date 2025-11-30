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
}
