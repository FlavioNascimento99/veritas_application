package br.edu.ifpb.veritas.controllers.api;

import br.edu.ifpb.veritas.models.Process;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.services.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/processos")
@RequiredArgsConstructor
public class ProcessAPIController {
   private final ProcessService processService;

   /**

    Student Process Controller

    1. Process Creator
    2. Own Process Listing

    */

   @PostMapping
   @PreAuthorize("hasRole('STUDENT')")
   public ResponseEntity<Process> create(@RequestBody Process process, Authentication auth, UriComponentsBuilder uriBuilder) {
      User student = (User) auth.getPrincipal();
      Long studentId = student.getId();
      Process saved = processService.create(process, studentId);
      var uri = uriBuilder.path("/api/processos/{id}").buildAndExpand(saved.getId()).toUri();
      return ResponseEntity.created(uri).body(saved);
   }

//   @GetMapping("/my-processes")
//   @PreAuthorize("hasRole('STUDENT')")
//   public ResponseEntity<List<ProcessListDTO>> listOwnedStudentProcesses(Authentication auth) {
//      User student = (User) auth.getPrincipal();
//      Long studentId = student.getId();
//      List<ProcessListDTO> processes = processService.listByStudent(studentId);
//      return ResponseEntity.ok(processes);
//   }

   @GetMapping("/my-processes")
   @PreAuthorize("hasRole('STUDENT')")
   public ResponseEntity<List<Process>> listOwnedStudentProcesses(Authentication auth) {
      User student = (User) auth.getPrincipal();
      Long studentId = student.getId();
      List<Process> processes = processService.listByStudent(studentId);
      return ResponseEntity.ok(processes);
   }


   /**

    Professor Process Controller

    1. Owned Professor Processes (REQFUNC 3)
    */

//   @GetMapping("/designated-to-me")
//   @PreAuthorize("hasRole('PROFESSOR')")
//   public ResponseEntity<List<ProcessListDTO>> listOwnedProfessorProcesses(Authentication auth) {
//      User professor = (User) auth.getPrincipal();
//      Long professorId = professor.getId();
//      List<ProcessListDTO> processes = processService.listByProfessor(professorId);
//      return ResponseEntity.ok(processes);
//   }

   @GetMapping("/designated-to-me")
   @PreAuthorize("hasRole('PROFESSOR')")
   public ResponseEntity<List<Process>> listOwnedProfessorProcesses(Authentication auth) {
      User professor = (User) auth.getPrincipal();
      Long professorId = professor.getId();
      List<Process> processes = processService.listByProfessor(professorId);
      return ResponseEntity.ok(processes);
   }

   /**
    * Coordinator Process Controller
    * 1. Distribute Process (REQFUNC 8)
    */
//   @PatchMapping("/{processId}/distribute")
//   @PreAuthorize("hasRole('COORDINATOR')")
//   public ResponseEntity<ProcessResponseDTO> distributeProcess(@PathVariable Long processId, @Valid @RequestBody ProcessDistributeDTO dto) {
//       ProcessResponseDTO updatedProcess = processService.distribute(processId, dto.getProfessorId());
//       return ResponseEntity.ok(updatedProcess);
//   }
   @PatchMapping("/{processId}/distribute")
   @PreAuthorize("hasRole('COORDINATOR')")
   public ResponseEntity<Process> distributeProcess(@PathVariable Long processId, @RequestBody Map<String, Long> body) {
      Long professorId = body.get("professorId");
      Process updatedProcess = processService.distribute(processId, professorId);
      return ResponseEntity.ok(updatedProcess);
   }
}
