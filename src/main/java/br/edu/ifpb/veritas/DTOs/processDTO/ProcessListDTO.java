package br.edu.ifpb.veritas.DTOs.processDTO;

import java.time.LocalDateTime;

import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.models.Process;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessListDTO {
   private Long            processId;
   private String          processTitle;
   private StatusProcess   processStatus;
   private String          studentName;
   private String          subjectTitle;
   private String          professorName;
   private LocalDateTime   processCreatedAt;

   public ProcessListDTO(Process process) {
      this.processId          = process.getId();
      this.processTitle       = process.getTitle();
      this.processStatus      = process.getStatus();
      this.studentName        = process.getStudent().getName();
      this.subjectTitle       = process.getSubject().getTitle();
      this.professorName      = process.getProfessor().getName() != null ? process.getProfessor().getName() : null; // Novamente verificação para caso o processo ainda não tenha sido anexado a nenhum professor.
      this.processCreatedAt   = process.getCreatedAt();
   }
}
