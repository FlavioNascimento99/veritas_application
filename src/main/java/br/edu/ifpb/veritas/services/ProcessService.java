package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.DTOs.processDTO.ProcessCreateDTO;
import br.edu.ifpb.veritas.DTOs.processDTO.ProcessListDTO;
import br.edu.ifpb.veritas.DTOs.processDTO.ProcessResponseDTO;
import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.repositories.ProcessRepository;
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

    @Transactional
    public ProcessResponseDTO create(ProcessCreateDTO dto, Long studentId) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado."));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada."));

        Process process = new Process();
        process.setStudent(student);
        process.setSubject(subject);
        process.setTitle(dto.getTitle());
        process.setDescription(dto.getDescription());
        process.setStatus(StatusProcess.WAITING);
        process.setCreatedAt(LocalDateTime.now());

        Process savedProcess = processRepository.save(process);
        return new ProcessResponseDTO(savedProcess);
    }

    public List<ProcessListDTO> listByStudent(Long studentId) {
        return processRepository.findByStudentId(studentId).stream()
                .map(ProcessListDTO::new)
                .collect(Collectors.toList());
    }

    public List<ProcessListDTO> listByProfessor(Long professorId) {
        return processRepository.findByProfessorId(professorId).stream()
                .map(ProcessListDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProcessResponseDTO distribute(Long processId, Long professorId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        var user = userRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));

        if (!(user instanceof Professor)) {
            throw new ResourceNotFoundException("Usuário não é um professor.");
        }
        Professor professor = (Professor) user;

        // opcional: checar estado atual do processo
        if (process.getStatus() != StatusProcess.WAITING) {
            // lançar exceção ou registrar e continuar conforme regra de negócio
            // throw new IllegalStateException("Processo não está em estado passível de distribuição");
        }

        process.setProfessor(professor);
        process.setStatus(StatusProcess.UNDER_ANALISYS);
        process.setDistributedAt(LocalDateTime.now());

        Process updatedProcess = processRepository.save(process);
        return new ProcessResponseDTO(updatedProcess);
    }
}
