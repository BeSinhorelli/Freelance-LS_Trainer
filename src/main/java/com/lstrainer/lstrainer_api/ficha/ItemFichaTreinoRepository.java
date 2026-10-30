package com.lstrainer.lstrainer_api.ficha;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemFichaTreinoRepository extends JpaRepository<ItemFichaTreino, Long> {
    List<ItemFichaTreino> findByFichaIdOrderByOrdemAsc(Long fichaId);
    void deleteByFichaId(Long fichaId);
}