package br.ufal.ic.p2.bikeshare.exception;

public class ValidacaoException extends BikeShareException {
    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }

    public ValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
