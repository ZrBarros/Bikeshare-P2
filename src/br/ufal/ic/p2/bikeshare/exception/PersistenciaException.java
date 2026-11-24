package br.ufal.ic.p2.bikeshare.exception;

public class PersistenciaException extends BikeShareException {
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
