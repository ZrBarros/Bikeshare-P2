package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.util.Datas;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Aluguel implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("id", String.class),
        new ObjectStreamField("usuario", String.class),
        new ObjectStreamField("bicicleta", String.class),
        new ObjectStreamField("estacaoOrigem", String.class),
        new ObjectStreamField("inicio", LocalDateTime.class),
        new ObjectStreamField("fim", LocalDateTime.class),
        new ObjectStreamField("estado", String.class),
        new ObjectStreamField("estacaoDestino", String.class),
        new ObjectStreamField("valorCobrado", BigDecimal.class)
    };
    private String id;
    private String usuario;
    private String bicicleta;
    private String estacaoOrigem;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private EstadoAluguel situacao = EstadoAluguel.EM_ANDAMENTO;
    private String estacaoDestino;
    private BigDecimal valorCobrado;

    public Aluguel(String id, String usuario, String bicicleta, String estacaoOrigem,
                   LocalDateTime inicio) {
        this.id = Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
        this.usuario = Valores.obrigatorio(usuario, "Identificacao do usuario nao pode ser nula.");
        this.bicicleta = Valores.obrigatorio(bicicleta, "Identificacao da bicicleta nao pode ser nula.");
        this.estacaoOrigem = Valores.obrigatorio(estacaoOrigem, "Identificacao da estacao nao pode ser nula.");
        this.inicio = Valores.naoNulo(inicio, "Data nao pode ser nula.");
    }

    public String id() { return id; }
    public String usuario() { return usuario; }
    public String bicicleta() { return bicicleta; }

    public long duracao(LocalDateTime fim) {
        if (situacao != EstadoAluguel.EM_ANDAMENTO) {
            throw new OperacaoNaoPermitidaException("Aluguel nao esta em andamento.");
        }
        Valores.naoNulo(fim, "Data nao pode ser nula.");
        if (fim.isBefore(inicio)) {
            throw new ValidacaoException("Data de devolucao anterior a data de retirada.");
        }
        return Duration.between(inicio, fim).toMinutes();
    }

    public EstadoAluguel estadoDevolucao(String destino, LocalDateTime fim, Tarifas tarifas) {
        Valores.obrigatorio(destino, "Identificacao da estacao nao pode ser nula.");
        Valores.naoNulo(tarifas, "Tarifas nao podem ser nulas.");
        long minutos = duracao(fim);
        if (minutos > tarifas.tempoMaximoMinutos()) return EstadoAluguel.EXPIRADO;
        if (estacaoOrigem.equals(destino) && minutos <= tarifas.carenciaMinutos()) return EstadoAluguel.CANCELADO;
        return EstadoAluguel.FINALIZADO;
    }

    public void encerrar(String destino, LocalDateTime fim, BigDecimal valor, Tarifas tarifas) {
        EstadoAluguel resultado = estadoDevolucao(destino, fim, tarifas);
        Valores.monetario(valor, "Valor", false);
        if (resultado != EstadoAluguel.FINALIZADO && valor.signum() != 0) {
            throw new ValidacaoException("Aluguel cancelado ou expirado nao pode ter cobranca.");
        }
        estacaoDestino = destino;
        this.fim = fim;
        situacao = resultado;
        valorCobrado = valor;
    }

    private void exigirConclusao() {
        if (situacao == EstadoAluguel.EM_ANDAMENTO) {
            throw new OperacaoNaoPermitidaException("Aluguel ainda esta em andamento.");
        }
    }

    public String atributo(String atributo) {
        if (atributo == null) throw new ValidacaoException("Atributo nao existe.");
        switch (atributo) {
            case "estado": return situacao.texto();
            case "usuario": return usuario;
            case "bicicleta": return bicicleta;
            case "estacaoOrigem": return estacaoOrigem;
            case "inicio": return Datas.formatar(inicio);
            case "fim": exigirConclusao(); return Datas.formatar(fim);
            case "estacaoDestino": exigirConclusao(); return estacaoDestino;
            case "valorCobrado": exigirConclusao(); return Valores.formatar(valorCobrado);
            default: throw new ValidacaoException("Atributo nao existe.");
        }
    }

    private void writeObject(ObjectOutputStream saida) throws IOException {
        ObjectOutputStream.PutField campos = saida.putFields();
        campos.put("id", id);
        campos.put("usuario", usuario);
        campos.put("bicicleta", bicicleta);
        campos.put("estacaoOrigem", estacaoOrigem);
        campos.put("inicio", inicio);
        campos.put("fim", fim);
        campos.put("estado", situacao.texto());
        campos.put("estacaoDestino", estacaoDestino);
        campos.put("valorCobrado", valorCobrado);
        saida.writeFields();
    }

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        try {
            ObjectInputStream.GetField campos = entrada.readFields();
            id = (String) campos.get("id", null);
            usuario = (String) campos.get("usuario", null);
            bicicleta = (String) campos.get("bicicleta", null);
            estacaoOrigem = (String) campos.get("estacaoOrigem", null);
            inicio = (LocalDateTime) campos.get("inicio", null);
            fim = (LocalDateTime) campos.get("fim", null);
            situacao = fim == null ? EstadoAluguel.EM_ANDAMENTO
                    : EstadoAluguel.porTexto((String) campos.get("estado", null));
            estacaoDestino = (String) campos.get("estacaoDestino", null);
            valorCobrado = (BigDecimal) campos.get("valorCobrado", null);
            Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
            Valores.obrigatorio(usuario, "Identificacao do usuario nao pode ser nula.");
            Valores.obrigatorio(bicicleta, "Identificacao da bicicleta nao pode ser nula.");
            Valores.obrigatorio(estacaoOrigem, "Identificacao da estacao nao pode ser nula.");
            Valores.naoNulo(inicio, "Data nao pode ser nula.");
            if (fim != null) {
                Valores.obrigatorio(estacaoDestino, "Identificacao da estacao nao pode ser nula.");
                Valores.monetario(valorCobrado, "Valor", false);
                if (fim.isBefore(inicio) || situacao == EstadoAluguel.EM_ANDAMENTO) {
                    throw new ValidacaoException("Conclusao de aluguel invalida.");
                }
            }
        } catch (IllegalArgumentException | br.ufal.ic.p2.bikeshare.exception.BikeShareException ex) {
            InvalidObjectException erro = new InvalidObjectException(ex.getMessage());
            erro.initCause(ex);
            throw erro;
        }
    }
}
