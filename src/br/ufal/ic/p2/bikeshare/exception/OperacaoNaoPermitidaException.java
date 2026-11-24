package br.ufal.ic.p2.bikeshare.exception;

public class OperacaoNaoPermitidaException extends BikeShareException {
    private static final long serialVersionUID = 1L;

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }

    public OperacaoNaoPermitidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
