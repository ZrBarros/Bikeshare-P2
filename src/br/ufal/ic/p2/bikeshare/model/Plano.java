package br.ufal.ic.p2.bikeshare.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Contrato da cobrança por corrida. Os valores de configuração não ficam na Facade. */
public interface Plano extends Serializable {
    String nome();
    BigDecimal mensalidade();
}
