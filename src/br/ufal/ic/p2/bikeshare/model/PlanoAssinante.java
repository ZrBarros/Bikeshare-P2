package br.ufal.ic.p2.bikeshare.model;

import java.math.BigDecimal;

public final class PlanoAssinante implements Plano {
    private static final long serialVersionUID = 1L;
    private final BigDecimal mensalidade;

    public PlanoAssinante(BigDecimal mensalidade) {
        this.mensalidade = mensalidade;
    }

    public String nome() {
        return "assinante";
    }

    public BigDecimal mensalidade() {
        return mensalidade;
    }
}
