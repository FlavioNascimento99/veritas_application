package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.DTOs.subjectsDTO.SubjectDTO;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public Subject create(SubjectDTO subjectDTO) {
        Subject subject = new Subject();
        subject.setTitle(subjectDTO.getTitle());
        subject.setDescription(subjectDTO.getDescription());
        return subjectRepository.save(subject);
    }

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Subject reload(Long id, SubjectDTO subjectDTO) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada."));
        subject.setTitle(subjectDTO.getTitle());
        subject.setDescription(subjectDTO.getDescription());
        subject.setModifiedAt(LocalDateTime.now());
        return subjectRepository.save(subject);
    }

    public void deactivate(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada."));
        subject.setActive(false);
        subject.setModifiedAt(LocalDateTime.now());
        subjectRepository.save(subject);
    }
}
