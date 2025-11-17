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
import br.edu.ifpb.veritas.repositories.ProcessRepository;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import br.edu.ifpb.veritas.repositories.SubjectRepository;
import br.edu.ifpb.veritas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Process create(Process process, Long studentId) {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado."));
        if (!(user instanceof Student)) {
            throw new ResourceNotFoundException("Usuário não é um estudante.");
        }
        Student student = (Student) user;

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

        var user = userRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));
        if (!(user instanceof Professor)) {
            throw new ResourceNotFoundException("Usuário não é um professor.");
        }
        Professor professor = (Professor) user;

        process.setProfessor(professor);
        process.setStatus(StatusProcess.UNDER_ANALISYS);
        process.setDistributedAt(LocalDateTime.now());

        return processRepository.save(process);
    }
}