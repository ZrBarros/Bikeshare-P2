package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.util.Datas;
import java.io.Serializable;
import java.time.LocalDateTime;

public final class Aluguel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String usuario;
    private final String bicicleta;
    private final String estacaoOrigem;
    private final LocalDateTime inicio;

    public Aluguel(String id, String usuario, String bicicleta, String estacaoOrigem,
                   LocalDateTime inicio) {
        this.id = id;
        this.usuario = usuario;
        this.bicicleta = bicicleta;
        this.estacaoOrigem = estacaoOrigem;
        this.inicio = inicio;
    }

    public String id() { return id; }

    public String atributo(String atributo) {
        if (atributo == null) throw new IllegalArgumentException("Atributo nao existe.");
        switch (atributo) {
            case "estado": return "emAndamento";
            case "usuario": return usuario;
            case "bicicleta": return bicicleta;
            case "estacaoOrigem": return estacaoOrigem;
            case "inicio": return Datas.formatar(inicio);
            case "fim":
            case "estacaoDestino":
            case "valorCobrado": throw new IllegalArgumentException("Aluguel ainda esta em andamento.");
            default: throw new IllegalArgumentException("Atributo nao existe.");
        }
    }
}
