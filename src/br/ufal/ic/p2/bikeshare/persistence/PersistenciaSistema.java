package br.ufal.ic.p2.bikeshare.persistence;

import br.ufal.ic.p2.bikeshare.exception.PersistenciaException;
import br.ufal.ic.p2.bikeshare.service.EstadoSistema;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Salva com serialização Java e troca atômica sempre que suportada pelo sistema de arquivos. */
public final class PersistenciaSistema {
    private final Path arquivo;

    public PersistenciaSistema() { this(Path.of("bikeshare-state.bin")); }

    public PersistenciaSistema(Path arquivo) {
        this.arquivo = Valores.naoNulo(arquivo, "Arquivo de persistencia nao pode ser nulo.");
    }

    public EstadoSistema carregar() {
        if (!Files.exists(arquivo)) {
            return new EstadoSistema();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            return (EstadoSistema) entrada.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            throw new PersistenciaException("Nao foi possivel carregar o estado do sistema.", ex);
        }
    }

    public void salvar(EstadoSistema estado) {
        Valores.naoNulo(estado, "Estado do sistema nao pode ser nulo.");
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(estado);
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING,
                           StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new PersistenciaException("Nao foi possivel salvar o estado do sistema.", ex);
        }
    }
}
