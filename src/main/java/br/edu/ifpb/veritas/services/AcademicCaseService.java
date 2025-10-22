package br.edu.ifpb.veritas.services;

import java.util.List;
import br.edu.ifpb.veritas.models.AcademicCase;

public interface AcademicCaseService {
    AcademicCase create(AcademicCase academicCase);
    AcademicCase update(Long id, AcademicCase academicCase);
    void delete(Long id);
    AcademicCase findById(Long id);
    List<AcademicCase> findAll();
}
