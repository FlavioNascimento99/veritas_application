package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.enums.VoteType;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Meeting;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Vote;
import br.edu.ifpb.veritas.repositories.MeetingRepository;
import br.edu.ifpb.veritas.repositories.ProcessRepository;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import br.edu.ifpb.veritas.repositories.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ProcessRepository processRepository;
    private final ProfessorRepository professorRepository;
    private final MeetingRepository meetingRepository;

    /**
     * REQFUNC 5: Professor registra seu voto em um processo.
     * REQFUNC 12: Impede votação em processos de reuniões finalizadas.
     */
    @Transactional
    public Vote registerVote(Long processId, Long professorId, VoteType voteType, String justification) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

        // Impede voto em processo de reunião finalizada
        validateMeetingNotFinalized(processId);

        // Valida se o processo está em análise
        if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
            throw new IllegalStateException("Processo não está disponível para votação. Status atual: " + process.getStatus());
        }

        // Verifica se o professor já votou
        Optional<Vote> existingVote = voteRepository.findByProcessIdAndProfessorId(processId, professorId);
        if (existingVote.isPresent()) {
            throw new IllegalStateException("Professor já votou neste processo. ID do voto existente: " + existingVote.get().getId());
        }

        // Cria e salva o voto
        Vote vote = new Vote();
        vote.setProcess(process);
        vote.setProfessor(professor);
        vote.setVoteType(voteType);
        vote.setJustification(justification);
        vote.setAway(false);
        vote.setVotedAt(LocalDateTime.now());

        Vote savedVote = voteRepository.save(vote);

        // Se o professor é o relator, atualiza o voto do relator no processo
        if (process.getProcessRapporteur() != null &&
                process.getProcessRapporteur().getId().equals(professorId)) {

            process.setRapporteurVote(voteType);
            processRepository.save(process);
        }

        return savedVote;
    }

    /**
     * Valida se o processo está em reunião finalizada
     */
    private void validateMeetingNotFinalized(Long processId) {
        List<Meeting> meetings = meetingRepository.findByProcessInAgenda(processId);

        for (Meeting meeting : meetings) {
            if (meeting.getStatus() == MeetingStatus.FINALIZADA) {
                throw new IllegalStateException(
                        "Não é possível votar em um processo cuja reunião (ID: "
                                + meeting.getId() + ") já foi finalizada."
                );
            }
        }
    }

    @Transactional
    public Vote registerAbsence(Long processId, Long professorId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));

        // Valida se reunião não está finalizada
        validateMeetingNotFinalized(processId);

        Vote vote = new Vote();
        vote.setProcess(process);
        vote.setProfessor(professor);
        vote.setAway(true);
        vote.setVotedAt(LocalDateTime.now());

        return voteRepository.save(vote);
    }

    public List<Vote> findVotesByProcess(Long processId) {
        return voteRepository.findByProcessId(processId);
    }

    public List<Vote> findActiveVotesByProcess(Long processId) {
        return voteRepository.findActiveVotesByProcessId(processId);
    }

    public boolean hasVoted(Long processId, Long professorId) {
        return voteRepository.findByProcessIdAndProfessorId(processId, professorId).isPresent();
    }

    public VoteType calculateResult(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        VoteType rapporteurVote = process.getRapporteurVote();

        if (rapporteurVote == null) {
            throw new IllegalStateException("Relator ainda não votou neste processo.");
        }

        Long votesForRapporteur = voteRepository.countByProcessIdAndVoteType(processId, rapporteurVote);
        Long totalVotes = voteRepository.countVotesByProcessId(processId);

        if (totalVotes == 0) {
            return rapporteurVote;
        }

        long majority = (totalVotes / 2) + 1;

        if (votesForRapporteur >= majority) {
            return rapporteurVote;
        } else {
            return rapporteurVote == VoteType.DEFERIDO ? VoteType.INDEFERIDO : VoteType.DEFERIDO;
        }
    }
}