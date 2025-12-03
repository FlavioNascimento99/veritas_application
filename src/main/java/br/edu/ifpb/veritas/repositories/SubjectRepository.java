package br.edu.ifpb.veritas.repositories;

import br.edu.ifpb.veritas.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Use the JpaRepository-provided findAll() and findById(id) signatures
    List<Subject> findAll();
    Optional<Subject> findById(Long id);
    Optional<Subject> findByTitle(String title);
    List<Subject> findByActive(Boolean active);
}
