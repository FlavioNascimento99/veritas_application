package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.repositories.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;


    /**
     * Consutáveis
     * 
     * Métodos que apenas buscam dados em Banco, sem necessidade 
     * alreação me Banco
     */
    public List<Subject> listSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findByTittle(String tittle) {
        return subjectRepository.findByTitle(tittle);
    }



    /**
     * Transacionais 
     * 
     * Métodos que necessitam operações de transações em Banco.
     */
    @Transactional
    public Subject createSubject(Subject subject) {
        if (subjectRepository.findById(subject.getId()).isPresent()) {
            throw new ResourceNotFoundException("Disciplina já cadastrada.");
        };
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject update(Long id, Subject updatedSubject) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada."));
        subject.setTitle(updatedSubject.getTitle());
        subject.setDescription(updatedSubject.getDescription());
        subject.setModifiedAt(LocalDateTime.now());

        return subjectRepository.save(subject);
    }

    @Transactional
    public void deactivate(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não fora encontrado."));
        subject.setActive(false);
        subject.setModifiedAt(LocalDateTime.now());
        subjectRepository.save(subject);
    }

    @Transactional
    public void reactivate(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Assunto não fora encontrado"
                ));
        subject.setActive(true);
        subject.setModifiedAt(LocalDateTime.now());
        subjectRepository.save(subject);
    }
}