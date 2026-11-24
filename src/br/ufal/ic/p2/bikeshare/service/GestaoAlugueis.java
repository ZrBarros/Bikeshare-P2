package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.*;
import br.ufal.ic.p2.bikeshare.util.Datas;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GestaoAlugueis {
    private final EstadoSistema estado;
    private final CadastroUsuarios usuarios;
    private final CadastroEstacoes estacoes;
    private final GestaoBicicletas bicicletas;
    private final int bateriaMinima;
    private final Tarifas tarifas;

    public GestaoAlugueis(EstadoSistema estado, CadastroUsuarios usuarios,
                          CadastroEstacoes estacoes, GestaoBicicletas bicicletas, int bateriaMinima, Tarifas tarifas) {
        this.estado = estado;
        this.usuarios = usuarios;
        this.estacoes = estacoes;
        this.bicicletas = bicicletas;
        this.bateriaMinima = bateriaMinima;
        this.tarifas = tarifas;
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

    public void devolver(String idAluguel, String idEstacao, String data) {
        Aluguel aluguel = buscar(idAluguel);
        Estacao estacao = estacoes.buscar(idEstacao);
        LocalDateTime fim = Datas.ler(data);
        long minutos = aluguel.duracao(fim);
        if (estacao.vagasLivres() == 0) throw new IllegalArgumentException("Estacao cheia.");

        Usuario usuario = usuarios.buscar(aluguel.usuario());
        Bicicleta bicicleta = bicicletas.buscar(aluguel.bicicleta());
        String resultado = aluguel.estadoDevolucao(idEstacao, minutos, tarifas);
        BigDecimal valor = BigDecimal.ZERO;
        if ("finalizado".equals(resultado)) {
            valor = usuario.calcularTarifa(minutos, tarifas).add(bicicleta.adicional(minutos, tarifas))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // A cobranca pode recusar por saldo; os vinculos so mudam depois de aprovada.
        usuario.cobrar(valor);
        estacao.adicionar(bicicleta.id());
        bicicleta.devolver(idEstacao, "expirado".equals(resultado));
        if ("expirado".equals(resultado)) usuario.adicionarPendencia(tarifas.penalidadeAtraso());
        usuario.aluguelAtivo(null);
        aluguel.concluir(idEstacao, fim, resultado, valor);
    }

    public String atributo(String id, String atributo) { return buscar(id).atributo(atributo); }

    private Aluguel buscar(String id) {
        Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
        Aluguel aluguel = estado.alugueis.get(id);
        if (aluguel == null) throw new IllegalArgumentException("Aluguel nao existe.");
        return aluguel;
    }
}
