package br.edu.ifpb.veritas.enums;

/**
 * Status do voto do relator em um processo.
 * Diferente de DecisionType (DEFERIMENTO/INDEFERIMENTO), que é a DECISÃO do voto.
 * 
 * Este enum rastreia o ESTADO do processo de votação do relator.
 */
public enum ProcessRapporteurStatus {
    /**
     * O relator ainda não votou no processo.
     * O processo está aguardando a votação do relator.
     */
    PENDING("Aguardando Voto"),
    
    /**
     * O relator já votou no processo.
     * O voto foi registrado com uma decisão (DEFERIMENTO ou INDEFERIMENTO).
     */
    VOTED("Votado"),
    
    /**
     * O relator se absteve de votar.
     * Não há decisão registrada.
     */
    ABSTAINED("Abstenção");
    
    private final String displayName;
    
    ProcessRapporteurStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
