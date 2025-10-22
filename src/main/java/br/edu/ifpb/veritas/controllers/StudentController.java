package br.edu.ifpb.veritas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ifpb.veritas.services.StudentService;

@Controller
@RequestMapping("/student")
public class StudentController {

   private final StudentService studentService;

   @Autowired
   public StudentController(StudentService studentService) {
       this.studentService = studentService;
   }

   @GetMapping("/cases/new")
   public String showNewCaseForm() {
      return "student/new-case-form";
   }

   @PostMapping("/cases") 
   public String createCase() {
      // Use studentService to create student-related resources if needed
      return "redirect:/student/cases";
   }

   @GetMapping("/cases")
   public String listMyCases() {
      return "student/my-cases";
   }

   @PostMapping("/cases/{id}/documents")
   public String uploadDocument(Long id) {
      return "redirect:/student/cases/" + id;
   }
}
