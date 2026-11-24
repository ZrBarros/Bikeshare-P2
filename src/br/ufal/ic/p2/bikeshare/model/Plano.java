package br.ufal.ic.p2.bikeshare.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

// Contrato da cobrança por corrida
public interface Plano extends Serializable {
    String nome();
    Optional<BigDecimal> mensalidade();
    default boolean assinante() { return false; }
    BigDecimal calcular(long minutos, Tarifas tarifas);
}
