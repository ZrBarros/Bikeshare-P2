package br.ufal.ic.p2.bikeshare.model.pagamento;

import java.math.BigDecimal;

public final class SaldoPreCarregado implements MetodoPagamento {
    private static final long serialVersionUID = 1L;
    private BigDecimal saldo = BigDecimal.ZERO;

    public String nome() {
        return "saldoPreCarregado";
    }

    public BigDecimal saldo() {
        return saldo;
    }

    public void recarregar(BigDecimal valor) {
        saldo = saldo.add(valor);
    }
}
