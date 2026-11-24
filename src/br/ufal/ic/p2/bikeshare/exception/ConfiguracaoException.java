package br.ufal.ic.p2.bikeshare.exception;

public class ConfiguracaoException extends BikeShareException {
    private static final long serialVersionUID = 1L;

    public ConfiguracaoException(String mensagem) {
        super(mensagem);
    }

    public ConfiguracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
