package br.edu.ifpb.veritas.DTOs.processDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCreateDTO {
   @NotNull(message = "Subject ID's necessary")
   private Long subjectId;

   @NotNull(message = "Process title's necessary")
   private String title;

   @NotBlank(message = "Description's necessary")
   @Size(min = 10, max = 500, message = "Description text-size gap's 4-500 chars")
   private String description;
}
