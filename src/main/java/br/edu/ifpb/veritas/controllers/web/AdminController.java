package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.services.ProfessorService;
import br.edu.ifpb.veritas.services.StudentService;
import br.edu.ifpb.veritas.services.SubjectService;
import br.edu.ifpb.veritas.services.ProcessService;
import br.edu.ifpb.veritas.services.CollegiateService;
import br.edu.ifpb.veritas.UI.NavePageBuilder;
import br.edu.ifpb.veritas.UI.NavPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final SubjectService subjectService;
    private final ProcessService processService;
    private final CollegiateService collegiateService;

    // --- CADASTRO DE ESTUDANTE ---
    @GetMapping("/students/new")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("pageTitle", "Cadastrar Estudante");
        return "pages/admin/new-student";
    }

    @PostMapping("/students")
    public String createStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.create(student);
            redirectAttributes.addFlashAttribute("successMessage", "Estudante cadastrado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/students/new";
        }
        return "redirect:/dashboard";
    }

    // --- CADASTRO DE PROFESSOR ---
    @GetMapping("/professors/new")
    public String showProfessorForm(Model model) {
        model.addAttribute("professor", new Professor());
        model.addAttribute("pageTitle", "Cadastrar Professor");
        return "pages/admin/new-professor";
    }

    @PostMapping("/professors")
    public String createProfessor(@ModelAttribute Professor professor, RedirectAttributes redirectAttributes) {
        try {
            professorService.create(professor);
            redirectAttributes.addFlashAttribute("successMessage", "Professor cadastrado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/professors/new";
        }
        return "redirect:/dashboard";
    }

    // --- LISTAGEM DE ESTUDANTES (PAGINADA) ---
    @GetMapping("/students")
    public String listStudents(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Page<Student> studentPage = studentService.findAll(PageRequest.of(page, size));
        NavPage nav = NavePageBuilder.newNavPage(studentPage.getNumber(), studentPage.getTotalElements(), studentPage.getTotalPages(), studentPage.getSize());
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("navPage", nav);
        model.addAttribute("activePage", "students");
        model.addAttribute("pageTitle", "Lista de Estudantes");
        return "pages/admin/list-students";
    }

    // --- GERENCIAMENTO DE PROCESSOS (PAGINADO) ---
    @GetMapping("/processes")
    public String listProcesses(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Page<Process> processPage = processService.findAllProcesses(PageRequest.of(page, size));
        NavPage nav = NavePageBuilder.newNavPage(processPage.getNumber(), processPage.getTotalElements(), processPage.getTotalPages(), processPage.getSize());
        model.addAttribute("processPage", processPage);
        model.addAttribute("navPage", nav);
        model.addAttribute("activePage", "processes");
        model.addAttribute("pageTitle", "Gerenciar Processos");
        return "pages/admin/processes";
    }

    // --- LISTAGENS DE USUÁRIOS (Professores e Estudantes) ---
    @GetMapping("/professors")
    public String listProfessors(Model model) {
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("professors", professorService.findAll());
        model.addAttribute("pageTitle", "Gerenciar Usuários");
        return "pages/admin/manage-users";
    }

    // --- GERENCIAMENTO DE USUÁRIOS ---
    @GetMapping("/users")
    public String showUserManagementPage(Model model) {
        model.addAttribute("professors", professorService.findAll());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("pageTitle", "Gerenciamento de Usuários");
        return "pages/admin/manage-users";
    }

    @PostMapping("/professors/{id}/update")
    public String updateProfessor(@PathVariable Long id, @ModelAttribute Professor professor, RedirectAttributes redirectAttributes) {
        try {
            professorService.update(id, professor);
            redirectAttributes.addFlashAttribute("successMessage", "Professor atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar professor: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/students/{id}/update")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        studentService.update(id, student);
        redirectAttributes.addFlashAttribute("successMessage", "Estudante atualizado com sucesso!");
        return "redirect:/admin/users";
    }

    // --- GERENCIAMENTO DE ASSUNTOS ---
    @GetMapping("/subjects")
    public String showSubjectList(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("subject", new Subject()); // Para o formulário de criação na mesma página
        model.addAttribute("pageTitle", "Gerenciar Assuntos");
        return "pages/admin/subjects";
    }

    @PostMapping("/subjects")
    public String createSubject(@ModelAttribute Subject subject, RedirectAttributes redirectAttributes) {
        subjectService.create(subject);
        redirectAttributes.addFlashAttribute("successMessage", "Assunto cadastrado com sucesso!");
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/{id}/deactivate")
    public String deactivateSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        subjectService.deactivate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Assunto desativado com sucesso!");
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/{id}/reactivate")
    public String reactivateSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        subjectService.reactivate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Assunto reativado com sucesso!");
        return "redirect:/admin/subjects";
    }

    // --- GERENCIAMENTO DE COLEGIADOS ---
    @GetMapping("/collegiates")
    public String listCollegiates(Model model) {
        model.addAttribute("collegiates", collegiateService.findAll());
        model.addAttribute("pageTitle", "Gerenciar Colegiados");
        return "pages/admin/collegiates";
    }
    
}

