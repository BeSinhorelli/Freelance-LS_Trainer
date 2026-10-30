package com.lstrainer.lstrainer_api.cardio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescricaoCardioRepository extends JpaRepository<PrescricaoCardio, Long> {
    List<PrescricaoCardio> findByAlunoIdAndProfessorId(Long alunoId, Long professorId);
    Optional<PrescricaoCardio> findByIdAndProfessorId(Long id, Long professorId);
}