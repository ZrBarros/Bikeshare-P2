package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.model.Estacao;
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
        int vagas = Valores.inteiro(capacidade, "Capacidade nao pode ser nula.", "Capacidade deve ser numerica.");
        return estado.cadastrarEstacao(id -> new Estacao(id, nome, endereco, vagas));
    }

    public String vagasDisponiveis(String id) {
        return Integer.toString(buscar(id).vagasLivres());
    }

    public String bicicletasDisponiveis(String id) {
        Estacao estacao = buscar(id);
        int disponiveis = 0;
        for (Bicicleta bicicleta : estado.bicicletas()) {
            if (estacao.id().equals(bicicleta.estacao()) && bicicleta.disponivel(bateriaMinima)) {
                disponiveis++;
            }
        }
        return Integer.toString(disponiveis);
    }

    Estacao buscar(String id) { return estado.estacao(id); }
}
