package com.lstrainer.lstrainer_api.ficha;

import com.lstrainer.lstrainer_api.ficha.dto.ItemFichaRequest;
import com.lstrainer.lstrainer_api.ficha.dto.ItemFichaResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichas/{fichaId}/itens")
public class ItemFichaController {

    private final ItemFichaService itemService;

    public ItemFichaController(ItemFichaService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemFichaResponse>> listar(
            @PathVariable Long fichaId,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(itemService.listarPorFicha(fichaId, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<ItemFichaResponse> adicionar(
            @PathVariable Long fichaId,
            @Valid @RequestBody ItemFichaRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.adicionar(fichaId, request, user.getProfessorId()));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> remover(
            @PathVariable Long fichaId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        itemService.remover(itemId, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}