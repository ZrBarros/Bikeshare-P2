package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.*;
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
        if (estado.bicicletas.containsKey(id)) throw new IllegalArgumentException("Bicicleta ja existe.");
        Bicicleta bicicleta;
        if ("convencional".equals(tipo)) {
            if (bateria != null) throw new IllegalArgumentException("Bicicleta nao possui bateria.");
            bicicleta = new BicicletaConvencional(id, idEstacao);
        } else if ("eletrica".equals(tipo)) {
            Valores.obrigatorio(bateria, "Nivel de bateria nao pode ser nulo.");
            int nivel;
            try {
                nivel = Integer.parseInt(bateria.trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Nivel de bateria deve ser numerico.");
            }
            if (nivel < 0 || nivel > 100) {
                throw new IllegalArgumentException("Nivel de bateria deve estar entre 0 e 100.");
            }
            bicicleta = new BicicletaEletrica(id, idEstacao, nivel);
        } else throw new IllegalArgumentException("Tipo invalido.");

        estacao.adicionar(id);
        estado.bicicletas.put(id, bicicleta);
    }

    public void remover(String idEstacao, String id) {
        Estacao estacao = estacoes.buscar(idEstacao);
        Bicicleta bicicleta = buscar(id);
        if (!idEstacao.equals(bicicleta.estacao())) {
            throw new IllegalArgumentException("Bicicleta nao esta nesta estacao.");
        }
        estacao.remover(id);
        estado.bicicletas.remove(id);
    }

    public String atributo(String id, String atributo) { return buscar(id).atributo(atributo); }
    public String estado(String id) { return buscar(id).estado(); }

    Bicicleta buscar(String id) {
        Valores.obrigatorio(id, "Identificacao da bicicleta nao pode ser nula.");
        Bicicleta bicicleta = estado.bicicletas.get(id);
        if (bicicleta == null) throw new IllegalArgumentException("Bicicleta nao existe.");
        return bicicleta;
    }
}
