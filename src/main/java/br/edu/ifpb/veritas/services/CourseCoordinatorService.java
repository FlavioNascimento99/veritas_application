package br.edu.ifpb.veritas.services;

import java.util.List;
import br.edu.ifpb.veritas.models.CourseCoordinator;

public interface CourseCoordinatorService {
    CourseCoordinator create(CourseCoordinator coordinator);
    CourseCoordinator update(Long id, CourseCoordinator coordinator);
    void delete(Long id);
    CourseCoordinator findById(Long id);
    List<CourseCoordinator> findAll();
}
