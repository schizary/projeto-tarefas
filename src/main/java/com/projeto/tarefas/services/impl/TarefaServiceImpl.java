package com.projeto.tarefas.services.impl;

import com.projeto.tarefas.dtos.TarefaRequest;
import com.projeto.tarefas.dtos.TarefaResponse;
import com.projeto.tarefas.exceptions.RecursoNaoEncontradoException;
import com.projeto.tarefas.mappers.TarefaMapper;
import com.projeto.tarefas.models.StatusTarefa;
import com.projeto.tarefas.models.Tarefa;
import com.projeto.tarefas.repositories.TarefaRepository;
import com.projeto.tarefas.services.TarefaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaServiceImpl implements TarefaService {

    private final TarefaRepository repositorio;
    private final TarefaMapper conversor;

    public TarefaServiceImpl(TarefaRepository repositorio, TarefaMapper conversor) {
        this.repositorio = repositorio;
        this.conversor = conversor;
    }

    @Override
    @Transactional
    public TarefaResponse criar(TarefaRequest requisicao) {
        Tarefa tarefa = conversor.paraEntidade(requisicao);
        return conversor.paraResposta(repositorio.save(tarefa));
    }

    @Override
    @Transactional
    public TarefaResponse alterar(Long id, TarefaRequest requisicao) {
        Tarefa tarefa = obterTarefa(id);
        tarefa.alterarDados(
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.status(),
                requisicao.observacoes()
        );
        return conversor.paraResposta(repositorio.save(tarefa));
    }

    @Override
    @Transactional
    public TarefaResponse alterarStatus(Long id, StatusTarefa status) {
        Tarefa tarefa = obterTarefa(id);
        tarefa.alterarStatus(status);
        return conversor.paraResposta(repositorio.save(tarefa));
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        repositorio.delete(obterTarefa(id));
    }

    @Override
    @Transactional(readOnly = true)
    public TarefaResponse buscarPorId(Long id) {
        return conversor.paraResposta(obterTarefa(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarefaResponse> listar(StatusTarefa status) {
        List<Tarefa> tarefas = status == null
                ? repositorio.findAllByOrderByDataCriacaoDesc()
                : repositorio.findByStatusOrderByDataCriacaoDesc(status);

        return tarefas.stream().map(conversor::paraResposta).toList();
    }

    private Tarefa obterTarefa(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.paraTarefa(id));
    }
}
