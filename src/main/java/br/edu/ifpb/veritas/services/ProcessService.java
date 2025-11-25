package br.edu.ifpb.veritas.services;

//import br.edu.ifpb.veritas.DTOs.processDTO.ProcessCreateDTO;
//import br.edu.ifpb.veritas.DTOs.processDTO.ProcessListDTO;
//import br.edu.ifpb.veritas.DTOs.processDTO.ProcessResponseDTO;
import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    // REQFUNC 1?
    @Transactional
    public Process create(Process process, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado."));

        if (process.getSubject() == null || process.getSubject().getId() == null) {
            throw new ResourceNotFoundException("Disciplina não informada.");
        }
        Subject subject = subjectRepository.findById(process.getSubject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada."));

        Process newProcess = new Process();
        newProcess.setStudent(student);
        newProcess.setSubject(subject);
        newProcess.setTitle(process.getTitle());
        newProcess.setDescription(process.getDescription());
        newProcess.setStatus(StatusProcess.WAITING);
        newProcess.setCreatedAt(LocalDateTime.now());

        return processRepository.save(newProcess);
    }

    public List<Process> listByStudent(Long studentId) {
        return processRepository.findByStudentId(studentId);
    }

    public List<Process> listByProfessor(Long professorId) {
        return processRepository.findByProfessorId(professorId);
    }

    @Transactional
    public Process distribute(Long processId, Long professorId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));

        process.setProfessor(professor);
        process.setStatus(StatusProcess.UNDER_ANALISYS);
        process.setDistributedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    // Estava me questionando se faz
    // mais sentido deixar aqui mesmo
    // ou em StudentService
    public List<Process> listByStudentFiltered(Long studentId, String status, Long subjectId, String sort) {
        List<Process> list = processRepository.findByStudentId(studentId);

        if (status != null && !status.isBlank()) {
            final StatusProcess statusEnum;
            try {
                statusEnum = StatusProcess.valueOf(status.trim());
            } catch (IllegalArgumentException ex) {
                throw new ResourceNotFoundException("Status inválido.");
            }
            list = list.stream()
                    .filter(p -> p.getStatus() == statusEnum)
                    .collect(Collectors.toList());
        }

        if (subjectId != null) {
            list = list.stream()
                    .filter(p -> p.getSubject() != null && Objects.equals(p.getSubject().getId(), subjectId))
                    .collect(Collectors.toList());
        }

        Comparator<Process> comp = Comparator.comparing(Process::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        if (sort != null && sort.equalsIgnoreCase("asc")) {
            list = list.stream().sorted(comp).collect(Collectors.toList());
        } else {
            // padrão desc
            list = list.stream().sorted(comp.reversed()).collect(Collectors.toList());
        }

        return list;
    }

}