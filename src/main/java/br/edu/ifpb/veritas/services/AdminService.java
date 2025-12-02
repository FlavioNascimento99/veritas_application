package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.*;
import br.edu.ifpb.veritas.repositories.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Fiz um CRUD básico, mas precisa rever
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CollegiateService collegiateService;
    private final SubjectService subjectService;

    // -------------------- CRUD da própria class admin --------------------
    @Transactional
    public Administrator create(Administrator admin) {
        if (admin.getLogin() != null && adminRepository.findByLogin(admin.getLogin()).isPresent()) {
            throw new ResourceNotFoundException("Login já cadastrado.");
        }
        if (admin.getRegister() != null && adminRepository.findByRegister(admin.getRegister()).isPresent()) {
            throw new ResourceNotFoundException("Matrícula já cadastrada.");
        }
        return adminRepository.save(admin);
    }

    public List<Administrator> listAdmins() {
        return adminRepository.findAll();
    }

    public Administrator findAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado."));
    }

    @Transactional
    public Administrator update(Long id, Administrator payload) {
        Administrator current = findAdminById(id);
        current.setName(payload.getName());
        current.setPhoneNumber(payload.getPhoneNumber());
        current.setLogin(payload.getLogin());
        current.setPassword(payload.getPassword());
        current.setRegister(payload.getRegister());
        return adminRepository.save(current);
    }

//    @Transactional
//    public void delete(Long id) {
//        Administrator current = findById(id);
//        adminRepository.delete(current);
//    }

    // Ao invés de excluir, apenas desativa o admin
    @Transactional
    public void deactivate(Long id) {
        Administrator current = findAdminById(id);

        if (!current.getIsActive()) {
            throw new ResourceNotFoundException("Administrador já está desativado.");
        }
        current.setIsActive(false);
        adminRepository.save(current);
    }

    @Transactional
    public void reactivate(Long id) {
        Administrator current = findAdminById(id);

        if (current.getIsActive()) {
            throw new ResourceNotFoundException("Administrador já está ativo.");
        }
        current.setIsActive(true);
        adminRepository.save(current);
    }

    // Requisito funcional #14

    // -------------------- CRUD de alunos --------------------
    public Student createStudent(Student student) {
        return studentService.create(student);
    }

    public List<Student> listStudents() {
        return studentService.findAll();
    }

    public Student findStudentById(Long id) {
        return studentService.findById(id);
    }

    public Student updateStudent(Long id, Student payload) {
        return studentService.update(id, payload);
    }

    @Transactional
    public void deactivateStudent(Long id) {
        studentService.desactivate(id);
    }

    @Transactional
    public void reactivateStudent(Long id) {
        studentService.reactivate(id);
    }

    // -------------------- CRUD de professores/coordenadores --------------------
    public Professor createProfessor(Professor professor) {
        return professorService.create(professor);
    }

    public List<Professor> listProfessors() {
        return professorService.findAll();
    }

    public Professor findProfessorById(Long id) {
        return professorService.findById(id);
    }

    public Professor updateProfessor(Long id, Professor payload) {
        return professorService.update(id, payload);
    }

    @Transactional
    public void toggleProfessorActive(Long id) {
        professorService.activeStateChanger(id);
    }

    // Seta um professor como coordenador
    // ou deixa-o como professor comum
    @Transactional
    public void toggleCoordinator(Long id) {
        professorService.coordinatorStateChanger(id);
    }

    // Requisito funcional #13

    // -------------------- CRUD de colegiados --------------------
    public Collegiate createCollegiate(Collegiate collegiate) {
        return collegiateService.create(collegiate);
    }

    public List<Collegiate> listCollegiates() {
        return collegiateService.findAll();
    }

    public Collegiate findCollegiateById(Long id) {
        return collegiateService.findById(id);
    }

    public Collegiate updateCollegiate(Long id, Collegiate payload) {
        return collegiateService.update(id, payload);
    }

    // Creio que, no caso de colegiados, não há problema em exclui-los
    @Transactional
    public void deleteCollegiate(Long id) {
        collegiateService.delete(id);
    }

    public List<Professor> listCollegiateMembers(Long collegiateId) {
        return collegiateService.findProfessorsByCollegiate(collegiateId);
    }

    // Requisito funcional #15
    // -------------------- CRUD de assuntos de processos --------------------
    public Subject createSubject(Subject subject) {
        return subjectService.create(subject);
    }

    public List<Subject> listSubjects() {
        return subjectService.findAll();
    }

    public Subject findSubjectById(Long id) {
        return subjectService.findById(id);
    }

    public Subject updateSubject(Long id, Subject payload) {
        return subjectService.update(id, payload);
    }

    @Transactional
    public void deactivateSubject(Long id) {
        subjectService.deactivate(id);
    }

    @Transactional
    public void reactivateSubject(Long id) {
        subjectService.reactivate(id);
    }

}