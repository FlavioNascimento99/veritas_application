package br.edu.ifpb.veritas.controllers.api;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//import br.edu.ifpb.veritas.DTOs.subjectsDTO.SubjectDTO;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.services.SubjectService;

@Controller
@RequestMapping("api/subjects")
public class SubjectAPIController {

   @Autowired
   private SubjectService subjectService;

   @PostMapping
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
      return ResponseEntity.ok(subjectService.createSubject(subject));
   }

   @GetMapping
   public ResponseEntity<List<Subject>> getSubjects() {
      return ResponseEntity.ok(subjectService.getAll());
   }

   @PutMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Subject> reloadSubjects(@PathVariable Long id, @RequestBody Subject subject) {
      return ResponseEntity.ok(subjectService.reload(id, subject));
   }

//   @PostMapping
//   @PreAuthorize("hasRole('ADMIN')")
//   public ResponseEntity<Subject> createSubject(@RequestBody SubjectDTO subjectDTO) {
//      return ResponseEntity.ok(subjectService.create(subjectDTO));
//   }

//
//   @PutMapping("/{id}")
//   @PreAuthorize("hasRole('ADMIN')")
//   public ResponseEntity<Subject> reloadSubjects(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
//      return ResponseEntity.ok(subjectService.reload(id, subjectDTO));
//   }
//
   @DeleteMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Subject> inactivateSubject(@PathVariable Long id) {
      subjectService.deactivate(id);
      return ResponseEntity.noContent().build();
   }
}
