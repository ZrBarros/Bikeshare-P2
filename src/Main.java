import easyaccept.EasyAccept;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String FACADE = "br.ufal.ic.p2.bikeshare.Facade";

    public static void main(String[] args) {
        if (args.length == 1 && "--m1".equals(args[0])) {
            executar(arquivos(9));
        } else if (args.length == 0) {
            executar(arquivos(16));
        } else {
            executar(List.of(args));
        }
    }

    private static List<String> arquivos(int ultimaHistoria) {
        List<String> arquivos = new ArrayList<>();
        for (int historia = 1; historia <= ultimaHistoria; historia++) {
            arquivos.add("tests/us" + historia + ".txt");
            if (historia != 2 && historia != 7) arquivos.add("tests/us" + historia + "_1.txt");
        }
        return arquivos;
    }

    private static void executar(List<String> arquivos) {
        for (String arquivo : arquivos) {
            EasyAccept.main(new String[]{FACADE, arquivo});
        }
    }
}
