package br.ufal.ic.p2.bikeshare.util;

import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Valores {
    private Valores() { }

    public static boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String obrigatorio(String texto, String mensagem) {
        if (vazio(texto)) {
            throw new ValidacaoException(mensagem);
        }
        return texto;
    }

    public static BigDecimal decimal(String texto, String campo, boolean positivo) {
        BigDecimal valor = numero(texto, campo + " nao pode ser nula.", campo + " deve ser numerica.");
        if (positivo ? valor.signum() <= 0 : valor.signum() < 0) {
            throw new ValidacaoException(campo + (positivo ? " deve ser positivo." : " deve ser nao-negativa."));
        }
        return valor;
    }

    public static BigDecimal numero(String texto, String nulo, String numerico) {
        obrigatorio(texto, nulo);
        try {
            return new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new ValidacaoException(numerico, ex);
        }
    }

    public static <T> T naoNulo(T valor, String mensagem) {
        if (valor == null) throw new ValidacaoException(mensagem);
        return valor;
    }

    public static String email(String email) {
        obrigatorio(email, "Email nao pode ser nulo.");
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new ValidacaoException("Email invalido.");
        }
        return email;
    }

    public static BigDecimal monetario(BigDecimal valor, String campo, boolean positivo) {
        naoNulo(valor, campo + " nao pode ser nulo.");
        if (positivo ? valor.signum() <= 0 : valor.signum() < 0) {
            throw new ValidacaoException(campo + (positivo ? " deve ser positivo." : " deve ser nao-negativo."));
        }
        return valor;
    }

    public static long minutos(long minutos) {
        if (minutos < 0) throw new ValidacaoException("Duracao deve ser nao-negativa.");
        return minutos;
    }

    public static int inteiro(String texto, String nulo, String numerico) {
        obrigatorio(texto, nulo);
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new ValidacaoException(numerico, ex);
        }
    }

    public static String formatar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }
}
