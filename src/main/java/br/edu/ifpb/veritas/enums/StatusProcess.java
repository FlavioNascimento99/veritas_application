package br.edu.ifpb.veritas.enums;

public enum StatusProcess {
   WAITING("WAITING"),
   UNDER_ANALISYS("UNDER_ANALISYS"),
   APPROVED("APPROVED"),
   REJECTED("REJECTED");

   private String status;

   StatusProcess(String status){ 
      this.status = status;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
