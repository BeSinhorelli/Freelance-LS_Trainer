package com.lstrainer.lstrainer_api.exception;

/**
 * Lançada quando um recurso não existe OU quando pertence a outro professor.
 * Retornar 404 (em vez de 403) evita revelar a existência do recurso — previne IDOR.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " não encontrado(a) com id: " + id);
    }
}