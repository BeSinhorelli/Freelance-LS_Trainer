package com.lstrainer.lstrainer_api.security;

import com.lstrainer.lstrainer_api.professor.Professor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ProfessorUserDetails implements UserDetails {

    private final Professor professor;

    public ProfessorUserDetails(Professor professor) {
        this.professor = professor;
    }

    public Long getProfessorId() {
        return professor.getId();
    }

    public String getNome() {
        return professor.getNome();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
    }

    @Override
    public String getPassword() {
        return professor.getSenhaHash();
    }

    @Override
    public String getUsername() {
        return professor.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return Boolean.TRUE.equals(professor.getAtivo()); }
}