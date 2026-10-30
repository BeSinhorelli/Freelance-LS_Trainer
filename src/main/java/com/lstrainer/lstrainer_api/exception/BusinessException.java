package com.lstrainer.lstrainer_api.exception;

/**
 * Lançada quando uma regra de negócio é violada (ex: e-mail duplicado,
 * ficha sem aluno, exercício repetido na mesma ficha).
 * Retorna HTTP 422 Unprocessable Entity (ou 400, conforme o caso).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}