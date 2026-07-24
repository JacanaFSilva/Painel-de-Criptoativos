package po23s.service;

import org.json.JSONObject;

public class DetalhesMoedaFormatter {
    private final CryptoService cryptoService;

    public DetalhesMoedaFormatter(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public String formatarDetalhes(String moeda) {
        StringBuilder info = new StringBuilder();
        info.append("Detalhes da moeda: ").append(moeda).append("\n\n");

        try {
            JSONObject obj = cryptoService.buscarTickerMercadoBitcoin(moeda);
            info.append("[Mercado Bitcoin]\n");
            info.append("Último preço: ").append(obj.getString("last")).append("\n");
            info.append("Alta 24h: ").append(obj.getString("high")).append("\n");
            info.append("Baixa 24h: ").append(obj.getString("low")).append("\n");
            info.append("Volume: ").append(obj.getString("vol")).append("\n");
            info.append("Compra em real: ").append(obj.getString("buy")).append("\n");
            info.append("Venda em real: ").append(obj.getString("sell")).append("\n");
            info.append("Variação 24h: ").append(obj.optString("var24h", "N/D")).append("\n\n");
        } catch (Exception e) {
            info.append("[Mercado Bitcoin]\nPar não negociado ou indisponível.\n\n");
        }

        try {
            String simboloBinance = cryptoService.encontrarParNaBinance(moeda);
            if (simboloBinance != null) {
                JSONObject obj = cryptoService.buscar24hBinance(simboloBinance);
                info.append("[Binance - Par: ").append(simboloBinance).append("]\n");
                info.append("Último preço: ").append(obj.getString("lastPrice")).append("\n");
                info.append("Alta 24h: ").append(obj.getString("highPrice")).append("\n");
                info.append("Baixa 24h: ").append(obj.getString("lowPrice")).append("\n");
                info.append("Volume: ").append(obj.getString("volume")).append("\n");
                info.append("Variação 24h (%): ").append(obj.getString("priceChangePercent")).append("\n");
                info.append("Compra em dólar: ").append(obj.getString("bidPrice")).append("\n");
                info.append("Venda em dólar: ").append(obj.getString("askPrice")).append("\n");
            } else {
                info.append("[Binance]\nPar não negociado ou indisponível.\n");
            }
        } catch (Exception e) {
            info.append("[Binance]\nErro ao buscar dados: ").append(e.getMessage()).append("\n");
        }

        return info.toString();
    }
}
