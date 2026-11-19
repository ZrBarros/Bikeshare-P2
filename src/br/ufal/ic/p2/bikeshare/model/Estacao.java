package br.ufal.ic.p2.bikeshare.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Estacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String nome;
    private final String endereco;
    private final int capacidade;
    private final Set<String> bicicletas = new LinkedHashSet<>();

    public Estacao(String id, String nome, String endereco, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    public String id() { return id; }
    public String nome() { return nome; }
    public String endereco() { return endereco; }
    public int capacidade() { return capacidade; }
    public int quantidadeBicicletas() { return bicicletas.size(); }
    public int vagasLivres() { return capacidade - bicicletas.size(); }
}
