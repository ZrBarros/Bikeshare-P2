package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.math.BigDecimal;

public final class BicicletaEletrica extends Bicicleta {
    private static final long serialVersionUID = 1L;
    private final int bateria;

    public BicicletaEletrica(String id, String estacao, int bateria) {
        super(id, estacao);
        if (bateria < 0 || bateria > 100) {
            throw new ValidacaoException("Nivel de bateria deve estar entre 0 e 100.");
        }
        this.bateria = bateria;
    }

    public String tipo() { return "eletrica"; }
    public String atributo(String atributo) {
        return "nivelBateria".equals(atributo) ? Integer.toString(bateria) : super.atributo(atributo);
    }

    public void validarRetirada(int bateriaMinima) {
        super.validarRetirada(bateriaMinima);
        if (bateria < bateriaMinima) throw new OperacaoNaoPermitidaException("Nivel de bateria insuficiente.");
    }

    public BigDecimal adicional(long minutos, Tarifas tarifas) {
        Valores.minutos(minutos);
        Valores.naoNulo(tarifas, "Tarifas nao podem ser nulas.");
        return tarifas.adicionalEletrica().multiply(BigDecimal.valueOf(minutos));
    }

    public boolean disponivel(int bateriaMinima) {
        return super.disponivel(bateriaMinima) && bateria >= bateriaMinima;
    }
}
