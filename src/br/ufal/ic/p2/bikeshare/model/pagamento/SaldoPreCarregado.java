package br.ufal.ic.p2.bikeshare.model.pagamento;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.math.BigDecimal;
import java.util.Map;

public final class SaldoPreCarregado implements MetodoPagamento {
    private static final long serialVersionUID = 1L;
    private BigDecimal saldo = BigDecimal.ZERO;

    public String nome() {
        return "saldoPreCarregado";
    }

    public Map<String, String> atributos() { return Map.of("saldo", Valores.formatar(saldo)); }

    public BigDecimal saldo() {
        return saldo;
    }

    public void validarRetirada() {
        if (saldo.signum() <= 0) throw new OperacaoNaoPermitidaException("Saldo insuficiente.");
    }

    public void cobrar(BigDecimal valor) {
        MetodoPagamento.super.cobrar(valor);
        if (saldo.compareTo(valor) < 0) throw new OperacaoNaoPermitidaException("Saldo insuficiente.");
        saldo = saldo.subtract(valor);
    }

    public void recarregar(BigDecimal valor) {
        saldo = saldo.add(Valores.monetario(valor, "Valor", true));
    }
}
