package com.lstrainer.lstrainer_api.aluno;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Page<Aluno> findByProfessorIdAndNomeContainingIgnoreCase(Long professorId, String nome, Pageable pageable);

    Page<Aluno> findByProfessorId(Long professorId, Pageable pageable);

    Optional<Aluno> findByIdAndProfessorId(Long id, Long professorId);

    // NOVO MÉTODO: Conta os alunos do professor diretamente no banco de dados
    long countByProfessorId(Long professorId);
}