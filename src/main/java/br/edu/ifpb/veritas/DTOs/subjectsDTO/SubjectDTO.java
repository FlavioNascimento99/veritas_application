package br.edu.ifpb.veritas.DTOs.subjectsDTO;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {

   @NotBlank(message = "Invalid Title")
   @Size(min = 3, max = 100, message = "Write something between 3-100 characters")
   private String title;

   @NotBlank(message = "Invalid Description")
   @Size(max = 500, message = "Character-limit: 500")
   private String description;

}
