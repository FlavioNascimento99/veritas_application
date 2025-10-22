package br.edu.ifpb.veritas.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.ifpb.veritas.models.AcademicCase;
import br.edu.ifpb.veritas.repositories.AcademicCaseRepository;
import br.edu.ifpb.veritas.services.AcademicCaseService;
import br.edu.ifpb.veritas.services.exceptions.ResourceNotFoundException;

@Service
public class AcademicCaseServiceImpl implements AcademicCaseService {

    @Autowired
    private AcademicCaseRepository academicCaseRepository;

    @Override
    public AcademicCase create(AcademicCase academicCase) {
        return academicCaseRepository.save(academicCase);
    }

    @Override
    public AcademicCase update(Long id, AcademicCase academicCase) {
        AcademicCase existing = academicCaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AcademicCase not found"));
        existing.setSubject(academicCase.getSubject());
        existing.setDescription(academicCase.getDescription());
        existing.setCreationDate(academicCase.getCreationDate());
        existing.setAuthor(academicCase.getAuthor());
        existing.setRapporteur(academicCase.getRapporteur());
        existing.setBoardMeeting(academicCase.getBoardMeeting());
        return academicCaseRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        AcademicCase existing = academicCaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AcademicCase not found"));
        academicCaseRepository.delete(existing);
    }

    @Override
    public AcademicCase findById(Long id) {
        return academicCaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AcademicCase not found"));
    }

    @Override
    public List<AcademicCase> findAll() {
        return academicCaseRepository.findAll();
    }
}
