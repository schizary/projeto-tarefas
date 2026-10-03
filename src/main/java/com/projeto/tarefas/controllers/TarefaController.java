package com.projeto.tarefas.controllers;

import com.projeto.tarefas.dtos.StatusRequest;
import com.projeto.tarefas.dtos.TarefaRequest;
import com.projeto.tarefas.dtos.TarefaResponse;
import com.projeto.tarefas.models.StatusTarefa;
import com.projeto.tarefas.services.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@Tag(name = "Tarefas", description = "Gerenciamento das tarefas do dia a dia")
public class TarefaController {

    private final TarefaService servico;

    public TarefaController(TarefaService servico) {
        this.servico = servico;
    }

    @PostMapping
    @Operation(summary = "Cria uma nova tarefa")
    public ResponseEntity<TarefaResponse> criar(@RequestBody @Valid TarefaRequest requisicao,
                                                UriComponentsBuilder construtorDeUri) {
        TarefaResponse resposta = servico.criar(requisicao);
        URI endereco = construtorDeUri.path("/api/tarefas/{id}")
                .buildAndExpand(resposta.id())
                .toUri();
        return ResponseEntity.created(endereco).body(resposta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera todos os dados de uma tarefa")
    public ResponseEntity<TarefaResponse> alterar(@PathVariable Long id,
                                                  @RequestBody @Valid TarefaRequest requisicao) {
        return ResponseEntity.ok(servico.alterar(id, requisicao));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera somente o status de uma tarefa")
    public ResponseEntity<TarefaResponse> alterarStatus(@PathVariable Long id,
                                                        @RequestBody @Valid StatusRequest requisicao) {
        return ResponseEntity.ok(servico.alterarStatus(id, requisicao.status()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma tarefa")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servico.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma tarefa pelo id")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servico.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista as tarefas, opcionalmente filtrando por status")
    public ResponseEntity<List<TarefaResponse>> listar(@RequestParam(required = false) StatusTarefa status) {
        return ResponseEntity.ok(servico.listar(status));
    }
}
