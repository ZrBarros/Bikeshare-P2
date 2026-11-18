package br.ufal.ic.p2.bikeshare.model;

import java.math.BigDecimal;

public final class PlanoAvulso implements Plano {
    private static final long serialVersionUID = 1L;

    public String nome() {
        return "avulso";
    }

    public BigDecimal mensalidade() {
        throw new IllegalArgumentException("Plano nao possui mensalidade.");
    }
}
