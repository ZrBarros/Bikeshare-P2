package br.ufal.ic.p2.bikeshare.model.pagamento;

import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public interface MetodoPagamento extends Serializable {
    String nome();
    Map<String, String> atributos();

    default void validarRetirada() { }

    // Cartao e Pix simulam aprovacao, sem integracao externa.
    default void cobrar(BigDecimal valor) {
        Valores.monetario(valor, "Valor", false);
    }
}
