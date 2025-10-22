package br.edu.ifpb.veritas.services.dto;

import java.time.LocalDate;

public class AcademicCaseDTO {
    private Long id;
    private String subject;
    private String description;
    private LocalDate creationDate;
    private Long authorId;
    private Long rapporteurId;
    private Long boardMeetingId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public Long getRapporteurId() { return rapporteurId; }
    public void setRapporteurId(Long rapporteurId) { this.rapporteurId = rapporteurId; }
    public Long getBoardMeetingId() { return boardMeetingId; }
    public void setBoardMeetingId(Long boardMeetingId) { this.boardMeetingId = boardMeetingId; }
}
