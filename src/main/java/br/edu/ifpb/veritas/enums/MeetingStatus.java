package br.edu.ifpb.veritas.enums;


public enum MeetingStatus {
   FINALIZADA("FINALIZADA"),
   AGENDADA ("AGENDADA");

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