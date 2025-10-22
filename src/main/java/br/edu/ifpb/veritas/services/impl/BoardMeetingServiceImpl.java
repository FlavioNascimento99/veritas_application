package br.edu.ifpb.veritas.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.ifpb.veritas.models.BoardMeeting;
import br.edu.ifpb.veritas.repositories.BoardMeetingRepository;
import br.edu.ifpb.veritas.services.BoardMeetingService;
import br.edu.ifpb.veritas.services.exceptions.ResourceNotFoundException;

@Service
public class BoardMeetingServiceImpl implements BoardMeetingService {

    @Autowired
    private BoardMeetingRepository boardMeetingRepository;

    @Override
    public BoardMeeting create(BoardMeeting boardMeeting) {
        return boardMeetingRepository.save(boardMeeting);
    }

    @Override
    public BoardMeeting update(Long id, BoardMeeting boardMeeting) {
        BoardMeeting existing = boardMeetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BoardMeeting not found"));
        existing.setMeetingDate(boardMeeting.getMeetingDate());
        existing.setAgenda(boardMeeting.getAgenda());
        existing.setMembers(boardMeeting.getMembers());
        return boardMeetingRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        BoardMeeting existing = boardMeetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BoardMeeting not found"));
        boardMeetingRepository.delete(existing);
    }

    @Override
    public BoardMeeting findById(Long id) {
        return boardMeetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BoardMeeting not found"));
    }

    @Override
    public List<BoardMeeting> findAll() {
        return boardMeetingRepository.findAll();
    }
}
