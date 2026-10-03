package com.projeto.tarefas.dtos;

import com.projeto.tarefas.models.StatusTarefa;

import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String nome,
        String descricao,
        StatusTarefa status,
        String descricaoStatus,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
