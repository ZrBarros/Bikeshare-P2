package br.ufal.ic.p2.bikeshare.model;

import java.io.Serializable;

public abstract class Bicicleta implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private String estacao;
    private String estado = "disponivel";

    protected Bicicleta(String id, String estacao) {
        this.id = id;
        this.estacao = estacao;
    }

    public String id() { return id; }
    public String estacao() { return estacao; }
    public String estado() { return estado; }
    public abstract String tipo();

    public boolean disponivel(int bateriaMinima) {
        return "disponivel".equals(estado);
    }

    public void validarRetirada(int bateriaMinima) {
        if (!"disponivel".equals(estado)) {
            throw new IllegalArgumentException("Bicicleta indisponivel.");
        }
    }

    public void retirar() {
        estado = "emUso";
        estacao = null;
    }

    protected String nivelBateria() {
        throw new IllegalArgumentException("Bicicleta nao possui bateria.");
    }

    public String atributo(String atributo) {
        if (atributo == null) throw new IllegalArgumentException("Atributo nao existe.");
        switch (atributo) {
            case "tipo": return tipo();
            case "estado": return estado;
            case "estacao": return estacao == null ? "nenhuma" : estacao;
            case "nivelBateria": return nivelBateria();
            default: throw new IllegalArgumentException("Atributo nao existe.");
        }
    }
}
