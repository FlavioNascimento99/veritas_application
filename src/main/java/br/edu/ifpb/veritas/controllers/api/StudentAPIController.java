package br.edu.ifpb.veritas.controllers.api;

import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.services.ProcessService;
import br.edu.ifpb.veritas.services.StudentService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentAPIController {
    private final StudentService studentService;
    private final ProcessService processService;

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        studentService.create(student);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<List<Student>> findAll() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return ResponseEntity.ok(studentService.update(id, student));
    }

    @PatchMapping("/{id}/desactivate")
    public ResponseEntity<Void> desactivate(@PathVariable Long id) {
        studentService.desactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable Long id) { 
        studentService.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * REQFUNC 2
     *
     * Lista processos do estudante com filtros opcionais:
     *  - status: nome do enum StatusProcess (ex: WAITING)
     *  - subjectId: id da disciplina
     *  - sort: "asc" ou "desc" (ordenado por createdAt). Padrão: desc.
     */

    // Precisa ser testado
    @GetMapping("/{studentId}/processes")
    public ResponseEntity<List<br.edu.ifpb.veritas.models.Process>> listStudentProcesses(
            @PathVariable Long studentId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "subjectId", required = false) Long subjectId) {

        List<Process> processes = processService.listByStudentFiltered(studentId, status, subjectId);
        return ResponseEntity.ok(processes);
    }

}
