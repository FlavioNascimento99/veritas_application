package br.edu.ifpb.veritas.enums;

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