package br.edu.ifpb.veritas.enums;


/**
 * ENUM Específico para controle de votos 
 * dos membros comuns do colegiado, neste caso
 * professores presentes dentro da reunião/processo
*/
public enum VoteType {
   DEFERIDO("DEFERIDO"),
   INDEFERIDO("INDEFERIDO");

   private String status;

   VoteType(String status){ 
      this.status = status;
   }

   public String getStatus() {
      return status;
   }
   
   public void setStatus(String status) {
      this.status = status;
   }
}