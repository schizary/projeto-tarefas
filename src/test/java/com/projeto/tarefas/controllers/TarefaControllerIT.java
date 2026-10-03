package com.projeto.tarefas.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.tarefas.dtos.StatusRequest;
import com.projeto.tarefas.dtos.TarefaRequest;
import com.projeto.tarefas.models.StatusTarefa;
import com.projeto.tarefas.models.Tarefa;
import com.projeto.tarefas.repositories.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de integração da API de tarefas")
class TarefaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper conversorJson;

    @Autowired
    private TarefaRepository repositorio;

    @BeforeEach
    void limparBase() {
        repositorio.deleteAll();
    }

    @Test
    @DisplayName("POST /api/tarefas deve criar a tarefa e devolver 201")
    void deveCriarTarefa() throws Exception {
        TarefaRequest requisicao = new TarefaRequest(
                "Comprar pão", "Padaria da esquina", StatusTarefa.PENDENTE, "pela manhã");

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversorJson.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Comprar pão"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.descricaoStatus").value("Pendente"))
                .andExpect(jsonPath("$.dataCriacao").exists())
                .andExpect(jsonPath("$.dataAtualizacao").exists());

        assertThat(repositorio.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/tarefas deve devolver 400 quando o nome estiver em branco")
    void deveRecusarTarefaSemNome() throws Exception {
        TarefaRequest requisicao = new TarefaRequest("   ", null, null, null);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversorJson.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(400))
                .andExpect(jsonPath("$.detalhes[0]").value("nome: O nome da tarefa é obrigatório"));

        assertThat(repositorio.count()).isZero();
    }

    @Test
    @DisplayName("PUT /api/tarefas/{id} deve alterar a tarefa")
    void deveAlterarTarefa() throws Exception {
        Long id = salvarTarefa("Nome antigo", StatusTarefa.PENDENTE).getId();
        TarefaRequest requisicao = new TarefaRequest(
                "Nome novo", "Nova descrição", StatusTarefa.EM_ANDAMENTO, "nova observação");

        mockMvc.perform(put("/api/tarefas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversorJson.writeValueAsString(requisicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome novo"))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));

        Tarefa atualizada = repositorio.findById(id).orElseThrow();
        assertThat(atualizada.getNome()).isEqualTo("Nome novo");
        assertThat(atualizada.getObservacoes()).isEqualTo("nova observação");
    }

    @Test
    @DisplayName("PUT /api/tarefas/{id} deve devolver 404 para um id inexistente")
    void deveDevolver404AoAlterarTarefaInexistente() throws Exception {
        TarefaRequest requisicao = new TarefaRequest("Qualquer", null, null, null);

        mockMvc.perform(put("/api/tarefas/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversorJson.writeValueAsString(requisicao)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404))
                .andExpect(jsonPath("$.mensagem").value("Tarefa não encontrada para o id 9999"));
    }

    @Test
    @DisplayName("PATCH /api/tarefas/{id}/status deve alterar somente o status")
    void deveAlterarStatus() throws Exception {
        Long id = salvarTarefa("Pagar a conta", StatusTarefa.PENDENTE).getId();

        mockMvc.perform(patch("/api/tarefas/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversorJson.writeValueAsString(new StatusRequest(StatusTarefa.CONCLUIDA))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Pagar a conta"))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        assertThat(repositorio.findById(id).orElseThrow().getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
    }

    @Test
    @DisplayName("DELETE /api/tarefas/{id} deve remover a tarefa e devolver 204")
    void deveDeletarTarefa() throws Exception {
        Long id = salvarTarefa("Tarefa descartável", StatusTarefa.PENDENTE).getId();

        mockMvc.perform(delete("/api/tarefas/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(repositorio.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/tarefas/{id} deve devolver 404 para um id inexistente")
    void deveDevolver404AoDeletarTarefaInexistente() throws Exception {
        mockMvc.perform(delete("/api/tarefas/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tarefas deve listar todas as tarefas")
    void deveListarTarefas() throws Exception {
        salvarTarefa("Primeira", StatusTarefa.PENDENTE);
        salvarTarefa("Segunda", StatusTarefa.CONCLUIDA);

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/tarefas?status= deve filtrar pelo status informado")
    void deveFiltrarTarefasPorStatus() throws Exception {
        salvarTarefa("Primeira", StatusTarefa.PENDENTE);
        salvarTarefa("Segunda", StatusTarefa.CONCLUIDA);

        mockMvc.perform(get("/api/tarefas").param("status", "CONCLUIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Segunda"));
    }

    @Test
    @DisplayName("GET /api/tarefas/{id} deve devolver a tarefa buscada")
    void deveBuscarTarefaPorId() throws Exception {
        Long id = salvarTarefa("Estudar Spring", StatusTarefa.EM_ANDAMENTO).getId();

        mockMvc.perform(get("/api/tarefas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.nome").value("Estudar Spring"));
    }

    private Tarefa salvarTarefa(String nome, StatusTarefa status) {
        return repositorio.saveAndFlush(new Tarefa(nome, "descrição", status, "observações"));
    }
}
