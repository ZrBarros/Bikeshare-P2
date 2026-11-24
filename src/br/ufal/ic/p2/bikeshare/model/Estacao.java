package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Estacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String nome;
    private final String endereco;
    private final int capacidade;
    @SuppressWarnings("serial")
    private final Set<String> bicicletas = new LinkedHashSet<>();

    public Estacao(String id, String nome, String endereco, int capacidade) {
        this.id = Valores.obrigatorio(id, "Identificacao da estacao nao pode ser nula.");
        this.nome = Valores.obrigatorio(nome, "Nome nao pode ser nulo.");
        this.endereco = Valores.obrigatorio(endereco, "Endereco nao pode ser nulo.");
        if (capacidade <= 0) throw new ValidacaoException("Capacidade deve ser positiva.");
        this.capacidade = capacidade;
    }

    public void adicionar(String bicicleta) {
        validarAdicao(bicicleta);
        bicicletas.add(bicicleta);
    }

    public void validarAdicao(String bicicleta) {
        Valores.obrigatorio(bicicleta, "Identificacao da bicicleta nao pode ser nula.");
        if (bicicletas.contains(bicicleta)) throw new OperacaoNaoPermitidaException("Bicicleta ja existe.");
        if (vagasLivres() == 0) throw new OperacaoNaoPermitidaException("Estacao sem vagas disponiveis.");
    }

    public void remover(String bicicleta) {
        Valores.obrigatorio(bicicleta, "Identificacao da bicicleta nao pode ser nula.");
        if (!bicicletas.remove(bicicleta)) {
            throw new OperacaoNaoPermitidaException("Bicicleta nao esta nesta estacao.");
        }
    }

    public boolean contem(String bicicleta) { return bicicletas.contains(bicicleta); }

    public String id() { return id; }
    public String nome() { return nome; }
    public String endereco() { return endereco; }
    public int capacidade() { return capacidade; }
    public int quantidadeBicicletas() { return bicicletas.size(); }
    public int vagasLivres() { return capacidade - bicicletas.size(); }
}
