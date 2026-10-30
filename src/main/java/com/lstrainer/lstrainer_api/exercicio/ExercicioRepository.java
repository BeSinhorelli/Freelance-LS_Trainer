package com.lstrainer.lstrainer_api.exercicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
    Page<Exercicio> findByProfessorId(Long professorId, Pageable pageable);
    Page<Exercicio> findByProfessorIdAndNomeContainingIgnoreCase(Long professorId, String nome, Pageable pageable);
    Optional<Exercicio> findByIdAndProfessorId(Long id, Long professorId);
}