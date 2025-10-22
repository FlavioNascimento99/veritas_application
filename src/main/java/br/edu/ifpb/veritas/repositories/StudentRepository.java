package br.edu.ifpb.veritas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifpb.veritas.models.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
