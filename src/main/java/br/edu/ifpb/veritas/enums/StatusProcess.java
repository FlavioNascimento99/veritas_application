package br.edu.ifpb.veritas.enums;

public enum StatusProcess {
   WAITING("EM ESPERA"),
   UNDER_ANALISYS("SOB ANÁLISE"),
   APPROVED("APROVADO"),
   REJECTED("REJEITADO");

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
