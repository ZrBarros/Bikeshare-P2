package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.util.Valores;
import java.math.BigDecimal;
import java.util.Optional;

public final class PlanoAssinante implements Plano {
    private static final long serialVersionUID = 1L;
    private final BigDecimal mensalidade;

    public PlanoAssinante(BigDecimal mensalidade) {
        this.mensalidade = Valores.monetario(mensalidade, "Mensalidade", false);
    }

    public String nome() {
        return "assinante";
    }

    public Optional<BigDecimal> mensalidade() { return Optional.of(mensalidade); }

    public boolean assinante() { return true; }
    public BigDecimal calcular(long minutos, Tarifas tarifas) {
        Valores.minutos(minutos);
        Valores.naoNulo(tarifas, "Tarifas nao podem ser nulas.");
        return tarifas.minutoExcedente().multiply(
                BigDecimal.valueOf(Math.max(0, minutos - tarifas.franquiaMinutos())));
    }
}
