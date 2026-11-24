package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.util.Valores;
import java.math.BigDecimal;
import java.util.Optional;

public final class PlanoAvulso implements Plano {
    private static final long serialVersionUID = 1L;

    public String nome() {
        return "avulso";
    }

    public Optional<BigDecimal> mensalidade() { return Optional.empty(); }
    public BigDecimal calcular(long minutos, Tarifas tarifas) {
        Valores.minutos(minutos);
        Valores.naoNulo(tarifas, "Tarifas nao podem ser nulas.");
        return tarifas.minutoAvulso().multiply(BigDecimal.valueOf(minutos));
    }
}
