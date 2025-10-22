package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.models.CourseCoordinator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {
   
   @Bean
   CommandLineRunner initAdminUser(UserService userService) {
      return args -> {
         
         String adminUsername = "admin";

         if (userService.findByUsername(adminUsername).isEmpty()) {
            CourseCoordinator admin = new CourseCoordinator();
            admin.setName(adminUsername);
            admin.setEmail("admin@veritas.com");
            admin.setPassword("admin");
            admin.setDepartment("Coordenação"); // Atributo herdado de Professor

            userService.create(admin);
         }
      };
   }  
}