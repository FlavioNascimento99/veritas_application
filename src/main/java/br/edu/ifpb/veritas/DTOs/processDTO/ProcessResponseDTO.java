package br.edu.ifpb.veritas.DTOs.processDTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessResponseDTO {
   private Long id;
   private String title;
   private String description;
   private String technicalOpinion;

   private Long studentId;
   private String studentName;

   private Long subjectId;
   private String subjectName;

   private Long professorId;
   private String professorName;

   private LocalDateTime createdAt;
   private LocalDateTime distributedAt;
   private LocalDateTime solvedAt;

   public ProcessResponseDTO(br.edu.ifpb.veritas.models.Process process) {
      this.id                 = process.getId();
      this.title              = process.getTitle();
      this.description        = process.getDescription();
      this.technicalOpinion   = process.getTechnicalOpinion();
      
      this.studentId          = process.getStudent().getId();
      this.studentName        = process.getStudent().getName();

      /*
       * O processo pode ainda não ter sido encamihado para um determinado professor
       * Então, é interessante termos uma camada condcional pra verificar isso.
       */
      if (process.getProfessor() != null) {
         this.professorId        = process.getProfessor().getId();
         this.professorName      = process.getProfessor().getName();
      }

      this.createdAt          = process.getCreatedAt();
      this.distributedAt      = process.getDistributedAt();
      this.solvedAt           = process.getSolvedAt();
   }

}
