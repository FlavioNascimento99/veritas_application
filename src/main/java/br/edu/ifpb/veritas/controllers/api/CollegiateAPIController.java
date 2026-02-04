package br.edu.ifpb.veritas.controllers.api;

import br.edu.ifpb.veritas.models.Collegiate;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.services.CollegiateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collegiates")
public class CollegiateAPIController {
    private final CollegiateService collegiateService;

    @PostMapping
    public ResponseEntity<Collegiate> create(@Valid @RequestBody Collegiate collegiate) {
        return ResponseEntity.ok(collegiateService.create(collegiate));
    }

    @GetMapping
    public ResponseEntity<List<Collegiate>> findAll() {
        return ResponseEntity.ok(collegiateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collegiate> findById(@PathVariable Long id) {
        return ResponseEntity.ok(collegiateService.findById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Collegiate> update(@PathVariable Long id, @RequestBody Collegiate collegiate) {
        return ResponseEntity.ok(collegiateService.update(id, collegiate));
    }

    // Retorna o colegiado associado a um representante estudantil específico
    @GetMapping("/representatives/{studentId}")
    public ResponseEntity<Collegiate> findByRepresentative(@PathVariable Long studentId) {
        return ResponseEntity.ok(collegiateService.findByRepresentativeStudent(studentId));
    }

    // Retorna o colegiado associado a uma reunião específica
    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<Collegiate> findByMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(collegiateService.findByMeeting(meetingId));
    }

    // Retorna a lista de professores membros de um colegiado específico
    @GetMapping("/{id}/professors")
    public ResponseEntity<List<Professor>> findProfessors(@PathVariable Long id) {
        return ResponseEntity.ok(collegiateService.findProfessorsByCollegiate(id));
    }
}
