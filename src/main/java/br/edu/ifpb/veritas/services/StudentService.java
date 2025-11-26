package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional
    public Student create(Student student) {
        if (student.getLogin() != null && studentRepository.findByLogin(student.getLogin()).isPresent()) {
            throw new ResourceNotFoundException("Login já cadastrado.");
        }
        // A verificação da matrícula é discutível
        if (student.getRegister() != null && studentRepository.findByRegister(student.getRegister()).isPresent()) {
            throw new ResourceNotFoundException("Matrícula já cadastrada.");
        }
        return studentRepository.save(student);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
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
        currentStudent.setPassword(payload.getPassword());
        currentStudent.setRegister(payload.getRegister());
        return studentRepository.save(currentStudent);
    }


    /**
     * Não deletaremos nenhum tipo de informação, seja lá qual for.
     * Todos os dados que irão compor o sistema deve ser considerado 
     * 'sensível'.
     * 
     * Métodos de alteração de estado.
     */
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
    // A partir daqui irei colocar
    // os requisitos específicos do projeto

}
