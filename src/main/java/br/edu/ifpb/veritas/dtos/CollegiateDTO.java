package br.edu.ifpb.veritas.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CollegiateDTO {
    private String description;
    private Long rapporteurId;
    private List<Long> memberIds;
}
