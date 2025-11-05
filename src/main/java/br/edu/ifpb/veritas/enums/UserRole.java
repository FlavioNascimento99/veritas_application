package br.edu.ifpb.veritas.enums;

public enum UserRole {
   ADMIN("ROLE_ADMIN"),
   COORDENADOR("ROLE_COORDINATOR"),
   PROFESSOR("ROLE_PROFESSOR"),
   ESTUDANTE("ROLE_STUDENT");

   private String authority;

   UserRole(String authority) {
      this.authority = authority;
   }

   public String getAuthority() {
      return authority;
   }
}