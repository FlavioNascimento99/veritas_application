package br.edu.ifpb.veritas.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import br.edu.ifpb.veritas.services.StudentService;
import br.edu.ifpb.veritas.services.exceptions.ResourceNotFoundException;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student create(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student update(Long id, Student student) {
        Student existing = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setPassword(student.getPassword());
        existing.setRegistrationNumber(student.getRegistrationNumber());
        existing.setCourse(student.getCourse());
        return studentRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Student existing = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        studentRepository.delete(existing);
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
}
