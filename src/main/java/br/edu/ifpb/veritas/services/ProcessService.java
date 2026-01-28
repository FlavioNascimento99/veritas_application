package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import br.edu.ifpb.veritas.repositories.ProcessRepository;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import br.edu.ifpb.veritas.repositories.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;

    @Transactional
    public Process createProcess(Process process, Long studentId, Long subjectId) {
        if (studentId == null) {
            throw new IllegalArgumentException("O ID do estudante não pode ser nulo.");
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com o ID: " + studentId));

        if (subjectId == null) {
            throw new IllegalArgumentException("O ID do assunto não pode ser nulo.");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o ID: " + subjectId));

        process.setProcessCreator(student);
        process.setSubject(subject);
        process.setCreatedAt(LocalDateTime.now());
        process.setStatus(StatusProcess.WAITING);
        process.setNumber(generateProcessNumber());

        return processRepository.save(process);
    }

    public List<Process> listByStudent(Long studentId) {
        return processRepository.findByProcessCreator_Id(studentId);
    }

    public List<Process> findAllProcesses() {
        return processRepository.findAll();
    }

    public Process findById(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));
    }

    /**
     * Retorna processos filtrados por status, estudante e/ou professor (relator).
     * Todos os parâmetros são opcionais; quando nulos, não participam do filtro.
     */
    public List<Process> findAllFiltered(String status, Long studentId, Long professorId) {
        Specification<Process> spec = Specification.where(null);

        if (status != null && !status.isBlank()) {
            try {
                StatusProcess statusEnum = StatusProcess.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // ignore invalid status values and return no additional filter
            }
        }

        if (studentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("processCreator").get("id"), studentId));
        }

        if (professorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("processRapporteur").get("id"), professorId));
        }

        if (spec == null) {
            return processRepository.findAll();
        }

        return processRepository.findAll(spec);
    }

    public List<Process> findWaitingProcesses() {
        return processRepository.findByStatus(StatusProcess.WAITING);
    }

    public List<Process> listByProfessor(Long professorId) {
        return processRepository.findByProcessRapporteur_Id(professorId);
    }

    public List<Process> listByStudentFiltered(Long studentId, String status, Long subjectId) {
        if (studentId == null) {
            throw new IllegalArgumentException("O ID do estudante não pode ser nulo para filtrar os processos.");
        }
        // Garante que o estudante existe antes de prosseguir
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com o ID: " + studentId));

        Specification<Process> spec = Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("processCreator").get("id"), studentId));

        if (status != null && !status.isBlank()) {
            try {
                StatusProcess statusEnum = StatusProcess.valueOf(status.toUpperCase());
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido: " + status);
            }
        }

        if (subjectId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("subject").get("id"), subjectId));
        }

        return processRepository.findAll(spec);
    }

    /**
     * Impede redistribuição de processos já julgados
     */
    @Transactional
    public Process distribute(Long processId, Long professorId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

        // Impede redistribuição de processo já finalizado
        if (process.getStatus() == StatusProcess.APPROVED || process.getStatus() == StatusProcess.REJECTED) {
            throw new IllegalStateException("Não é possível redistribuir um processo já finalizado.");
        }

        // Impede redistribuição de processo em análise
        if (process.getStatus() == StatusProcess.UNDER_ANALISYS) {
            throw new IllegalStateException("Este processo já foi distribuído para um relator e está em análise. Não é possível redistribuí-lo.");
        }

        // Valida se o processo está aguardando distribuição
        if (process.getStatus() != StatusProcess.WAITING) {
            throw new IllegalStateException("O processo não pode ser distribuído pois seu status é: " + process.getStatus());
        }

        process.setProcessRapporteur(professor);
        process.setStatus(StatusProcess.UNDER_ANALISYS);
        process.setDistributedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    private String generateProcessNumber() {
        // Use the current year and a 5-digit sequence derived from the current time
        int seq = (int) (System.currentTimeMillis() % 100000);
        return String.format("%d-%05d", Year.now().getValue(), seq);
    }

    // Busca processos finalizados (aprovados ou rejeitados)
    public List<Process> findFinalizedProcesses() {
        return processRepository.findByStatusIn(List.of(StatusProcess.APPROVED, StatusProcess.REJECTED));
    }

    // Verifica se um processo pode ser votado
    public boolean canBeVoted(Long processId) {
        Process process = findById(processId);
        return process.getStatus() == StatusProcess.UNDER_ANALISYS
                && process.getProcessRapporteur() != null;
    }
}