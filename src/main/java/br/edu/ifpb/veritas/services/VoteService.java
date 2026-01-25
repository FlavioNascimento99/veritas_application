package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.enums.DecisionType;
import br.edu.ifpb.veritas.enums.VoteType;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Vote;
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

    // REQFUNC 5: Professor registra seu voto em um processo
    @Transactional
    public Vote registerVote(Long processId, Long professorId, VoteType voteType, String justification) {
        // Verifica se o processo existe
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com ID: " + processId));

        // Veririca se o professor existe
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));

        // Verifica se o professor já votou neste processo
        Optional<Vote> existingVote = voteRepository.findByProcessIdAndProfessorId(processId, professorId);
        if (existingVote.isPresent()) {
            throw new IllegalStateException("Professor já votou neste processo. ID do voto existente: " + existingVote.get().getId());
        }

        // Computa o voto de fato
        Vote vote = new Vote();
        vote.setProcess(process);
        vote.setProfessor(professor);
        vote.setVoteType(voteType);
        vote.setJustification(justification);
        vote.setAway(false);
        vote.setVotedAt(LocalDateTime.now());

        return voteRepository.save(vote);
    }

    // Registra ausência de um professor em uma votação
    @Transactional
    public Vote registerAbsence(Long processId, Long professorId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));

        Vote vote = new Vote();
        vote.setProcess(process);
        vote.setProfessor(professor);
        vote.setAway(true);
        vote.setVotedAt(LocalDateTime.now());

        return voteRepository.save(vote);
    }

    // Busca todos os votos de um processo
    public List<Vote> findVotesByProcess(Long processId) {
        return voteRepository.findByProcessId(processId);
    }

    // Busca votos ativos (excluindo ausentes) de um processo
    public List<Vote> findActiveVotesByProcess(Long processId) {
        return voteRepository.findActiveVotesByProcessId(processId);
    }

    // Verifica se um professor já votou em um processo
    public boolean hasVoted(Long processId, Long professorId) {
        return voteRepository.findByProcessIdAndProfessorId(processId, professorId).isPresent();
    }

    /**
     * REQFUNC 11: Calcula o resultado da votação de um processo.
     * - Se maioria votou igual ao relator, então: resultado = voto do relator
     * - Se maioria votou diferente do relator, então: resultado = contrário ao relator
     */
    public VoteType calculateResult(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado."));

        // Voto do relator (já registrado no processo)
        VoteType rapporteurVote = convertDecisionToVote(process.getRapporteurVote());

        if (rapporteurVote == null) {
            throw new IllegalStateException("Relator ainda não votou neste processo.");
        }

        // Contagem de votos
        Long votesForRapporteur = voteRepository.countByProcessIdAndVoteType(processId, rapporteurVote);
        Long totalVotes = voteRepository.countVotesByProcessId(processId);

        // Se não houve votação, prevalece o voto do relator
        if (totalVotes == 0) {
            return rapporteurVote;
        }

        // Calcula maioria
        long majority = (totalVotes / 2) + 1;

        // --- Acho que a lógica é essa, é bom validar com mais calma depois ---
        // Se maioria votou com o relator, mantém o voto do relator
        if (votesForRapporteur >= majority) {
            return rapporteurVote;
        } else {
            // Caso contrário, retorna o contrário do voto do relator
            return rapporteurVote == VoteType.DEFERIDO ? VoteType.INDEFERIDO : VoteType.DEFERIDO;
        }
    }

    // Converte DecisionType (do relator) para VoteType (dos membros)
    private VoteType convertDecisionToVote(DecisionType decision) {
        if (decision == null) return null;
        return decision == DecisionType.DEFERIMENTO
                ? VoteType.DEFERIDO
                : VoteType.INDEFERIDO;
    }
}
