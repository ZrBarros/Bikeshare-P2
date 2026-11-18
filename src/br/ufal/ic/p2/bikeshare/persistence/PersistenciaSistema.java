package br.ufal.ic.p2.bikeshare.persistence;

import br.ufal.ic.p2.bikeshare.service.EstadoSistema;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Salva com serialização Java e troca atômica sempre que suportada pelo sistema de arquivos. */
public final class PersistenciaSistema {
    private final Path arquivo = Path.of("bikeshare-state.bin");

    public EstadoSistema carregar() {
        if (!Files.exists(arquivo)) {
            return new EstadoSistema();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            return (EstadoSistema) entrada.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException("Nao foi possivel carregar o estado do sistema.", ex);
        }
    }

    public void salvar(EstadoSistema estado) {
        Path temporario = Path.of("bikeshare-state.bin.tmp");
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
            throw new IllegalStateException("Nao foi possivel salvar o estado do sistema.", ex);
        }
    }
}
