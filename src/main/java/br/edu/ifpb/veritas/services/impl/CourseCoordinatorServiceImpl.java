package br.edu.ifpb.veritas.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.ifpb.veritas.models.CourseCoordinator;
    import br.edu.ifpb.veritas.repositories.CourseCoordinatorRepository;
import br.edu.ifpb.veritas.services.CourseCoordinatorService;
import br.edu.ifpb.veritas.services.exceptions.ResourceNotFoundException;

@Service
public class CourseCoordinatorServiceImpl implements CourseCoordinatorService {

    @Autowired
    private CourseCoordinatorRepository courseCoordinatorRepository;

    @Override
    public CourseCoordinator create(CourseCoordinator coordinator) {
    return courseCoordinatorRepository.save(coordinator);
    }

    @Override
    public CourseCoordinator update(Long id, CourseCoordinator coordinator) {
    CourseCoordinator existing = courseCoordinatorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));
        existing.setName(coordinator.getName());
        existing.setEmail(coordinator.getEmail());
        existing.setPassword(coordinator.getPassword());
        existing.setDepartment(coordinator.getDepartment());
    return courseCoordinatorRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
    CourseCoordinator existing = courseCoordinatorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));
    courseCoordinatorRepository.delete(existing);
    }

    @Override
    public CourseCoordinator findById(Long id) {
    return courseCoordinatorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));
    }

    @Override
    public List<CourseCoordinator> findAll() {
        return courseCoordinatorRepository.findAll();
    }
}
