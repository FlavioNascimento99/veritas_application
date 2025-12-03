package br.edu.ifpb.veritas.controllers.web;

import java.time.Year;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
/**
 * Controlador responsável por páginas: Login, Homepage da Aplicação e Login com sucesso.
 */

@Controller
public class HomeController {

   @ModelAttribute
   public void addCommonAttribute(Model model, HttpSession http) {
      model.addAttribute("SystemName", "VERITAS");
      model.addAttribute("actualYear", Year.now().getValue());
      model.addAttribute("version", "0.0.3");

      // Atualmente não vamos precisar de mais atributos.
   }

   @GetMapping("/")
   public String home(Model model) {
      // Metadados para carregamento de dados
      model.addAttribute("pageTitle", "VERITAS - Aplicação de controle e gerenciamento institucional.");
      model.addAttribute("pageDescription", "A melhor aplicação para controle acadêmico ");
      model.addAttribute("activePage", "home");
      model.addAttribute("mainContent", "pages/index :: mainContent");;

      // Dados para componentes
      model.addAttribute("heroTitle", "Chega de papelada administrativa...");
      model.addAttribute("heroSubTitle", "Conheça a solução definitiva para controle total de projetos e requisições acadêmicas.");
      model.addAttribute("heroBtntext", "Entrar");
      model.addAttribute("heroBtnUrl", "/login");

      return "home";
   }

   @GetMapping("/login")
   public String login(Model model) {
      model.addAttribute("pageTitle", "Login");
      model.addAttribute("activePage", "login");
      model.addAttribute("mainContent", "pages/login :: content");
      return "home";
   }

   @GetMapping("/dashboard")
   public String dashboard(Model model) {
      model.addAttribute("pageTitle", "Dashboard");
      model.addAttribute("activePage", "dashboard");
      model.addAttribute("mainContent", "pages/dashboard :: content");
      return "home";
   }

   @GetMapping("/contact")
   public String contact(Model model) {
      model.addAttribute("pageTitle", "Contato");
      model.addAttribute("activePage", "contact"); 
      model.addAttribute("mainContent", "pages/contact :: content");
      return "home";
   }

   @GetMapping("/about")
   public String about(Model model) {
      model.addAttribute("pageTitle", "Sobre");
      model.addAttribute("activePage", "about");
      model.addAttribute("mainContent", "pages/about :: content");
      return "home";
   }
   
}
