package br.edu.ifpb.veritas.services;

import java.util.List;
import br.edu.ifpb.veritas.models.Professor;

public interface ProfessorService {
    Professor create(Professor professor);
    Professor update(Long id, Professor professor);
    void delete(Long id);
    Professor findById(Long id);
    List<Professor> findAll();
}
