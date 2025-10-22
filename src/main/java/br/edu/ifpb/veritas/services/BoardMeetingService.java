package br.edu.ifpb.veritas.services;

import java.util.List;
import br.edu.ifpb.veritas.models.BoardMeeting;

public interface BoardMeetingService {
    BoardMeeting create(BoardMeeting boardMeeting);
    BoardMeeting update(Long id, BoardMeeting boardMeeting);
    void delete(Long id);
    BoardMeeting findById(Long id);
    List<BoardMeeting> findAll();
}
