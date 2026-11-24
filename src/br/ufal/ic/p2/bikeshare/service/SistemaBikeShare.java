package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.exception.ConfiguracaoException;
import br.ufal.ic.p2.bikeshare.model.Tarifas;
import br.ufal.ic.p2.bikeshare.persistence.PersistenciaSistema;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

// Sistema que coordena os serviços e o ciclo de vida da aplicação.
public final class SistemaBikeShare {
    private final PersistenciaSistema persistencia = new PersistenciaSistema();
    private final Properties config = carregarConfiguracao();
    private final BigDecimal mensalidadePadrao = configurarMensalidade();
    private final Tarifas tarifas = new Tarifas(config);
    private final int bateriaMinima = configurarBateriaMinima();
    private EstadoSistema estado = persistencia.carregar();
    private CadastroUsuarios cadastro = new CadastroUsuarios(estado, mensalidadePadrao);
    private CadastroEstacoes estacoes = new CadastroEstacoes(estado, bateriaMinima);

    private GestaoBicicletas bicicletas = new GestaoBicicletas(estado, estacoes);

    private GestaoAlugueis alugueis =
            new GestaoAlugueis(estado, cadastro, estacoes, bicicletas, bateriaMinima, tarifas);

    public GestaoAlugueis alugueis() { return alugueis; }
    public GestaoBicicletas bicicletas() { return bicicletas; }
    public CadastroUsuarios usuarios() { return cadastro; }
    public CadastroEstacoes estacoes() { return estacoes; }

    public void zerar() {
        estado = new EstadoSistema();
        cadastro = new CadastroUsuarios(estado, mensalidadePadrao);
        estacoes = new CadastroEstacoes(estado, bateriaMinima);
        bicicletas = new GestaoBicicletas(estado, estacoes);
        alugueis = new GestaoAlugueis(estado, cadastro, estacoes, bicicletas, bateriaMinima, tarifas);
    }

    public void encerrar() { persistencia.salvar(estado); }

    private int configurarBateriaMinima() {
        int minimo;
        try {
            minimo = Integer.parseInt(config.getProperty("bicicleta.eletrica.bateriaMinima", "15"));
        } catch (NumberFormatException ex) {
            throw new ConfiguracaoException("Bateria minima deve ser numerica.", ex);
        }
        if (minimo < 0 || minimo > 100) {
            throw new ConfiguracaoException("Bateria minima deve estar entre 0 e 100.");
        }
        return minimo;
    }

    private BigDecimal configurarMensalidade() {
        try {
            BigDecimal mensalidade = new BigDecimal(config.getProperty("plano.assinante.mensalidadePadrao", "30.00"));
            if (mensalidade.signum() >= 0) return mensalidade;
        } catch (NumberFormatException ex) {
            throw new ConfiguracaoException("Mensalidade padrao invalida.", ex);
        }
        throw new ConfiguracaoException("Mensalidade padrao invalida.");
    }

    private static Properties carregarConfiguracao() {
        Properties config = new Properties();
        Path arquivo = Path.of("config.properties");
        if (Files.exists(arquivo)) {
            try (InputStream entrada = Files.newInputStream(arquivo)) {
                config.load(entrada);
            } catch (IOException ex) {
                throw new ConfiguracaoException("Nao foi possivel ler a configuracao.", ex);
            }
        }
        return config;
    }
}
