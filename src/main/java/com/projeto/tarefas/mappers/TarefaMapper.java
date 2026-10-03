package com.projeto.tarefas.mappers;

import com.projeto.tarefas.dtos.TarefaRequest;
import com.projeto.tarefas.dtos.TarefaResponse;
import com.projeto.tarefas.models.Tarefa;
import org.springframework.stereotype.Component;

@Component
public class TarefaMapper {

    public Tarefa paraEntidade(TarefaRequest requisicao) {
        return new Tarefa(
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.status(),
                requisicao.observacoes()
        );
    }

    public TarefaResponse paraResposta(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getNome(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getStatus() == null ? null : tarefa.getStatus().getDescricao(),
                tarefa.getObservacoes(),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao()
        );
    }
}
