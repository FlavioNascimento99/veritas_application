package br.edu.ifpb.veritas.enums;


public enum MeetingStatus {
   DISPONIVEL("DISPONIVEL"),
   EM_ANDAMENTO("EM_ANDAMENTO"),
   FINALIZADA("FINALIZADA");

   private String status;

   MeetingStatus (String status){ 
      this.status = status;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}