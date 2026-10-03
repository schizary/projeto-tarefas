package com.projeto.tarefas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        ErroResponse corpo = ErroResponse.de(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                excecao.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException excecao) {
        List<String> detalhes = excecao.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatarErroDeCampo)
                .toList();

        ErroResponse corpo = ErroResponse.de(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Verifique os campos informados",
                detalhes
        );
        return ResponseEntity.badRequest().body(corpo);
    }

    private String formatarErroDeCampo(FieldError erro) {
        return erro.getField() + ": " + erro.getDefaultMessage();
    }
}
