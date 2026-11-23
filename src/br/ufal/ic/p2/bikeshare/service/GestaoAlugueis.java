package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.*;
import br.ufal.ic.p2.bikeshare.util.Datas;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.time.LocalDateTime;

public final class GestaoAlugueis {
    private final EstadoSistema estado;
    private final CadastroUsuarios usuarios;
    private final CadastroEstacoes estacoes;
    private final GestaoBicicletas bicicletas;
    private final int bateriaMinima;

    public GestaoAlugueis(EstadoSistema estado, CadastroUsuarios usuarios,
                          CadastroEstacoes estacoes, GestaoBicicletas bicicletas, int bateriaMinima) {
        this.estado = estado;
        this.usuarios = usuarios;
        this.estacoes = estacoes;
        this.bicicletas = bicicletas;
        this.bateriaMinima = bateriaMinima;
    }

    public String retirar(String idUsuario, String idEstacao, String idBicicleta, String data) {
        Usuario usuario = usuarios.buscar(idUsuario);
        Estacao estacao = estacoes.buscar(idEstacao);
        Bicicleta bicicleta = bicicletas.buscar(idBicicleta);
        LocalDateTime inicio = Datas.ler(data);
        usuario.validarNovoAluguel();
        if (!idEstacao.equals(bicicleta.estacao())) {
            throw new IllegalArgumentException("Bicicleta nao esta nesta estacao.");
        }
        bicicleta.validarRetirada(bateriaMinima);
        usuario.validarPagamento();

        String id = "A" + estado.proximoAluguel++;
        Aluguel aluguel = new Aluguel(id, idUsuario, idBicicleta, idEstacao, inicio);
        estacao.remover(idBicicleta);
        bicicleta.retirar();
        usuario.aluguelAtivo(id);
        estado.alugueis.put(id, aluguel);
        return id;
    }

    public String atributo(String id, String atributo) {
        Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
        Aluguel aluguel = estado.alugueis.get(id);
        if (aluguel == null) throw new IllegalArgumentException("Aluguel nao existe.");
        return aluguel.atributo(atributo);
    }
}
