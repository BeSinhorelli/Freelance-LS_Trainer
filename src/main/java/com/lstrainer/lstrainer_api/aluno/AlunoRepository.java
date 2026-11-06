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

    long countByProfessorId(Long professorId);

    // NOVO: Verifica se já existe um aluno com o mesmo nome para o professor
    boolean existsByProfessorIdAndNomeIgnoreCase(Long professorId, String nome);
}