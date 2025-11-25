package br.edu.ifpb.veritas.controllers.api;

import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.services.ProcessService;
import br.edu.ifpb.veritas.services.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/professors")
@RequiredArgsConstructor
public class ProfessorAPIController {

    private final ProfessorService professorService;
    private final ProcessService processService;

    @PostMapping
    public ResponseEntity<Professor> create(@Valid @RequestBody Professor professor) {
        professorService.create(professor);
        return ResponseEntity.ok(professor);
    }

    @GetMapping
    public ResponseEntity<java.util.List<Professor>> findAll() {
        return ResponseEntity.ok(professorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> findById(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> update(@PathVariable Long id, @RequestBody Professor professor) {
        return ResponseEntity.ok(professorService.update(id, professor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professorService.delete(id);
        return ResponseEntity.noContent().build();
    }


    // REQFUNC 3: professor consulta todos os processos designados a ele
    @GetMapping("/{id}/processes")
    public ResponseEntity<List<br.edu.ifpb.veritas.models.Process>> getAssignedProcesses(@PathVariable("id") Long professorId) {
        List<Process> processes = processService.listByProfessor(professorId);
        return ResponseEntity.ok(processes);
    }

}
