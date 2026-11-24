package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.model.BicicletaConvencional;
import br.ufal.ic.p2.bikeshare.model.BicicletaEletrica;
import br.ufal.ic.p2.bikeshare.model.Estacao;
import br.ufal.ic.p2.bikeshare.util.Valores;

public final class GestaoBicicletas {
    private final EstadoSistema estado;
    private final CadastroEstacoes estacoes;

    public GestaoBicicletas(EstadoSistema estado, CadastroEstacoes estacoes) {
        this.estado = estado;
        this.estacoes = estacoes;
    }

    public void adicionar(String idEstacao, String id, String tipo, String bateria) {
        Estacao estacao = estacoes.buscar(idEstacao);
        Valores.obrigatorio(id, "Identificacao da bicicleta nao pode ser nula.");
        if (estado.temBicicleta(id)) throw new OperacaoNaoPermitidaException("Bicicleta ja existe.");
        Bicicleta bicicleta;
        if ("convencional".equals(tipo)) {
            if (bateria != null) throw new ValidacaoException("Bicicleta nao possui bateria.");
            bicicleta = new BicicletaConvencional(id, idEstacao);
        } else if ("eletrica".equals(tipo)) {
            int nivel = Valores.inteiro(bateria, "Nivel de bateria nao pode ser nulo.",
                    "Nivel de bateria deve ser numerico.");
            bicicleta = new BicicletaEletrica(id, idEstacao, nivel);
        } else throw new ValidacaoException("Tipo invalido.");

        estacao.adicionar(id);
        estado.adicionarBicicleta(bicicleta);
    }

    public void remover(String idEstacao, String id) {
        Estacao estacao = estacoes.buscar(idEstacao);
        Bicicleta bicicleta = buscar(id);
        if (!idEstacao.equals(bicicleta.estacao())) {
            throw new OperacaoNaoPermitidaException("Bicicleta nao esta nesta estacao.");
        }
        estacao.remover(id);
        estado.removerBicicleta(id);
    }

    public String atributo(String id, String atributo) { return buscar(id).atributo(atributo); }
    public String estado(String id) { return buscar(id).estado(); }

    Bicicleta buscar(String id) { return estado.bicicleta(id); }
}
