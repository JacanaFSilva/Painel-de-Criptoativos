package po23s.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfiguracaoRepository {
    private final String arquivoConfig;

    public ConfiguracaoRepository(String arquivoConfig) {
        this.arquivoConfig = arquivoConfig;
    }

    public boolean carregarModoEscuro() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(arquivoConfig)) {
            props.load(in);
            return Boolean.parseBoolean(props.getProperty("modoEscuro", "false"));
        } catch (IOException e) {
            System.out.println("Arquivo de configuração não encontrado. Usando tema claro.");
            return false;
        }
    }

    public void salvarModoEscuro(boolean modoEscuroAtivo) {
        try {
            Properties props = new Properties();
            props.setProperty("modoEscuro", String.valueOf(modoEscuroAtivo));
            try (FileOutputStream out = new FileOutputStream(arquivoConfig)) {
                props.store(out, "Configurações do CryptoDashboard");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar configuração: " + e.getMessage());
        }
    }
}
