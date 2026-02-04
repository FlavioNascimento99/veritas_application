package br.edu.ifpb.veritas.repositories;

import br.edu.ifpb.veritas.models.Collegiate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegiateRepository extends JpaRepository<Collegiate, Long> {
    // Optional<Collegiate> findByRepresentativeStudentId(Long studentId);
    // Optional<Collegiate> findByCollegiateMeetingsListId(Long meetingId);
    Optional<Collegiate> findByCollegiateMemberListId(Long professorId);
}