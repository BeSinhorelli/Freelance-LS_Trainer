package com.lstrainer.lstrainer_api.exercicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    // Query segura utilizando CAST e COALESCE para evitar erros de tipo no PostgreSQL
    @Query("SELECT e FROM Exercicio e WHERE e.professor.id = :professorId " +
           "AND (:nome IS NULL OR LOWER(e.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) " +
           "AND (:grupo IS NULL OR e.grupoMuscular = :grupo) " +
           "AND (:ativo IS NULL OR e.ativo = :ativo) " +
           "ORDER BY e.nome")
    Page<Exercicio> buscarComFiltros(
            @Param("professorId") Long professorId,
            @Param("nome") String nome,
            @Param("grupo") String grupo,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );

    Page<Exercicio> findByProfessorId(Long professorId, Pageable pageable);
    Optional<Exercicio> findByIdAndProfessorId(Long id, Long professorId);
}