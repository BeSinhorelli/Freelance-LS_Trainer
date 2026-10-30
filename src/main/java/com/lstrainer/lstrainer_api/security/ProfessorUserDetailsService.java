package com.lstrainer.lstrainer_api.security;

import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProfessorUserDetailsService implements UserDetailsService {

    private final ProfessorRepository professorRepository;

    public ProfessorUserDetailsService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Professor não encontrado: " + email));
        return new ProfessorUserDetails(professor);
    }
}