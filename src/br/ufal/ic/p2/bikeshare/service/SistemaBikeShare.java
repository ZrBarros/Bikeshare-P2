package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.persistence.PersistenciaSistema;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Coordena os serviços e o ciclo de vida da aplicação. */
public final class SistemaBikeShare {
    private final PersistenciaSistema persistencia = new PersistenciaSistema();
    private final BigDecimal mensalidadePadrao = configurarMensalidade();
    private EstadoSistema estado = persistencia.carregar();
    private CadastroUsuarios cadastro = new CadastroUsuarios(estado, mensalidadePadrao);
    private CadastroEstacoes estacoes = new CadastroEstacoes(estado);

    public CadastroUsuarios usuarios() { return cadastro; }
    public CadastroEstacoes estacoes() { return estacoes; }

    public void zerar() {
        estado = new EstadoSistema();
        cadastro = new CadastroUsuarios(estado, mensalidadePadrao);
        estacoes = new CadastroEstacoes(estado);
    }

    public void encerrar() { persistencia.salvar(estado); }

    private static BigDecimal configurarMensalidade() {
        Properties config = new Properties();
        Path arquivo = Path.of("config.properties");
        if (Files.exists(arquivo)) {
            try (InputStream entrada = Files.newInputStream(arquivo)) {
                config.load(entrada);
            } catch (IOException ex) {
                throw new IllegalStateException("Nao foi possivel ler a configuracao.", ex);
            }
        }
        return new BigDecimal(config.getProperty("plano.assinante.mensalidadePadrao", "30.00"));
    }
}
