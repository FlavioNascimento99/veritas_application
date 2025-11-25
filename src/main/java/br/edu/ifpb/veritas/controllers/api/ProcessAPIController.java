package br.edu.ifpb.veritas.controllers.api;
import br.edu.ifpb.veritas.models.Process;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import br.edu.ifpb.veritas.services.ProcessService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;


/**
 * REST Controller for managing Processes.
 * Handles creation, listing, and distribution of processes
 * for students, professors, and coordinators.
 */
@Controller
@RequestMapping("/api/processes")
@RequiredArgsConstructor
public class ProcessAPIController {
   private final ProcessService processService;

   /**
    * Student Process Controller
    * 1. Process Creator
    * 2. Own Process Listing
    */
   @PostMapping
   public ResponseEntity<Process> create(@RequestBody Process process, @RequestParam("studentId") Long studentId, UriComponentsBuilder uriBuilder) {
      Process saved = processService.create(process, studentId);
      var uri = uriBuilder.path("/api/processes/{id}").buildAndExpand(saved.getId()).toUri();
      return ResponseEntity.created(uri).body(saved);
   }

   @GetMapping("/my-processes")
   public ResponseEntity<List<Process>> listOwnedStudentProcesses(@RequestParam("studentId") Long studentId) {
      List<Process> processes = processService.listByStudent(studentId);
      return ResponseEntity.ok(processes);
   }


   /**
    * Professor Process Controller
    * 1. Owned Professor Processes (REQFUNC 3)
    */
   @GetMapping("/designated-to-me")
   public ResponseEntity<List<Process>> listOwnedProfessorProcesses(@RequestParam("professorId") Long professorId) {
      List<Process> processes = processService.listByProfessor(professorId);
      return ResponseEntity.ok(processes);
   }


   /**
    * Coordinator Process Controller
    * 1. Distribute Process (REQFUNC 8)
    */
   @PatchMapping("/{processId}/distribute")
   public ResponseEntity<Process> distributeProcess(@PathVariable Long processId, @RequestBody Map<String, Long> body) {
      Long professorId = body.get("professorId");
      Process updatedProcess = processService.distribute(processId, professorId);
      return ResponseEntity.ok(updatedProcess);
   }


}
