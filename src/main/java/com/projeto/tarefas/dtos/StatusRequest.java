package com.projeto.tarefas.dtos;

import com.projeto.tarefas.models.StatusTarefa;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(

        @NotNull(message = "O status é obrigatório")
        StatusTarefa status
) {
}
