package br.edu.ifpb.veritas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpb.veritas.DTOs.subjectsDTO.SubjectDTO;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.services.SubjectService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("api/subjects")
public class SubjectController {
   @Autowired
   private SubjectService subjectService;

   @PostMapping
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Subject> createSubject(@RequestBody SubjectDTO subjectDTO) {
      return ResponseEntity.ok(subjectService.create(subjectDTO));
   }

   @GetMapping
   public ResponseEntity<List<Subject>> getSubjects() {
       return ResponseEntity.ok(subjectService.getAll());
   }
   
   @PutMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Subject> reloadSubjects(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
      return ResponseEntity.ok(subjectService.reload(id, subjectDTO));
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Subject> inactivateSubject(@PathVariable Long id) {
      subjectService.deactivate(id);
      return ResponseEntity.noContent().build();
   }
}
