package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    @Transactional
    public Professor create(Professor professor) {
        if (professor.getLogin() != null && professorRepository.findByLogin(professor.getLogin()).isPresent()) {
            throw new ResourceNotFoundException("Login já cadastrado.");
        }
        if (professor.getRegister() != null && professorRepository.findByRegister(professor.getRegister()).isPresent()) {
            throw new ResourceNotFoundException("Matrícula já cadastrada.");
        }
        return professorRepository.save(professor);
    }

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    public Professor findById(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));
    }

    @Transactional
    public Professor update(Long id, Professor payload) {
        Professor currentProfessor = findById(id);
        currentProfessor.setName(payload.getName());
        currentProfessor.setPhoneNumber(payload.getPhoneNumber());
        currentProfessor.setLogin(payload.getLogin());
        currentProfessor.setPassword(payload.getPassword());
        currentProfessor.setRegister(payload.getRegister());
        currentProfessor.setCoordinator(payload.isCoordinator());
        return professorRepository.save(currentProfessor);
    }

    @Transactional
    public void delete(Long id) {
        Professor currentProfessor = findById(id);
        professorRepository.delete(currentProfessor);
    }
}
