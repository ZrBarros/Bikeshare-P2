package br.ufal.ic.p2.bikeshare.model.pagamento;

import java.io.Serializable;
import java.math.BigDecimal;

/** Consultas específicas ficam nas implementações; nenhum cast é necessário. */
public interface MetodoPagamento extends Serializable {
    String nome();

    default String numeroCartao() {
        throw new IllegalArgumentException("Usuario nao usa cartao.");
    }

    default String validadeCartao() {
        throw new IllegalArgumentException("Usuario nao usa cartao.");
    }

    default String chavePix() {
        throw new IllegalArgumentException("Usuario nao usa pix.");
    }

    default BigDecimal saldo() {
        throw new IllegalArgumentException("Usuario nao usa saldo pre-carregado.");
    }

    default void recarregar(BigDecimal valor) {
        throw new IllegalArgumentException("Usuario nao usa saldo pre-carregado.");
    }
}
