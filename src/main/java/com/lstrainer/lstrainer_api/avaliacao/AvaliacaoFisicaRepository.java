package com.lstrainer.lstrainer_api.avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, Long> {
    List<AvaliacaoFisica> findByAlunoIdAndProfessorIdOrderByDataDesc(Long alunoId, Long professorId);
    Optional<AvaliacaoFisica> findByIdAndProfessorId(Long id, Long professorId);
}