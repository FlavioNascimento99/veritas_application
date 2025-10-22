package br.edu.ifpb.veritas.services.mapper;

import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.models.AcademicCase;
import br.edu.ifpb.veritas.services.dto.UserDTO;
import br.edu.ifpb.veritas.services.dto.AcademicCaseDTO;

public class DTOMapper {

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static AcademicCaseDTO toAcademicCaseDTO(AcademicCase ac) {
        if (ac == null) return null;
        AcademicCaseDTO dto = new AcademicCaseDTO();
        dto.setId(ac.getId());
        dto.setSubject(ac.getSubject());
        dto.setDescription(ac.getDescription());
        dto.setCreationDate(ac.getCreationDate());
        if (ac.getAuthor() != null) dto.setAuthorId(ac.getAuthor().getId());
        if (ac.getRapporteur() != null) dto.setRapporteurId(ac.getRapporteur().getId());
        if (ac.getBoardMeeting() != null) dto.setBoardMeetingId(ac.getBoardMeeting().getId());
        return dto;
    }
}
