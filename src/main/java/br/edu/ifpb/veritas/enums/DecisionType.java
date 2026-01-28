package br.edu.ifpb.veritas.enums;

// ENUM específico para o voto relator (não é o mesmo voto dos membros do colegiado)
public enum DecisionType {
    // Indica que o processo foi aprovado pelo relator
    DEFERIMENTO,

    // Indica que o processo foi negado pelo relator
    INDEFERIMENTO;

    /**
     * Converte valores antigos para os novos valores do enum.
     * Compatibilidade com dados legados do banco de dados.
     */
    public static DecisionType fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        return switch (value.toUpperCase()) {
            case "DEFERIDO", "DEFERIMENTO" -> DEFERIMENTO;
            case "INDEFERIDO", "INDEFERIMENTO" -> INDEFERIMENTO;
            default -> valueOf(value); // Tenta converter normal se não cair em nenhum case
        };
    }
}
