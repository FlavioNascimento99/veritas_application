package br.edu.ifpb.veritas.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CollegiateEditDTO {
    private Long id;
    private String description;
    private Long rapporteurId;
    private List<Long> memberIds;
    private List<Long> processIds;
}
