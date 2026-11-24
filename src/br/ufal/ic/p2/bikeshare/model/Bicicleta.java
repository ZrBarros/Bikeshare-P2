package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.math.BigDecimal;

public abstract class Bicicleta implements Serializable {
    private static final long serialVersionUID = 1L;
    // Mantem o formato dos arquivos salvos antes da introducao dos enums.
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("id", String.class),
        new ObjectStreamField("estacao", String.class),
        new ObjectStreamField("estado", String.class)
    };
    private String id;
    private String estacao;
    private EstadoBicicleta situacao = EstadoBicicleta.DISPONIVEL;

    protected Bicicleta(String id, String estacao) {
        this.id = Valores.obrigatorio(id, "Identificacao da bicicleta nao pode ser nula.");
        this.estacao = Valores.obrigatorio(estacao, "Identificacao da estacao nao pode ser nula.");
    }

    public String id() { return id; }
    public String estacao() { return estacao; }
    public String estado() { return situacao.texto(); }
    public abstract String tipo();

    public boolean disponivel(int bateriaMinima) {
        return situacao == EstadoBicicleta.DISPONIVEL;
    }

    public void validarRetirada(int bateriaMinima) {
        if (situacao != EstadoBicicleta.DISPONIVEL || estacao == null) {
            throw new OperacaoNaoPermitidaException("Bicicleta indisponivel.");
        }
    }

    public void retirar(int bateriaMinima) {
        validarRetirada(bateriaMinima);
        situacao = EstadoBicicleta.EM_USO;
        estacao = null;
    }

    public void validarDevolucao(String destino) {
        Valores.obrigatorio(destino, "Identificacao da estacao nao pode ser nula.");
        if (situacao != EstadoBicicleta.EM_USO) {
            throw new OperacaoNaoPermitidaException("Bicicleta nao esta em uso.");
        }
    }

    public void devolver(String destino, boolean manutencao) {
        validarDevolucao(destino);
        estacao = destino;
        situacao = manutencao ? EstadoBicicleta.EM_MANUTENCAO : EstadoBicicleta.DISPONIVEL;
    }

    public BigDecimal adicional(long minutos, Tarifas tarifas) {
        Valores.minutos(minutos);
        Valores.naoNulo(tarifas, "Tarifas nao podem ser nulas.");
        return BigDecimal.ZERO;
    }

    public String atributo(String atributo) {
        if (atributo == null) throw new ValidacaoException("Atributo nao existe.");
        switch (atributo) {
            case "tipo": return tipo();
            case "estado": return estado();
            case "estacao": return estacao == null ? "nenhuma" : estacao;
            default: throw new ValidacaoException("Atributo nao existe.");
        }
    }

    private void writeObject(ObjectOutputStream saida) throws IOException {
        ObjectOutputStream.PutField campos = saida.putFields();
        campos.put("id", id);
        campos.put("estacao", estacao);
        campos.put("estado", situacao.texto());
        saida.writeFields();
    }

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        try {
            ObjectInputStream.GetField campos = entrada.readFields();
            id = (String) campos.get("id", null);
            estacao = (String) campos.get("estacao", null);
            situacao = EstadoBicicleta.porTexto((String) campos.get("estado", "disponivel"));
            Valores.obrigatorio(id, "Identificacao da bicicleta nao pode ser nula.");
            if (situacao == EstadoBicicleta.EM_USO ? estacao != null : Valores.vazio(estacao)) {
                throw new ValidacaoException("Estacao incompativel com o estado da bicicleta.");
            }
        } catch (IllegalArgumentException | br.ufal.ic.p2.bikeshare.exception.BikeShareException ex) {
            InvalidObjectException erro = new InvalidObjectException(ex.getMessage());
            erro.initCause(ex);
            throw erro;
        }
    }
}
