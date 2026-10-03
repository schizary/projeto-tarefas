package com.projeto.tarefas.models;

public enum StatusTarefa {

    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean ehFinalizada() {
        return this == CONCLUIDA || this == CANCELADA;
    }
}
