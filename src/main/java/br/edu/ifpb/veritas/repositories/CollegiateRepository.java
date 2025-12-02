package br.edu.ifpb.veritas.repositories;

import br.edu.ifpb.veritas.models.Collegiate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegiateRepository extends JpaRepository<Collegiate, Long> {
    Optional<Collegiate> findByCourse(String course);

    // Busca o colegiado pelo ID do representante estudantil
    Optional<Collegiate> findByRepresentativeStudentId(Long representativeStudentId);

    // Busca o colegiado pelo ID de uma reunião associada
    Optional<Collegiate> findByMeetingsId(Long meetingId);
}
