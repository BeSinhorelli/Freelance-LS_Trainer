package com.lstrainer.lstrainer_api.ficha;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FichaTreinoRepository extends JpaRepository<FichaTreino, Long> {
    Page<FichaTreino> findByProfessorId(Long professorId, Pageable pageable);
    Page<FichaTreino> findByAlunoIdAndProfessorId(Long alunoId, Long professorId, Pageable pageable);
    Optional<FichaTreino> findByIdAndProfessorId(Long id, Long professorId);
    
    long countByProfessorIdAndStatus(Long professorId, String status);
    long countByProfessorIdAndProximaReavaliacaoBetween(Long professorId, LocalDate inicio, LocalDate fim);

    // A MÁGICA ACONTECE AQUI: JOIN FETCH traz o aluno na mesma consulta
    @Query("SELECT f FROM FichaTreino f JOIN FETCH f.aluno a WHERE f.professor.id = :professorId AND f.status = 'ATIVA' ORDER BY f.proximaReavaliacao ASC")
    List<FichaTreino> buscarReavaliacoesProximas(@Param("professorId") Long professorId);
}