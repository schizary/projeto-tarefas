package com.projeto.tarefas.repositories;

import com.projeto.tarefas.models.StatusTarefa;
import com.projeto.tarefas.models.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByStatusOrderByDataCriacaoDesc(StatusTarefa status);

    List<Tarefa> findAllByOrderByDataCriacaoDesc();
}
