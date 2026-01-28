package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.DecisionType;
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

/**
 * Serviço responsável pelo gerenciamento de votos no sistema de colegiado.
 *
 * Lógica de votação (pelo menos foi o que entendi):
 * 1) Relator vota primeiro: DEFERIMENTO ou INDEFERIMENTO (com justificativa)
 * 2) Membros do colegiado votam: COM_RELATOR ou DIVERGENTE
 * 3) Sistema calcula resultado: maioria define se prevalece voto do relator ou não
 */
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ProcessRepository processRepository;
    private final ProfessorRepository professorRepository;
    private final MeetingRepository meetingRepository;

//    =============== ENTENDIMENTO INICIAL DA VOTAÇÃO ===============
//    /**
//     * REQFUNC 5: Professor registra seu voto em um processo.
//     *
//     * Validações:
//     * - Processo não pode estar finalizado
//     * - Professor não pode votar duas vezes no mesmo processo
//     * - Reunião contendo o processo não pode estar finalizada
//     */
//    @Transactional
//    public Vote registerVote(Long processId, Long professorId, VoteType voteType, String justification) {
//        Process process = processRepository.findById(processId)
//                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));
//
//        Professor professor = professorRepository.findById(professorId)
//                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));
//
//        // Impede voto em processo já finalizado
//        if (process.getStatus() == StatusProcess.APPROVED || process.getStatus() == StatusProcess.REJECTED) {
//            throw new IllegalStateException("Não é possível votar em um processo já finalizado. Status: " + process.getStatus());
//        }
//
//        // Impede voto em reunião finalizada
//        validateMeetingNotFinalized(processId);
//
//        // Valida se o processo está sob análise
//        if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
//            throw new IllegalStateException("Processo não está disponível para votação. Status atual: " + process.getStatus());
//        }
//
//        // Verifica se o professor já votou
//        Optional<Vote> existingVote = voteRepository.findByProcessIdAndProfessorId(processId, professorId);
//        if (existingVote.isPresent()) {
//            throw new IllegalStateException("Professor já votou neste processo. ID do voto existente: " + existingVote.get().getId());
//        }
//
//        // Cria e salva o voto
//        Vote vote = new Vote();
//        vote.setProcess(process);
//        vote.setProfessor(professor);
//        vote.setVoteType(voteType);
//        vote.setJustification(justification);
//        vote.setAway(false);
//        vote.setVotedAt(LocalDateTime.now());
//
//        Vote savedVote = voteRepository.save(vote);
//
//        // Se o professor é o relator, atualiza o voto do relator no processo
//        if (process.getProcessRapporteur() != null &&
//                process.getProcessRapporteur().getId().equals(professorId)) {
//
//            process.setRapporteurVote(voteType);
//            processRepository.save(process);
//        }
//
//        return savedVote;
//    }

    /**
     * Achei melhor separar os métodos (um pro voto do relator e outro pro voto dos membros do colegiado)
     *
     * REQFUNC 5: Professor RELATOR registra seu voto (decisão) sobre o processo.
     *
     * O relator vota pelo DEFERIMENTO ou INDEFERIMENTO do processo, podendo incluir
     * uma justificativa escrita.
     *
     * Observações:
     * - APENAS o relator designado pode votar
     * - Processo deve estar UNDER_ANALYSIS
     * - Relator NÃO PODE votar duas vezes
     * - Processo NÃO PODE estar em reunião já finalizada
     */
    @Transactional
    public Process registerRapporteurDecision(Long processId, Long professorId,
                                              DecisionType decision, String justification) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

        // Valida se o professor é o relator do processo
        if (process.getProcessRapporteur() == null ||
                !process.getProcessRapporteur().getId().equals(professorId)) {
            throw new IllegalStateException("Apenas o relator designado pode registrar a decisão sobre o processo.");
        }

        // Valida se processo está em análise
        if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
            throw new IllegalStateException("Processo não está disponível para votação do relator. Status atual: " + process.getStatus().getStatus());
        }

        // Impede voto duplicado do relator
        if (process.getRapporteurVote() != null) {
            throw new IllegalStateException("Relator já registrou sua decisão para este processo.");
        }

        // Valida se reunião não está finalizada (caso processo esteja em pauta)
        validateMeetingNotFinalized(processId);

        // Registra a decisão do relator
        process.setRapporteurVote(decision);
        process.setRapporteurJustification(justification);

        return processRepository.save(process);
    }

    /**
     * Registra o voto de um membro do colegiado (NÃO relator) em um processo.
     *
     * O membro vota se concorda (COM_RELATOR) ou discorda (DIVERGENTE) da decisão do relator.
     *
     * Este voto acontece durante a reunião do colegiado.
     *
     * Validações:
     * - Relator já deve ter votado
     * - Processo deve estar EM_ANALISE
     * - Professor não pode ser o relator
     * - Professor não pode votar duas vezes
     * - Reunião não pode estar finalizada
     * - Deve existir reunião ativa
     */
    @Transactional
    public Vote registerMemberVote(Long processId, Long professorId,
                                   VoteType voteType, String justification) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

        // Valida se relator já votou
        if (process.getRapporteurVote() == null) {
            throw new IllegalStateException("O relator ainda não registrou sua decisão sobre este processo.");
        }

        // Valida se processo está em análise
        if (process.getStatus() != StatusProcess.UNDER_ANALISYS) {
            throw new IllegalStateException("Processo não está disponível para votação. Status atual: " + process.getStatus().getStatus());
        }

        // Valida que quem está votando NÃO é o relator (talvez seja desnecessário, mas né, validar demais nunca é ruim)
        if (process.getProcessRapporteur() != null &&
                process.getProcessRapporteur().getId().equals(professorId)) {
            throw new IllegalStateException("O relator não vota novamente como membro. Sua decisão já foi registrada.");
        }

        // Verifica se reunião está ativa
        Meeting activeMeeting = meetingRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Não há reunião ativa no momento."));

        // Valida se processo está na pauta da reunião ativa
        boolean isInAgenda = activeMeeting.getProcesses().stream()
                .anyMatch(p -> p.getId().equals(processId));

        if (!isInAgenda) {
            throw new IllegalStateException("Processo não está na pauta da reunião ativa (ID: " + activeMeeting.getId() + ").");
        }

        // Valida se professor é participante da reunião
        boolean isParticipant = activeMeeting.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(professorId));

        if (!isParticipant) {
            throw new IllegalStateException("Professor não é participante da reunião ativa.");
        }

        // Verifica se professor já votou
        Optional<Vote> existingVote = voteRepository.findByProcessIdAndProfessorId(processId, professorId);
        if (existingVote.isPresent()) {
            throw new IllegalStateException("Professor já votou neste processo. ID do voto: " + existingVote.get().getId());
        }

        // Valida reunião não finalizada
        validateMeetingNotFinalized(processId);

        // Cria e salva o voto
        Vote vote = new Vote();
        vote.setProcess(process);
        vote.setProfessor(professor);
        vote.setVoteType(voteType);
        vote.setJustification(justification);
        vote.setAway(false);
        vote.setVotedAt(LocalDateTime.now());

        return voteRepository.save(vote);
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
     * Regra de cálculo:
     * - Conta votos COM RELATOR vs DIVERGENTE DO RELATOR
     * - Se maioria votou COM RELATOR → resultado = decisão do relator
     * - Se maioria votou DIVERGENTE → resultado = contrário à decisão do relator
     * - Em caso de empate, prevalece o voto do relator (OBS.: não tenho certeza se é essa lógica mesmo em caso de empate)
     *
     * Atualiza o status do processo para DEFERIDO ou INDEFERIDO.
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
        DecisionType result = calculateResult(processId);

        // Atualiza o status do processo baseado no resultado
        if (result == DecisionType.DEFERIMENTO) {
            process.setStatus(StatusProcess.APPROVED);
        } else {
            process.setStatus(StatusProcess.REJECTED);
        }

        process.setSolvedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    /**
     * REQFUNC 11: Calcula o resultado da votação de um processo.
     *
     * Lógica:
     * 1. Conta quantos membros votaram COM_RELATOR
     * 2. Conta quantos membros votaram DIVERGENTE
     * 3. Se maioria votou COM_RELATOR → mantém decisão do relator
     * 4. Se maioria votou DIVERGENTE → inverte decisão do relator
     * 5. Em empate → prevalece decisão do relator
     */
    public DecisionType calculateResult(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        DecisionType rapportVote = process.getRapporteurVote();

        if (rapportVote == null) {
            throw new IllegalStateException("Relator ainda não votou neste processo.");
        }

        // Conta votos dos membros
        Long votosComRelator = voteRepository.countByProcessIdAndVoteType(processId, VoteType.DEFERIDO);
        Long votosDivergentes = voteRepository.countByProcessIdAndVoteType(processId, VoteType.INDEFERIDO);

        // Se não houve votação dos membros, prevalece decisão do relator
        if (votosComRelator == 0 && votosDivergentes == 0) {
            return rapportVote;
        }

        // Se maioria votou COM_RELATOR, mantém decisão do relator
        if (votosComRelator >= votosDivergentes) {
            return rapportVote;
        } else {
            // Se maioria votou DIVERGENTE, inverte a decisão
            return rapportVote == DecisionType.DEFERIMENTO
                    ? DecisionType.INDEFERIMENTO
                    : DecisionType.DEFERIMENTO;
        }
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

    // Métodos adicionais de consulta
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
     * Estatísticas de votação (precisa ser revisado)
     */
    public static class VotingStats {
        public final Long totalVotes;
        public final Long votesForDeferido;
        public final Long votesForIndeferido;
        public final DecisionType rapporteurVote;
        public final StatusProcess processStatus;

        public VotingStats(Long totalVotes, Long votesForDeferido, Long votesForIndeferido,
                           DecisionType rapporteurVote, StatusProcess processStatus) {
            this.totalVotes = totalVotes;
            this.votesForDeferido = votesForDeferido;
            this.votesForIndeferido = votesForIndeferido;
            this.rapporteurVote = rapporteurVote;
            this.processStatus = processStatus;
        }
    }
}