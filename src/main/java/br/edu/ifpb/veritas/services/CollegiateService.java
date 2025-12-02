package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Collegiate;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.CollegiateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CollegiateService {

    private final CollegiateRepository collegiateRepository;

    @Transactional
    public Collegiate create(Collegiate collegiate) {
        // Seta a data de criação se não estiver definida
        if (collegiate.getCreatedAt() == null) {
            collegiate.setCreatedAt(LocalDateTime.now());
        }
        // Colocar validações

        return collegiateRepository.save(collegiate);
    }

    public List<Collegiate> findAll() {
        return collegiateRepository.findAll();
    }

    public Collegiate findById(Long id) {
        return collegiateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colegiado não encontrado."));
    }

    @Transactional
    public Collegiate update(Long id, Collegiate payload) {
        Collegiate current = findById(id);
        // Colocar validações

        current.setCreatedAt(payload.getCreatedAt());
        current.setEndedAt(payload.getEndedAt());
        current.setDescription(payload.getDescription());
        current.setResolution(payload.getResolution());
        current.setCourse(payload.getCourse());
        current.setMembers(payload.getMembers());
        current.setMeetings(payload.getMeetings());
        current.setRepresentativeStudent(payload.getRepresentativeStudent());
        return collegiateRepository.save(current);
    }

    // Retorna o colegiado associado a um estudante representante específico
    public Collegiate findByRepresentativeStudent(Long studentId) {
        return collegiateRepository.findByRepresentativeStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Colegiado com o representante informado não encontrado."));
    }

    // Retorna o colegiado associado a uma reunião específica
    public Collegiate findByMeeting(Long meetingId) {
        return collegiateRepository.findByMeetingsId(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Colegiado associado à reunião informado não encontrado."));
    }

    // Retorna a lista de professores membros de um determinado colegiado
    public List<Professor> findProfessorsByCollegiate(Long id) {
        Collegiate collegiate = findById(id);
        return List.copyOf(collegiate.getMembers());
    }

    @Transactional
    public void delete(Long id) {
        Collegiate current = findById(id);
        collegiateRepository.delete(current);
    }
}
