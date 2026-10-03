package com.projeto.tarefas.exceptions;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException paraTarefa(Long id) {
        return new RecursoNaoEncontradoException("Tarefa não encontrada para o id " + id);
    }
}
