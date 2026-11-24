package br.ufal.ic.p2.bikeshare.model;

import java.math.BigDecimal;
import java.util.Properties;

public final class Tarifas {
    private final BigDecimal minutoAvulso;
    private final BigDecimal minutoExcedente;
    private final BigDecimal adicionalEletrica;
    private final BigDecimal penalidadeAtraso;
    private final long franquiaMinutos;
    private final long carenciaMinutos;
    private final long tempoMaximoMinutos;

    public Tarifas(Properties config) {
        minutoAvulso = decimal(config, "tarifa.avulso.minuto", "0.50");
        minutoExcedente = decimal(config, "tarifa.assinante.minutoExcedente", "0.30");
        adicionalEletrica = decimal(config, "tarifa.eletrica.adicionalMinuto", "0.20");
        penalidadeAtraso = decimal(config, "aluguel.penalidadeAtraso", "10.00");
        franquiaMinutos = inteiro(config, "tarifa.assinante.franquiaMinutos", "60");
        carenciaMinutos = inteiro(config, "aluguel.carenciaMinutos", "2");
        tempoMaximoMinutos = inteiro(config, "aluguel.tempoMaximoMinutos", "1440");
        if (tempoMaximoMinutos == 0 || carenciaMinutos > tempoMaximoMinutos) {
            throw new IllegalArgumentException("Limites de aluguel invalidos.");
        }
    }

    public BigDecimal minutoAvulso() { return minutoAvulso; }
    public BigDecimal minutoExcedente() { return minutoExcedente; }
    public BigDecimal adicionalEletrica() { return adicionalEletrica; }
    public BigDecimal penalidadeAtraso() { return penalidadeAtraso; }
    public long franquiaMinutos() { return franquiaMinutos; }
    public long carenciaMinutos() { return carenciaMinutos; }
    public long tempoMaximoMinutos() { return tempoMaximoMinutos; }

    private static BigDecimal decimal(Properties config, String chave, String padrao) {
        try {
            BigDecimal valor = new BigDecimal(config.getProperty(chave, padrao));
            if (valor.signum() >= 0) return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Configuracao invalida: " + chave + ".", ex);
        }
        throw new IllegalArgumentException("Configuracao invalida: " + chave + ".");
    }

    private static long inteiro(Properties config, String chave, String padrao) {
        try {
            long valor = Long.parseLong(config.getProperty(chave, padrao));
            if (valor >= 0) return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Configuracao invalida: " + chave + ".", ex);
        }
        throw new IllegalArgumentException("Configuracao invalida: " + chave + ".");
    }
}
