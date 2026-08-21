package po23s.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import po23s.http.ClienteHttp;

public class MoedaRepository {
    private final String arquivoMoedas;
    public static final Set<String> STABLECOINS_USD = Set.of("USDT", "USDC", "BUSD", "TUSD", "DAI", "PAX", "GUSD",
            "USDP", "USDD", "FDUSD");
    public static final Set<String> MOEDAS_G20 = Set.of("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CNY", "INR", "RUB",
            "BRL", "MXN", "KRW", "TRY", "SAR", "ZAR", "IDR", "ARS");

    public MoedaRepository(String arquivoMoedas) {
        this.arquivoMoedas = arquivoMoedas;
    }

    public List<String> carregarMoedas() {
        List<String> moedas = new ArrayList<>();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(arquivoMoedas));
            for (String moeda : linhas) {
                moeda = moeda.trim().toUpperCase();
                if (!moeda.isEmpty() && !moedas.contains(moeda)) {
                    moedas.add(moeda);
                }
            }
        } catch (IOException e) {
            System.out.println("Nenhum arquivo de moedas salvo encontrado.");
        }
        return moedas;
    }

    public void salvarMoedas(List<String> moedas) {
        try {
            Files.write(Paths.get(arquivoMoedas), moedas);
        } catch (Exception e) {
            System.err.println("Erro ao salvar moedas: " + e.getMessage());
        }
    }

    public boolean existeNaBinance(String simboloMoeda) {
        try {
            ClienteHttp cliente = new ClienteHttp();
            String url = "https://api.binance.com/api/v3/exchangeInfo";
            String json = cliente.buscaDados(url);
            org.json.JSONObject obj = new org.json.JSONObject(json);
            var symbols = obj.getJSONArray("symbols");
            String[] preferenciais = { "USDT", "BUSD", "BTC", "ETH" };
            for (String preferido : preferenciais) {
                String parAlvo = simboloMoeda + preferido;
                for (int i = 0; i < symbols.length(); i++) {
                    org.json.JSONObject par = symbols.getJSONObject(i);
                    if (par.getString("symbol").equalsIgnoreCase(parAlvo)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro exchangeInfo Binance: " + e.getMessage());
        }
        return false;
    }

    public boolean existeNoMercadoBitcoin(String moeda) {
        try {
            ClienteHttp cliente = new ClienteHttp();
            String url = "https://www.mercadobitcoin.net/api/" + moeda + "/ticker";
            String json = cliente.buscaDados(url);
            org.json.JSONObject obj = new org.json.JSONObject(json);
            return obj.has("ticker");
        } catch (Exception e) {
            return false;
        }
    }
}
