package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.Estacao;
import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.util.Valores;

public final class CadastroEstacoes {
    private final EstadoSistema estado;

    private final int bateriaMinima;

    public CadastroEstacoes(EstadoSistema estado, int bateriaMinima) {
        this.estado = estado;
        this.bateriaMinima = bateriaMinima;
    }

    public String criar(String nome, String endereco, String capacidade) {
        Valores.obrigatorio(nome, "Nome nao pode ser nulo.");
        Valores.obrigatorio(endereco, "Endereco nao pode ser nulo.");
        Valores.obrigatorio(capacidade, "Capacidade nao pode ser nula.");
        int vagas;
        try {
            vagas = Integer.parseInt(capacidade.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Capacidade deve ser numerica.");
        }
        if (vagas <= 0) throw new IllegalArgumentException("Capacidade deve ser positiva.");

        String id = "E" + estado.proximaEstacao++;
        estado.estacoes.put(id, new Estacao(id, nome, endereco, vagas));
        return id;
    }

    public String vagasDisponiveis(String id) {
        return Integer.toString(buscar(id).vagasLivres());
    }

    public String bicicletasDisponiveis(String id) {
        Estacao estacao = buscar(id);
        int disponiveis = 0;
        for (Bicicleta bicicleta : estado.bicicletas.values()) {
            if (estacao.id().equals(bicicleta.estacao()) && bicicleta.disponivel(bateriaMinima)) {
                disponiveis++;
            }
        }
        return Integer.toString(disponiveis);
    }

    Estacao buscar(String id) {
        Valores.obrigatorio(id, "Identificacao da estacao nao pode ser nula.");
        Estacao estacao = estado.estacoes.get(id);
        if (estacao == null) throw new IllegalArgumentException("Estacao nao existe.");
        return estacao;
    }
}
