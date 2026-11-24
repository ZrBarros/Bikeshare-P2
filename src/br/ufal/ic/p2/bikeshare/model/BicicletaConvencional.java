package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;

public final class BicicletaConvencional extends Bicicleta {
    private static final long serialVersionUID = 1L;

    public BicicletaConvencional(String id, String estacao) {
        super(id, estacao);
    }

    public String atributo(String atributo) {
        if ("nivelBateria".equals(atributo)) {
            throw new OperacaoNaoPermitidaException("Bicicleta nao possui bateria.");
        }
        return super.atributo(atributo);
    }

    public String tipo() { return "convencional"; }
}
