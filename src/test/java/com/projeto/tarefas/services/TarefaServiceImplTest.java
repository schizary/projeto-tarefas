package com.projeto.tarefas.services;

import com.projeto.tarefas.dtos.TarefaRequest;
import com.projeto.tarefas.dtos.TarefaResponse;
import com.projeto.tarefas.exceptions.RecursoNaoEncontradoException;
import com.projeto.tarefas.mappers.TarefaMapper;
import com.projeto.tarefas.models.StatusTarefa;
import com.projeto.tarefas.models.Tarefa;
import com.projeto.tarefas.repositories.TarefaRepository;
import com.projeto.tarefas.services.impl.TarefaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários do serviço de tarefas")
class TarefaServiceImplTest {

    @Mock
    private TarefaRepository repositorio;

    @Captor
    private ArgumentCaptor<Tarefa> capturadorDeTarefa;

    private TarefaServiceImpl servico;

    @BeforeEach
    void preparar() {
        servico = new TarefaServiceImpl(repositorio, new TarefaMapper());
    }

    @Test
    @DisplayName("deve criar uma tarefa com os dados informados")
    void deveCriarTarefa() {
        TarefaRequest requisicao = new TarefaRequest(
                "Comprar pão", "Padaria da esquina", StatusTarefa.PENDENTE, "pela manhã");
        when(repositorio.save(any(Tarefa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        TarefaResponse resposta = servico.criar(requisicao);

        verify(repositorio).save(capturadorDeTarefa.capture());
        Tarefa salva = capturadorDeTarefa.getValue();
        assertThat(salva.getNome()).isEqualTo("Comprar pão");
        assertThat(salva.getDescricao()).isEqualTo("Padaria da esquina");
        assertThat(salva.getObservacoes()).isEqualTo("pela manhã");
        assertThat(resposta.status()).isEqualTo(StatusTarefa.PENDENTE);
        assertThat(resposta.descricaoStatus()).isEqualTo("Pendente");
    }

    @Test
    @DisplayName("deve assumir o status PENDENTE quando nenhum for informado")
    void deveAssumirStatusPendente() {
        TarefaRequest requisicao = new TarefaRequest("Lavar o carro", null, null, null);
        when(repositorio.save(any(Tarefa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        TarefaResponse resposta = servico.criar(requisicao);

        assertThat(resposta.status()).isEqualTo(StatusTarefa.PENDENTE);
    }

    @Test
    @DisplayName("deve alterar os dados de uma tarefa existente")
    void deveAlterarTarefa() {
        Tarefa existente = criarTarefaComId(1L, "Nome antigo", StatusTarefa.PENDENTE);
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorio.save(any(Tarefa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        TarefaRequest requisicao = new TarefaRequest(
                "Nome novo", "Nova descrição", StatusTarefa.EM_ANDAMENTO, "nova observação");
        TarefaResponse resposta = servico.alterar(1L, requisicao);

        assertThat(resposta.nome()).isEqualTo("Nome novo");
        assertThat(resposta.descricao()).isEqualTo("Nova descrição");
        assertThat(resposta.status()).isEqualTo(StatusTarefa.EM_ANDAMENTO);
        assertThat(resposta.observacoes()).isEqualTo("nova observação");
    }

    @Test
    @DisplayName("deve manter o status atual quando a alteração não informar um novo")
    void deveManterStatusAoAlterarSemInformar() {
        Tarefa existente = criarTarefaComId(1L, "Estudar", StatusTarefa.EM_ANDAMENTO);
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorio.save(any(Tarefa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        TarefaResponse resposta = servico.alterar(1L, new TarefaRequest("Estudar mais", null, null, null));

        assertThat(resposta.status()).isEqualTo(StatusTarefa.EM_ANDAMENTO);
    }

    @Test
    @DisplayName("deve alterar somente o status da tarefa")
    void deveAlterarApenasOStatus() {
        Tarefa existente = criarTarefaComId(5L, "Pagar a conta", StatusTarefa.PENDENTE);
        when(repositorio.findById(5L)).thenReturn(Optional.of(existente));
        when(repositorio.save(any(Tarefa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        TarefaResponse resposta = servico.alterarStatus(5L, StatusTarefa.CONCLUIDA);

        assertThat(resposta.nome()).isEqualTo("Pagar a conta");
        assertThat(resposta.status()).isEqualTo(StatusTarefa.CONCLUIDA);
        assertThat(resposta.status().ehFinalizada()).isTrue();
    }

    @Test
    @DisplayName("deve deletar a tarefa encontrada")
    void deveDeletarTarefa() {
        Tarefa existente = criarTarefaComId(7L, "Tarefa descartável", StatusTarefa.PENDENTE);
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));

        servico.deletar(7L);

        verify(repositorio).delete(existente);
    }

    @Test
    @DisplayName("deve lançar exceção ao deletar uma tarefa inexistente")
    void deveFalharAoDeletarTarefaInexistente() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servico.deletar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");

        verify(repositorio, never()).delete(any(Tarefa.class));
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar uma tarefa inexistente")
    void deveFalharAoBuscarTarefaInexistente() {
        when(repositorio.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servico.buscarPorId(42L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("deve listar todas as tarefas quando nenhum status for informado")
    void deveListarTodasAsTarefas() {
        when(repositorio.findAllByOrderByDataCriacaoDesc()).thenReturn(List.of(
                criarTarefaComId(1L, "Primeira", StatusTarefa.PENDENTE),
                criarTarefaComId(2L, "Segunda", StatusTarefa.CONCLUIDA)
        ));

        List<TarefaResponse> respostas = servico.listar(null);

        assertThat(respostas).hasSize(2);
        assertThat(respostas).extracting(TarefaResponse::nome).containsExactly("Primeira", "Segunda");
        verify(repositorio, never()).findByStatusOrderByDataCriacaoDesc(any());
    }

    @Test
    @DisplayName("deve filtrar as tarefas pelo status informado")
    void deveListarTarefasPorStatus() {
        when(repositorio.findByStatusOrderByDataCriacaoDesc(StatusTarefa.PENDENTE))
                .thenReturn(List.of(criarTarefaComId(1L, "Primeira", StatusTarefa.PENDENTE)));

        List<TarefaResponse> respostas = servico.listar(StatusTarefa.PENDENTE);

        assertThat(respostas).hasSize(1);
        assertThat(respostas.getFirst().status()).isEqualTo(StatusTarefa.PENDENTE);
        verify(repositorio, never()).findAllByOrderByDataCriacaoDesc();
    }

    private Tarefa criarTarefaComId(Long id, String nome, StatusTarefa status) {
        Tarefa tarefa = new Tarefa(nome, "descrição", status, "observações");
        ReflectionTestUtils.setField(tarefa, "id", id);
        return tarefa;
    }
}
