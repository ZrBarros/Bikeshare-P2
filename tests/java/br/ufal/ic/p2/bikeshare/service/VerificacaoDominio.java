package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.Facade;
import br.ufal.ic.p2.bikeshare.exception.EntidadeNaoEncontradaException;
import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.PersistenciaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.model.Aluguel;
import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.model.BicicletaEletrica;
import br.ufal.ic.p2.bikeshare.model.Estacao;
import br.ufal.ic.p2.bikeshare.model.PessoaFisica;
import br.ufal.ic.p2.bikeshare.model.PessoaJuridica;
import br.ufal.ic.p2.bikeshare.model.PlanoAssinante;
import br.ufal.ic.p2.bikeshare.model.PlanoAvulso;
import br.ufal.ic.p2.bikeshare.model.Tarifas;
import br.ufal.ic.p2.bikeshare.model.Usuario;
import br.ufal.ic.p2.bikeshare.model.pagamento.Cartao;
import br.ufal.ic.p2.bikeshare.model.pagamento.Pix;
import br.ufal.ic.p2.bikeshare.persistence.PersistenciaSistema;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public final class VerificacaoDominio {
    private static int verificacoes;
    private static final LocalDateTime INICIO = LocalDateTime.of(2025, 6, 1, 8, 0);

    public static void main(String[] args) throws Exception {
        usuariosEPagamentos();
        estacoesEBicicletas();
        alugueis();
        repositorio();
        persistencia();
        devolucaoRecusada();
        System.out.println(verificacoes + " verificacoes de dominio aprovadas.");
    }

    private static void usuariosEPagamentos() {
        Usuario usuario = pessoa("U1");
        falha(ValidacaoException.class, () -> usuario.nome(" "));
        falha(ValidacaoException.class, () -> usuario.email("invalido"));
        falha(ValidacaoException.class, () -> usuario.senha(null));
        falha(ValidacaoException.class, () -> usuario.plano(null));
        igual("Ana", usuario.atributo("nome"));
        igual("ana@exemplo.com", usuario.email());
        falha(ValidacaoException.class, () -> new PessoaFisica("U2", "Ana", "ana@exemplo.com", "123", null,
                new PlanoAvulso()));
        falha(ValidacaoException.class, () -> new PessoaJuridica("U2", "Ana", "ana@exemplo.com", "123", "123",
                null, new PlanoAvulso()));
        falha(ValidacaoException.class, () -> new PlanoAssinante(new BigDecimal("-1")));
        falha(ValidacaoException.class, () -> new Cartao("123", "13/2030"));
        falha(ValidacaoException.class, () -> new Pix(" "));
        usuario.usarCarteira();
        usuario.recarregar(BigDecimal.TEN);
        falha(ValidacaoException.class, () -> usuario.recarregar(new BigDecimal("-5")));
        falha(ValidacaoException.class, () -> usuario.cobrar(new BigDecimal("-5")));
        falha(OperacaoNaoPermitidaException.class, () -> usuario.cobrar(new BigDecimal("11")));
        igual("10,00", usuario.atributo("saldo"));
        usuario.usarPix(new Pix("ana@pix.com"));
        falha(OperacaoNaoPermitidaException.class, () -> usuario.recarregar(BigDecimal.ONE));
        usuario.usarCarteira();
        igual("10,00", usuario.atributo("saldo"));
        falha(ValidacaoException.class, () -> usuario.adicionarPendencia(new BigDecimal("-1")));
        igual("ativo", usuario.atributo("estado"));
        falha(ValidacaoException.class, () -> usuario.iniciarAluguel(" "));
        usuario.iniciarAluguel("A1");
        falha(OperacaoNaoPermitidaException.class, () -> usuario.iniciarAluguel("A2"));
        falha(OperacaoNaoPermitidaException.class, () -> usuario.concluirAluguel("A2"));
        igual("A1", usuario.aluguelAtivo());
        usuario.concluirAluguel("A1");
        igual("nenhum", usuario.atributo("aluguelAtivo"));
    }

    private static void estacoesEBicicletas() {
        falha(ValidacaoException.class, () -> new Estacao("E1", "UFAL", "Rua", 0));
        Estacao estacao = new Estacao("E1", "UFAL", "Rua", 1);
        falha(ValidacaoException.class, () -> estacao.adicionar(null));
        estacao.adicionar("B1");
        falha(OperacaoNaoPermitidaException.class, () -> estacao.adicionar("B2"));
        igual(0, estacao.vagasLivres());
        falha(OperacaoNaoPermitidaException.class, () -> estacao.remover("B2"));
        igual(1, estacao.quantidadeBicicletas());
        falha(ValidacaoException.class, () -> new BicicletaEletrica("B2", "E1", 101));
        Bicicleta bicicleta = new BicicletaEletrica("B1", "E1", 14);
        falha(OperacaoNaoPermitidaException.class, () -> bicicleta.retirar(15));
        igual("disponivel", bicicleta.estado());
        igual("E1", bicicleta.estacao());
        bicicleta.retirar(14);
        falha(OperacaoNaoPermitidaException.class, () -> bicicleta.retirar(14));
        falha(ValidacaoException.class, () -> bicicleta.devolver(null, false));
        igual("emUso", bicicleta.estado());
        bicicleta.devolver("E2", false);
        falha(OperacaoNaoPermitidaException.class, () -> bicicleta.devolver("E3", true));
        igual("E2", bicicleta.estacao());
    }

    private static void alugueis() {
        Tarifas tarifas = new Tarifas(new Properties());
        falha(ValidacaoException.class, () -> new Aluguel("A1", "U1", "B1", "E1", null));
        Aluguel aluguel = new Aluguel("A1", "U1", "B1", "E1", INICIO);
        falha(ValidacaoException.class, () -> aluguel.encerrar("E1", INICIO.minusMinutes(1),
                BigDecimal.ZERO, tarifas));
        falha(ValidacaoException.class, () -> aluguel.encerrar("E1", INICIO.plusMinutes(1),
                BigDecimal.ONE, tarifas));
        igual("emAndamento", aluguel.atributo("estado"));
        aluguel.encerrar("E1", INICIO.plusMinutes(1), BigDecimal.ZERO, tarifas);
        falha(OperacaoNaoPermitidaException.class, () -> aluguel.encerrar("E2", INICIO.plusMinutes(5),
                BigDecimal.ONE, tarifas));
        igual("cancelado", aluguel.atributo("estado"));
        igual("E1", aluguel.atributo("estacaoDestino"));
    }

    private static void repositorio() {
        EstadoSistema estado = new EstadoSistema();
        falha(ValidacaoException.class, () -> estado.cadastrarUsuario(id ->
                new PessoaFisica(id, "", "ana@exemplo.com", "123", "123", new PlanoAvulso())));
        igual("U1", estado.cadastrarUsuario(VerificacaoDominio::pessoa));
        List<Usuario> usuarios = estado.usuarios();
        falha(UnsupportedOperationException.class, usuarios::clear);
        estado.cadastrarUsuario(VerificacaoDominio::pessoa);
        igual(1, usuarios.size());
        igual(2, estado.usuarios().size());
        falha(EntidadeNaoEncontradaException.class, () -> estado.usuario("U999"));
    }

    private static void persistencia() throws Exception {
        Path pasta = Files.createTempDirectory("bikeshare-dominio-");
        Path arquivo = pasta.resolve("estado.bin");
        PersistenciaSistema persistencia = new PersistenciaSistema(arquivo);
        EstadoSistema estado = new EstadoSistema();
        estado.cadastrarUsuario(VerificacaoDominio::pessoa);
        persistencia.salvar(estado);
        igual("Ana", persistencia.carregar().usuario("U1").atributo("nome"));
        Files.writeString(arquivo, "arquivo corrompido");
        try {
            persistencia.carregar();
            throw new AssertionError("A falha de leitura foi ignorada.");
        } catch (PersistenciaException ex) {
            verificar(ex.getCause() != null, "Causa de leitura nao preservada.");
        }
        PersistenciaSistema destinoInvalido = new PersistenciaSistema(arquivo.resolve("estado.bin"));
        falha(PersistenciaException.class, () -> destinoInvalido.salvar(estado));
        Files.delete(arquivo);
        Files.delete(pasta);
    }

    private static void devolucaoRecusada() {
        Facade sistema = new Facade();
        sistema.zerarSistema();
        String origem = sistema.criarEstacao("UFAL", "Rua", "1");
        String destino = sistema.criarEstacao("Centro", "Rua", "1");
        sistema.adicionarBicicleta(origem, "B1", "convencional");
        sistema.adicionarBicicleta(destino, "B2", "convencional");
        String usuario = sistema.criarUsuario("Ana", "ana@exemplo.com", "123", "pessoaFisica", "123");
        sistema.alterarUsuario(usuario, "metodoPagamento", "saldoPreCarregado");
        sistema.alterarUsuario(usuario, "saldo", "1");
        String aluguel = sistema.retirarBicicleta(usuario, origem, "B1", "1/6/2025 08:00");
        falha(OperacaoNaoPermitidaException.class, () -> sistema.devolverBicicleta(aluguel, destino,
                "1/6/2025 08:10"));
        igual("1,00", sistema.getAtributoUsuario(usuario, "saldo"));
        sistema.removerBicicleta(destino, "B2");
        falha(OperacaoNaoPermitidaException.class, () -> sistema.devolverBicicleta(aluguel, destino,
                "1/6/2025 08:10"));
        igual("emAndamento", sistema.getAtributoAluguel(aluguel, "estado"));
        igual("emUso", sistema.getEstadoBicicleta("B1"));
        igual("nenhuma", sistema.getAtributoBicicleta("B1", "estacao"));
        igual("1", sistema.getVagasDisponiveis(destino));
        igual("1,00", sistema.getAtributoUsuario(usuario, "saldo"));
        igual(aluguel, sistema.getAtributoUsuario(usuario, "aluguelAtivo"));
    }

    private static Usuario pessoa(String id) {
        return new PessoaFisica(id, "Ana", "ana@exemplo.com", "123", "123", new PlanoAvulso());
    }

    private static void igual(Object esperado, Object obtido) {
        verificar(esperado.equals(obtido), "Esperado " + esperado + ", obtido " + obtido);
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        verificacoes++;
    }

    private static void falha(Class<? extends Throwable> tipo, Runnable operacao) {
        try {
            operacao.run();
        } catch (RuntimeException ex) {
            verificar(tipo.isInstance(ex), "Categoria de falha incorreta: " + ex.getClass().getName());
            return;
        }
        throw new AssertionError("Operacao deveria falhar: " + tipo.getSimpleName());
    }
}
