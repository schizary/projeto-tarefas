package com.projeto.tarefas.services;

import com.projeto.tarefas.dtos.TarefaRequest;
import com.projeto.tarefas.dtos.TarefaResponse;
import com.projeto.tarefas.models.StatusTarefa;

import java.util.List;

public interface TarefaService {

    TarefaResponse criar(TarefaRequest requisicao);

    TarefaResponse alterar(Long id, TarefaRequest requisicao);

    TarefaResponse alterarStatus(Long id, StatusTarefa status);

    void deletar(Long id);

    TarefaResponse buscarPorId(Long id);

    List<TarefaResponse> listar(StatusTarefa status);
}
