package br.edu.ifpb.veritas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifpb.veritas.models.CourseCoordinator;

@Repository
public interface CourseCoordinatorRepository extends JpaRepository<CourseCoordinator, Long> {
}
