package br.edu.ifpb.veritas.DTOs.subjectsDTO;

import java.time.LocalDateTime;

import br.edu.ifpb.veritas.models.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponseDTO {
   private Long id;
   private String title;
   private String description;
   private Boolean active;
   private LocalDateTime createdAt;
   private LocalDateTime modfiedAt;


   public SubjectResponseDTO(Subject subject) {
      this.id = subject.getId();
      this.title = subject.getTitle();
      this.description = subject.getDescription();
      this.active = subject.getActive();
      this.createdAt = subject.getCreatedAt();
      this.modfiedAt = subject.getModifiedAt();
   }
}
