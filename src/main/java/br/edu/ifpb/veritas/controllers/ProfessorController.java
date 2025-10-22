package br.edu.ifpb.veritas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ifpb.veritas.services.ProfessorService;

@Controller
@RequestMapping("/professor")
public class ProfessorController {
   
   private final ProfessorService professorService;

   @Autowired
   public ProfessorController(ProfessorService professorService) {
       this.professorService = professorService;
   }

   @GetMapping("/cases")
   public String listMyRapporteur() {
      return "professor/rapporteur-cases";
   }

   @GetMapping("/meetings")
   public String listMeetings() {
      return "professor/meetings-list";
   }
}
