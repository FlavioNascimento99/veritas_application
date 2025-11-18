package br.edu.ifpb.veritas.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.services.SubjectService;

@Controller
@RequestMapping("api/subjects")
public class SubjectAPIController {

   @Autowired
   private SubjectService subjectService;

   @PostMapping
   public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
      return ResponseEntity.ok(subjectService.createSubject(subject));
   }

   @GetMapping
   public ResponseEntity<List<Subject>> getSubjects() {
      return ResponseEntity.ok(subjectService.listSubjects());
   }

   @PutMapping("/{id}")
   public ResponseEntity<Subject> reloadSubjects(@PathVariable Long id, @RequestBody Subject subject) {
      return ResponseEntity.ok(subjectService.reload(id, subject));
   }

   // Não remove o subject,
   // apenas seta "active" pra false
   @DeleteMapping("/{id}")
   public ResponseEntity<Subject> inactivateSubject(@PathVariable Long id) {
      subjectService.deactivate(id);
      return ResponseEntity.noContent().build();
   }

   // Talvez seja interessante ser
   // possível reativar o subject

}
