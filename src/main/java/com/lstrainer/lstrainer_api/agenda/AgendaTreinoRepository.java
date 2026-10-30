package com.lstrainer.lstrainer_api.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaTreinoRepository extends JpaRepository<AgendaTreino, Long> {
    List<AgendaTreino> findByAlunoIdAndProfessorId(Long alunoId, Long professorId);
    List<AgendaTreino> findByProfessorIdAndDataBetween(Long professorId, LocalDate inicio, LocalDate fim);
    Optional<AgendaTreino> findByIdAndProfessorId(Long id, Long professorId);
}