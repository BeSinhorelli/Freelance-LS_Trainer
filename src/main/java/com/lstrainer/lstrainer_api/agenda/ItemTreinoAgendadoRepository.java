package com.lstrainer.lstrainer_api.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemTreinoAgendadoRepository extends JpaRepository<ItemTreinoAgendado, Long> {
    List<ItemTreinoAgendado> findByTreinoAgendadoIdOrderByOrdemAsc(Long treinoId);
    void deleteByTreinoAgendadoId(Long treinoId);
}