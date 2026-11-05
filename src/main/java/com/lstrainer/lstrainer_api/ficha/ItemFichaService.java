package com.lstrainer.lstrainer_api.ficha;

import com.lstrainer.lstrainer_api.config.SanitizationService;
import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.exercicio.Exercicio;
import com.lstrainer.lstrainer_api.exercicio.ExercicioRepository;
import com.lstrainer.lstrainer_api.ficha.dto.ItemFichaRequest;
import com.lstrainer.lstrainer_api.ficha.dto.ItemFichaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemFichaService {

    private final ItemFichaTreinoRepository itemRepository;
    private final FichaTreinoRepository fichaRepository;
    private final ExercicioRepository exercicioRepository;
    private final SanitizationService sanitizer; // INJETADO

    public ItemFichaService(ItemFichaTreinoRepository itemRepository,
                            FichaTreinoRepository fichaRepository,
                            ExercicioRepository exercicioRepository,
                            SanitizationService sanitizer) {
        this.itemRepository = itemRepository;
        this.fichaRepository = fichaRepository;
        this.exercicioRepository = exercicioRepository;
        this.sanitizer = sanitizer;
    }

    @Transactional(readOnly = true)
    public List<ItemFichaResponse> listarPorFicha(Long fichaId, Long professorId) {
        fichaRepository.findByIdAndProfessorId(fichaId, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha", fichaId));

        return itemRepository.findByFichaIdOrderByOrdemAsc(fichaId)
                .stream()
                .map(ItemFichaResponse::from)
                .toList();
    }

    @Transactional
    public ItemFichaResponse adicionar(Long fichaId, ItemFichaRequest request, Long professorId) {
        FichaTreino ficha = fichaRepository.findByIdAndProfessorId(fichaId, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha", fichaId));

        Exercicio exercicio = exercicioRepository.findByIdAndProfessorId(request.exercicioId(), professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício", request.exercicioId()));

        boolean jaExiste = itemRepository.findByFichaIdOrderByOrdemAsc(fichaId)
                .stream()
                .anyMatch(i -> i.getExercicio().getId().equals(exercicio.getId()));
        if (jaExiste) {
            throw new BusinessException("Este exercício já está na ficha");
        }

        ItemFichaTreino item = ItemFichaTreino.builder()
                .ficha(ficha)
                .exercicio(exercicio)
                .ordem(request.ordem())
                .series(request.series())
                .repeticoes(sanitizer.clean(request.repeticoes()))
                .carga(sanitizer.clean(request.carga()))
                .descansoSegundos(request.descansoSegundos())
                .observacoes(sanitizer.clean(request.observacoes()))
                .build();

        return ItemFichaResponse.from(itemRepository.save(item));
    }

    @Transactional
    public void remover(Long itemId, Long professorId) {
        ItemFichaTreino item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item da Ficha", itemId));

        if (!item.getFicha().getProfessor().getId().equals(professorId)) {
            throw new ResourceNotFoundException("Item da Ficha", itemId);
        }

        itemRepository.delete(item);
    }
}