package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.model.Aluguel;
import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.model.Estacao;
import br.ufal.ic.p2.bikeshare.model.EstadoAluguel;
import br.ufal.ic.p2.bikeshare.model.Tarifas;
import br.ufal.ic.p2.bikeshare.model.Usuario;
import br.ufal.ic.p2.bikeshare.util.Datas;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
        if (!idEstacao.equals(bicicleta.estacao()) || !estacao.contem(idBicicleta)) {
            throw new OperacaoNaoPermitidaException("Bicicleta nao esta nesta estacao.");
        }
        bicicleta.validarRetirada(bateriaMinima);
        usuario.validarPagamento();

        String id = estado.cadastrarAluguel(chave ->
                new Aluguel(chave, idUsuario, idBicicleta, idEstacao, inicio));
        estacao.remover(idBicicleta);
        bicicleta.retirar(bateriaMinima);
        usuario.iniciarAluguel(id);
        return id;
    }

    public void devolver(String idAluguel, String idEstacao, String data) {
        Aluguel aluguel = buscar(idAluguel);
        Estacao estacao = estacoes.buscar(idEstacao);
        LocalDateTime fim = Datas.ler(data);
        long minutos = aluguel.duracao(fim);
        if (estacao.vagasLivres() == 0) throw new OperacaoNaoPermitidaException("Estacao cheia.");

        Usuario usuario = usuarios.buscar(aluguel.usuario());
        Bicicleta bicicleta = bicicletas.buscar(aluguel.bicicleta());
        estacao.validarAdicao(bicicleta.id());
        usuario.validarAluguelAtivo(idAluguel);
        bicicleta.validarDevolucao(idEstacao);
        EstadoAluguel resultado = aluguel.estadoDevolucao(idEstacao, fim, tarifas);
        BigDecimal valor = BigDecimal.ZERO;
        if (resultado == EstadoAluguel.FINALIZADO) {
            valor = usuario.calcularTarifa(minutos, tarifas).add(bicicleta.adicional(minutos, tarifas))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // A cobranca pode recusar por saldo; os vinculos so mudam depois de aprovada.
        usuario.cobrar(valor);
        estacao.adicionar(bicicleta.id());
        bicicleta.devolver(idEstacao, resultado == EstadoAluguel.EXPIRADO);
        if (resultado == EstadoAluguel.EXPIRADO) usuario.adicionarPendencia(tarifas.penalidadeAtraso());
        usuario.concluirAluguel(idAluguel);
        aluguel.encerrar(idEstacao, fim, valor, tarifas);
    }

    public String atributo(String id, String atributo) { return buscar(id).atributo(atributo); }

    private Aluguel buscar(String id) { return estado.aluguel(id); }
}
