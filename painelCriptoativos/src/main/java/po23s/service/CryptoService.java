package po23s.service;

import org.json.JSONObject;
import po23s.http.ClienteHttp;

public class CryptoService {

    private final ClienteHttp cliente;

    public CryptoService(ClienteHttp cliente) {
        this.cliente = cliente;
    }

    public boolean existeNoMercadoBitcoin(String moeda) {
        try {
            String url = "https://www.mercadobitcoin.net/api/" + moeda + "/ticker";
            String json = cliente.buscaDados(url);
            JSONObject obj = new JSONObject(json);
            return obj.has("ticker");
        } catch (Exception e) {
            return false;
        }
    }

    public String encontrarParNaBinance(String simboloMoeda) {
        try {
            String url = "https://api.binance.com/api/v3/exchangeInfo";
            String json = cliente.buscaDados(url);
            JSONObject obj = new JSONObject(json);
            var symbols = obj.getJSONArray("symbols");

            String[] preferenciais = { "USDT", "BUSD", "BTC", "ETH" };

            for (String preferido : preferenciais) {
                String parAlvo = simboloMoeda + preferido;
                for (int i = 0; i < symbols.length(); i++) {
                    JSONObject par = symbols.getJSONObject(i);
                    if (par.getString("symbol").equalsIgnoreCase(parAlvo)) {
                        return parAlvo;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro exchangeInfo Binance: " + e.getMessage());
        }
        return null;
    }

    public JSONObject buscarTickerMercadoBitcoin(String moeda) throws Exception {
        String urlMB = "https://www.mercadobitcoin.net/api/" + moeda + "/ticker";
        String json = cliente.buscaDados(urlMB);
        return new JSONObject(json).getJSONObject("ticker");
    }

    public JSONObject buscarTickerBinance(String simboloBinance) throws Exception {
        String urlBNB = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=" + simboloBinance;
        String json = cliente.buscaDados(urlBNB);
        return new JSONObject(json);
    }

    public JSONObject buscar24hBinance(String simboloBinance) throws Exception {
        String urlBNB = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + simboloBinance;
        String json = cliente.buscaDados(urlBNB);
        return new JSONObject(json);
    }
}
