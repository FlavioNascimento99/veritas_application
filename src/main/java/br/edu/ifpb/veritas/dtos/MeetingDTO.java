package br.edu.ifpb.veritas.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para criação e edição de Reuniões.
 * Facilita a transferência de dados entre Controller e Service.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDTO {
    private Long id;
    private String description;
    private Long collegiateId;
    private LocalDateTime scheduledDate;
    private List<Long> processIds;
    private List<Long> participantIds;
}
