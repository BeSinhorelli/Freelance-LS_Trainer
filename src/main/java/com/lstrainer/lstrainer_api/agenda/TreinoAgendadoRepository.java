package com.lstrainer.lstrainer_api.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TreinoAgendadoRepository extends JpaRepository<TreinoAgendado, Long> {
    List<TreinoAgendado> findByAlunoIdAndProfessorIdOrderByDataAsc(Long alunoId, Long professorId);
    Optional<TreinoAgendado> findByIdAndProfessorId(Long id, Long professorId);
}