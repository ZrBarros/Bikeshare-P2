package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.util.Datas;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import br.ufal.ic.p2.bikeshare.util.Valores;
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

    private String estado;
    private LocalDateTime fim;
    private String estacaoDestino;
    private BigDecimal valorCobrado;

    public String usuario() { return usuario; }
    public String bicicleta() { return bicicleta; }

    public long duracao(LocalDateTime fim) {
        if (this.fim != null) throw new IllegalArgumentException("Aluguel nao esta em andamento.");
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Data de devolucao anterior a data de retirada.");
        }
        return Duration.between(inicio, fim).toMinutes();
    }

    public String estadoDevolucao(String destino, long minutos, Tarifas tarifas) {
        if (minutos > tarifas.tempoMaximoMinutos()) return "expirado";
        if (estacaoOrigem.equals(destino) && minutos <= tarifas.carenciaMinutos()) return "cancelado";
        return "finalizado";
    }

    public void concluir(String destino, LocalDateTime fim, String estado, BigDecimal valor) {
        this.estacaoDestino = destino;
        this.fim = fim;
        this.estado = estado;
        this.valorCobrado = valor;
    }

    private void exigirConclusao() {
        if (fim == null) throw new IllegalArgumentException("Aluguel ainda esta em andamento.");
    }

    public String id() { return id; }

    public String atributo(String atributo) {
        if (atributo == null) throw new IllegalArgumentException("Atributo nao existe.");
        switch (atributo) {
            case "estado": return fim == null ? "emAndamento" : estado;
            case "usuario": return usuario;
            case "bicicleta": return bicicleta;
            case "estacaoOrigem": return estacaoOrigem;
            case "inicio": return Datas.formatar(inicio);
            case "fim": exigirConclusao(); return Datas.formatar(fim);
            case "estacaoDestino": exigirConclusao(); return estacaoDestino;
            case "valorCobrado": exigirConclusao(); return Valores.formatar(valorCobrado);
            default: throw new IllegalArgumentException("Atributo nao existe.");
        }
    }
}
