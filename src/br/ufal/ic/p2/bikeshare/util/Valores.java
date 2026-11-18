package br.ufal.ic.p2.bikeshare.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Valores {
    private Valores() { }

    public static boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String obrigatorio(String texto, String mensagem) {
        if (vazio(texto)) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto;
    }

    public static BigDecimal decimal(String texto, String campo, boolean positivo) {
        obrigatorio(texto, campo + " nao pode ser nula.");
        BigDecimal valor;
        try {
            valor = new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(campo + " deve ser numerica.");
        }
        if (positivo ? valor.signum() <= 0 : valor.signum() < 0) {
            throw new IllegalArgumentException(campo + (positivo ? " deve ser positivo." : " deve ser nao-negativa."));
        }
        return valor;
    }

    public static String formatar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }
}
