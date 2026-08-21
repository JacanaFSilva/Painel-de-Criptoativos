package po23s.service;

import org.json.JSONObject;
import po23s.common.NumberFormatter;

import java.util.ArrayList;
import java.util.List;

public class CryptoDataUpdater {
    private final CryptoService cryptoService;

    public static class DadosCripto {
        public final String moeda;
        public final String compraMB;
        public final String vendaMB;
        public final String compraBNB;
        public final String vendaBNB;

        public DadosCripto(String moeda, String compraMB, String vendaMB, String compraBNB, String vendaBNB) {
            this.moeda = moeda;
            this.compraMB = compraMB;
            this.vendaMB = vendaMB;
            this.compraBNB = compraBNB;
            this.vendaBNB = vendaBNB;
        }
    }

    public CryptoDataUpdater(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public DadosCripto atualizarDados(String moeda) {
        String compraMB = "Par não negociado";
        String vendaMB = "Par não negociado";
        try {
            JSONObject obj = cryptoService.buscarTickerMercadoBitcoin(moeda);
            compraMB = obj.getString("buy");
            vendaMB = obj.getString("sell");
        } catch (Exception ignored) {
        }

        String compraBNB = "Par não negociado";
        String vendaBNB = "Par não negociado";
        try {
            String simboloBinance = cryptoService.encontrarParNaBinance(moeda);
            if (simboloBinance != null) {
                JSONObject obj = cryptoService.buscarTickerBinance(simboloBinance);
                compraBNB = obj.getString("bidPrice");
                vendaBNB = obj.getString("askPrice");
            }
        } catch (Exception ignored) {
        }

        return new DadosCripto(
                moeda,
                NumberFormatter.formatarNumero(compraMB),
                NumberFormatter.formatarNumero(vendaMB),
                NumberFormatter.formatarNumero(compraBNB),
                NumberFormatter.formatarNumero(vendaBNB));
    }

    public List<DadosCripto> atualizarTodos(List<String> moedas) {
        List<DadosCripto> lista = new ArrayList<>();
        for (String moeda : moedas) {
            lista.add(atualizarDados(moeda));
        }
        return lista;
    }
}
