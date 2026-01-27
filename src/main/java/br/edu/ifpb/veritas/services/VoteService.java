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
     *
     * Validações:
     * - Processo não pode estar finalizado
     * - Professor não pode votar duas vezes no mesmo processo
     * - Reunião contendo o processo não pode estar finalizada
     */
    @Transactional
    public Vote registerVote(Long processId, Long professorId, VoteType voteType, String justification) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

        // Impede voto em processo já finalizado
        if (process.getStatus() == StatusProcess.APPROVED || process.getStatus() == StatusProcess.REJECTED) {
            throw new IllegalStateException("Não é possível votar em um processo já finalizado. Status: " + process.getStatus());
        }

        // Impede voto em reunião finalizada
        validateMeetingNotFinalized(processId);

        // Valida se o processo está sob análise
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
     * Verifica se reunião contendo o processo não está finalizada
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

    /**
     * REQFUNC 11: Apregoa um processo e calcula seu resultado final automaticamente.
     *
     * Este método deve ser chamado pelo coordenador durante a reunião ativa.
     *
     * Finaliza o julgamento do processo, calculando o resultado baseado nos votos
     * e atualizando seu status para APPROVED ou REJECTED
     */
    @Transactional
    public Process announceProcess(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        // Valida se o processo está sob análise
        if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
            throw new IllegalStateException("Processo não pode ser apregoado. Status atual: " + process.getStatus());
        }

        // Valida se o relator já votou
        if (process.getRapporteurVote() == null) {
            throw new IllegalStateException("Relator ainda não votou neste processo. Apregoamento não pode ser realizado.");
        }

        // Valida se há uma reunião ativa
        Meeting activeMeeting = meetingRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Não há reunião ativa no momento."));

        // Valida se o processo está na pauta da reunião ativa
        boolean isInAgenda = activeMeeting.getProcesses().stream()
                .anyMatch(p -> p.getId().equals(processId));

        if (!isInAgenda) {
            throw new IllegalStateException("Processo não está na pauta da reunião ativa (ID: " + activeMeeting.getId() + ").");
        }

        // Calcula o resultado da votação
        VoteType result = calculateResult(processId);

        // Atualiza o status do processo baseado no resultado
        if (result == VoteType.DEFERIDO) {
            process.setStatus(StatusProcess.APPROVED);
        } else {
            process.setStatus(StatusProcess.REJECTED);
        }

        process.setSolvedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    /**
     * Registra ausência de um professor em uma votação
     */
    @Transactional
    public Vote registerAbsence(Long processId, Long professorId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));

        // Valida se reunião não está finalizada
        validateMeetingNotFinalized(processId);

        // Valida se processo não está finalizado
        if (process.getStatus() == StatusProcess.APPROVED || process.getStatus() == StatusProcess.REJECTED) {
            throw new IllegalStateException("Não é possível registrar ausência em processo já finalizado.");
        }

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

    /**
     * REQFUNC 11: Calcula o resultado da votação de um processo.
     *
     * Regra de negócio:
     * - Se maioria votou igual ao relator → resultado = voto do relator
     * - Se maioria votou diferente do relator → resultado = contrário ao relator
     */
    public VoteType calculateResult(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        VoteType rapporteurVote = process.getRapporteurVote();

        if (rapporteurVote == null) {
            throw new IllegalStateException("Relator ainda não votou neste processo.");
        }

        // Contagem de votos
        Long votesForRapporteur = voteRepository.countByProcessIdAndVoteType(processId, rapporteurVote);
        Long totalVotes = voteRepository.countVotesByProcessId(processId);

        // Se não houve votação pelos membros, prevalece o voto do relator
        if (totalVotes == 0) {
            return rapporteurVote;
        }

        // Calcula maioria
        long majority = (totalVotes / 2) + 1;

        // Se maioria votou com o relator, mantém o voto do relator
        if (votesForRapporteur >= majority) {
            return rapporteurVote;
        } else {
            // Caso contrário, retorna o contrário do voto do relator
            return rapporteurVote == VoteType.DEFERIDO ? VoteType.INDEFERIDO : VoteType.DEFERIDO;
        }
    }

    /**
     * Retorna informações sobre a votação de um processo
     */
    public VotingStats getVotingStats(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        Long totalVotes = voteRepository.countVotesByProcessId(processId);
        Long votesForDeferido = voteRepository.countByProcessIdAndVoteType(processId, VoteType.DEFERIDO);
        Long votesForIndeferido = voteRepository.countByProcessIdAndVoteType(processId, VoteType.INDEFERIDO);

        return new VotingStats(
                totalVotes,
                votesForDeferido,
                votesForIndeferido,
                process.getRapporteurVote(),
                process.getStatus()
        );
    }

    /**
     * Estatísticas de votação
     */
    public static class VotingStats {
        public final Long totalVotes;
        public final Long votesForDeferido;
        public final Long votesForIndeferido;
        public final VoteType rapporteurVote;
        public final StatusProcess processStatus;

        public VotingStats(Long totalVotes, Long votesForDeferido, Long votesForIndeferido,
                           VoteType rapporteurVote, StatusProcess processStatus) {
            this.totalVotes = totalVotes;
            this.votesForDeferido = votesForDeferido;
            this.votesForIndeferido = votesForIndeferido;
            this.rapporteurVote = rapporteurVote;
            this.processStatus = processStatus;
        }
    }
}