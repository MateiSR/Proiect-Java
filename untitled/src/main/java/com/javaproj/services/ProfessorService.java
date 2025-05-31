package com.javaproj.services;

import com.javaproj.db.Professor;
import com.javaproj.db.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javaproj.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    @Autowired
    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Transactional
    public Professor createProfessor(Professor professor) {
        return professorRepository.save(professor);
    }

    @Transactional(readOnly = true)
    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Professor> getProfessorById(Integer professorId) {
        return professorRepository.findById(professorId);
    }

    @Transactional(readOnly = true)
    public Optional<Professor> getProfessorByEmail(String email) {
        return professorRepository.findByEmail(email);
    }

    @Transactional
    public Professor updateProfessor(Integer professorId, Professor professorDetails) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with id: " + professorId));

        professor.setFirstName(professorDetails.getFirstName());
        professor.setLastName(professorDetails.getLastName());
        professor.setEmail(professorDetails.getEmail());
        professor.setDepartment(professorDetails.getDepartment());
        professor.setOffice(professorDetails.getOffice());

        return professorRepository.save(professor);
    }

    @Transactional
    public void deleteProfessor(Integer professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with id: " + professorId));
        professorRepository.delete(professor);
    }
}
