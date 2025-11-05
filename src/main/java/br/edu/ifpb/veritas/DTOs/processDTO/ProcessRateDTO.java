package br.edu.ifpb.veritas.DTOs.processDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessRateDTO {
   @NotNull(message = "Approve it?")
   private Boolean approve;

   @Size(min = 3, max = 500, message = "Technical opinion's gap is 3-500")
   private String technicalOpinion;
}

