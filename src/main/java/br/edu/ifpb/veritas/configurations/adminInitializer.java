/**
 *    @Author: Flavio Nasicmento
 * 
 *    Classe de construção do Super Usuário da aplicação
 * 
 *       Consiste em casos de inexistências, validada dentro do método createAdminUser()
 *    construir o mesmo dentro de createNewAdmin(). O método inicializador faz a chamada do 
 *    método de verificação.
 *       
 *       A maneira vista como necessária vista a tipagem escolhida para o método findByEmail()
 *    na classe UserRepository, que fora definida como Optional<User>.
 */

package br.edu.ifpb.veritas.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.repositories.UserRepository;


@Component
public class adminInitializer implements CommandLineRunner {

   @Autowired
   private UserRepository userRepository;

   @Override
   public void run(String... args) throws Exception {
      createAdminUser();
   }

   private void createAdminUser() {
      String adminEmail = "admin@veritas.com";
      userRepository.findByEmail(adminEmail)
         .ifPresentOrElse(
            user -> System.out.println("Usuário Administrativo já existe!"),
            () -> createNewAdmin(adminEmail)
         );
   }

   private void createNewAdmin(String email) {
      User admin = new User();
      admin.setName("admin");
      admin.setEmail(email);
      admin.setPassword("senhasegura123");
      admin.setRole(UserRole.ADMIN);

      userRepository.save(admin);
      System.out.println("Usuário Administrador fora criado [" + admin.getEmail() + "] - [" + admin.getPassword() + "]");
   }
}
