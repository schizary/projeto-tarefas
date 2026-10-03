package com.projeto.tarefas.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime momento,
        int codigo,
        String erro,
        String mensagem,
        List<String> detalhes
) {

    public static ErroResponse de(int codigo, String erro, String mensagem) {
        return new ErroResponse(LocalDateTime.now(), codigo, erro, mensagem, List.of());
    }

    public static ErroResponse de(int codigo, String erro, String mensagem, List<String> detalhes) {
        return new ErroResponse(LocalDateTime.now(), codigo, erro, mensagem, detalhes);
    }
}
