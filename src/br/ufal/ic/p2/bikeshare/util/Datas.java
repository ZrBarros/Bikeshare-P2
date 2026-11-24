package br.ufal.ic.p2.bikeshare.util;

import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class Datas {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter
            .ofPattern("d/M/uuuu H:mm").withResolverStyle(ResolverStyle.STRICT);

    private Datas() { }

    public static LocalDateTime ler(String texto) {
        Valores.obrigatorio(texto, "Data nao pode ser nula.");
        try {
            return LocalDateTime.parse(texto, FORMATO);
        } catch (DateTimeParseException ex) {
            throw new ValidacaoException("Data invalida.", ex);
        }
    }

    public static String formatar(LocalDateTime data) {
        return FORMATO.format(Valores.naoNulo(data, "Data nao pode ser nula."));
    }
}
