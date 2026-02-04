package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Student create(Student student) {
        if (student.getLogin() != null && studentRepository.findByLogin(student.getLogin()).isPresent()) {
            throw new ResourceNotFoundException("Login já cadastrado.");
        }
        if (student.getRegister() != null && studentRepository.findByRegister(student.getRegister()).isPresent()) {
            throw new ResourceNotFoundException("Matrícula já cadastrada.");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado."));
    }

    @Transactional
    public Student update(Long id, Student payload) {
        Student currentStudent = findById(id);
        currentStudent.setName(payload.getName());
        currentStudent.setPhoneNumber(payload.getPhoneNumber());
        currentStudent.setLogin(payload.getLogin());
        currentStudent.setRegister(payload.getRegister());

        if (payload.getPassword() != null && !payload.getPassword().isEmpty()) {
            currentStudent.setPassword(passwordEncoder.encode(payload.getPassword()));
        }
        return studentRepository.save(currentStudent);
    }

    @Transactional
    public void desactivate(Long id) {
        Student currentStudent = findById(id);
        currentStudent.setIsActive(false);
        studentRepository.save(currentStudent);
    }

    @Transactional
    public void reactivate(Long id) {
        Student currentStudent = findById(id);
        if (!currentStudent.getIsActive()) {
            currentStudent.setIsActive(true);
        }
        studentRepository.save(currentStudent);
    }

    public Optional<Student> findByLogin(String login) {
        return studentRepository.findByLogin(login);
    }

    public Optional<Student> findByRegister(String register) {
        return studentRepository.findByRegister(register);
    }
}
