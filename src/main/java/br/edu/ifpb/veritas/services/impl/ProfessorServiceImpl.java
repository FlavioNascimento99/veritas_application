package br.edu.ifpb.veritas.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import br.edu.ifpb.veritas.services.ProfessorService;
import br.edu.ifpb.veritas.services.exceptions.ResourceNotFoundException;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    public Professor create(Professor professor) {
        return professorRepository.save(professor);
    }

    @Override
    public Professor update(Long id, Professor professor) {
        Professor existing = professorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        existing.setName(professor.getName());
        existing.setEmail(professor.getEmail());
        existing.setPassword(professor.getPassword());
        existing.setDepartment(professor.getDepartment());
        return professorRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Professor existing = professorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        professorRepository.delete(existing);
    }

    @Override
    public Professor findById(Long id) {
        return professorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
    }

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }
}
