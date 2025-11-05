package br.edu.ifpb.veritas.DTOs.processDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDistributeDTO {
   @NotNull(message = "Professor ID's necessary")
   private Long professorId;
}
