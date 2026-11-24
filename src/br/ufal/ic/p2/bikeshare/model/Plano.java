package br.ufal.ic.p2.bikeshare.model;

import java.io.Serializable;
import java.math.BigDecimal;

// Contrato da cobrança por corrida
public interface Plano extends Serializable {
    String nome();
    BigDecimal mensalidade();
    BigDecimal calcular(long minutos, Tarifas tarifas);
}
