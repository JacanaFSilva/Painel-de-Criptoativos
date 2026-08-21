package po23s.controller;

import po23s.http.ClienteHttp;
import po23s.service.MoedaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Locale;

public class CryptoDashboardController {
    private final DefaultTableModel tableModel;
    private final List<String> listaCriptos;
    private final ClienteHttp cliente;
    private final JFrame parent;
    private final String ARQUIVO_MOEDAS = System.getProperty("user.dir") + "/moedas.txt";
    private final Set<String> STABLECOINS_USD = new HashSet<>(
            Arrays.asList("USDT", "USDC", "BUSD", "TUSD", "DAI", "PAX", "GUSD", "USDP", "USDD", "FDUSD"));
    private final Set<String> MOEDAS_G20 = new HashSet<>(Arrays.asList("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CNY",
            "INR", "RUB", "BRL", "MXN", "KRW", "TRY", "SAR", "ZAR", "IDR", "ARS"));
    private final MoedaService moedaService = new MoedaService();

    public CryptoDashboardController(DefaultTableModel tableModel, List<String> listaCriptos, JFrame parent) {
        this.tableModel = tableModel;
        this.listaCriptos = listaCriptos;
        this.parent = parent;
        this.cliente = new ClienteHttp();
    }

    public void adicionarCripto(String inputMoeda) {
        String input = inputMoeda.toUpperCase().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Informe uma moeda!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (listaCriptos.contains(input)) {
            JOptionPane.showMessageDialog(parent, "Moeda já adicionada!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!moedaService.isMoedaValida(input)) {
            JOptionPane.showMessageDialog(parent,
                    "Moeda não encontrada em Binance, Mercado Bitcoin, G20 ou Stablecoins.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        listaCriptos.add(input);
        SwingUtilities.invokeLater(() -> {
            tableModel
                    .addRow(new Object[] { input, "Carregando...", "Carregando...", "Carregando...", "Carregando..." });
            atualizarTodosDados();
        });
        salvarMoedasEmArquivo();
    }

    public void removerSelecionado(JTable tabela) {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            String moeda = (String) tableModel.getValueAt(linha, 0);
            listaCriptos.remove(moeda);
            tableModel.removeRow(linha);
            salvarMoedasEmArquivo();
            return;
        }
        JOptionPane.showMessageDialog(parent, "Selecione uma moeda para remover.", "Aviso",
                JOptionPane.WARNING_MESSAGE);
    }

    public void exibirDetalhesMoeda(String moeda) {
        StringBuilder info = new StringBuilder();
        info.append("Detalhes da moeda: ").append(moeda).append("\n\n");
        if (isStablecoin(moeda)) {
            String usdtBuy = "N/D";
            try {
                String urlMB = "https://www.mercadobitcoin.net/api/USDT/ticker";
                String json = cliente.buscaDados(urlMB);
                org.json.JSONObject obj = new org.json.JSONObject(json).getJSONObject("ticker");
                info.append("[Mercado Bitcoin - USDT]\n");
                info.append("Último preço: ").append(obj.optString("last", "N/D")).append("\n");
                info.append("Alta 24h: ").append(obj.optString("high", "N/D")).append("\n");
                info.append("Baixa 24h: ").append(obj.optString("low", "N/D")).append("\n");
                info.append("Volume: ").append(obj.optString("vol", "N/D")).append("\n");
                info.append("Compra em real: ").append(obj.optString("buy", "N/D")).append("\n");
                info.append("Venda em real: ").append(obj.optString("sell", "N/D")).append("\n");
                info.append("Variação 24h: ").append(obj.optString("var24h", "N/D")).append("\n");
                usdtBuy = obj.optString("buy", "N/D");
            } catch (Exception e) {
                info.append("[Mercado Bitcoin - USDT]\nPar não negociado ou indisponível.\n");
            }
            // Cálculo da variação USDT vs USD
            try {
                String[] cotacaoUSD = buscarCotacaoG20("USD");
                double usdtBRL = Double.parseDouble(usdtBuy.replace(",", "."));
                double usdBRL = Double.parseDouble(cotacaoUSD[0].replace(",", "."));
                if (usdtBRL > 0 && usdBRL > 0) {
                    double variacao = ((usdtBRL - usdBRL) / usdBRL) * 100.0;
                    info.append("Variação USDT vs USD (%): ")
                            .append(String.format(Locale.US, "%.2f", variacao)).append("%\n");
                } else {
                    info.append("Variação USDT vs USD (%): N/D\n");
                }
            } catch (Exception e) {
                info.append("Variação USDT vs USD (%): N/D\n");
            }
        } else if (MOEDAS_G20.contains(moeda)) {
            String[] cotacoes = buscarCotacaoG20(moeda);
            info.append("[Moeda Fiat - G20]\n");
            info.append("Último preço: ").append(cotacoes[1]).append("\n");
            info.append("Alta 24h: N/D\n");
            info.append("Baixa 24h: N/D\n");
            info.append("Volume: N/D\n");
            info.append("Compra em real: ").append(cotacoes[0]).append("\n");
            info.append("Venda em real: ").append(cotacoes[0]).append("\n");
            info.append("Variação 24h: N/D\n");
            if ("BRL".equals(moeda)) {
                info.append("Compra em dólar: ").append(cotacoes[1]).append("\n");
                info.append("Venda em dólar: ").append(cotacoes[1]).append("\n");
            } else if ("USD".equals(moeda)) {
                info.append("Compra em dólar: 1.00\n");
                info.append("Venda em dólar: 1.00\n");
            } else {
                info.append("Compra em dólar: ").append(cotacoes[1]).append("\n");
                info.append("Venda em dólar: ").append(cotacoes[1]).append("\n");
            }
            // Cálculo da variação USD vs USDT
            try {
                String urlMB = "https://www.mercadobitcoin.net/api/USDT/ticker";
                String json = cliente.buscaDados(urlMB);
                org.json.JSONObject obj = new org.json.JSONObject(json).getJSONObject("ticker");
                String usdtBuy = obj.optString("buy", "0");
                double usdBRL = Double.parseDouble(cotacoes[0].replace(",", "."));
                double usdtBRL = Double.parseDouble(usdtBuy.replace(",", "."));
                if (usdBRL > 0 && usdtBRL > 0) {
                    double variacao = ((usdBRL - usdtBRL) / usdtBRL) * 100.0;
                    info.append("Variação USD vs USDT (%): ")
                            .append(String.format(Locale.US, "%.2f", variacao)).append("%\n");
                } else {
                    info.append("Variação USD vs USDT (%): N/D\n");
                }
            } catch (Exception e) {
                info.append("Variação USD vs USDT (%): N/D\n");
            }
            info.append("Fonte: exchangerate.host, frankfurter.app, open.er-api.com\n");
            if ("N/D".equals(cotacoes[0]) && "N/D".equals(cotacoes[1])) {
                info.append("Não foi possível obter a cotação no momento.\n");
            }
        } else {
            try {
                String urlMB = "https://www.mercadobitcoin.net/api/" + moeda + "/ticker";
                String json = cliente.buscaDados(urlMB);
                org.json.JSONObject obj = new org.json.JSONObject(json).getJSONObject("ticker");
                info.append("[Mercado Bitcoin]\n");
                info.append("Último preço: ").append(obj.optString("last", "N/D")).append("\n");
                info.append("Alta 24h: ").append(obj.optString("high", "N/D")).append("\n");
                info.append("Baixa 24h: ").append(obj.optString("low", "N/D")).append("\n");
                info.append("Volume: ").append(obj.optString("vol", "N/D")).append("\n");
                info.append("Compra em real: ").append(obj.optString("buy", "N/D")).append("\n");
                info.append("Venda em real: ").append(obj.optString("sell", "N/D")).append("\n");
                info.append("Variação 24h: ").append(obj.optString("var24h", "N/D")).append("\n");
            } catch (Exception e) {
                info.append("[Mercado Bitcoin]\nPar não negociado ou indisponível.\n");
            }
            try {
                String simboloBinance = encontrarParNaBinance(moeda);
                if (simboloBinance != null) {
                    String urlBNB = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + simboloBinance;
                    String json = cliente.buscaDados(urlBNB);
                    org.json.JSONObject obj = new org.json.JSONObject(json);
                    info.append("[Binance - Par: ").append(simboloBinance).append("]\n");
                    info.append("Último preço: ").append(obj.optString("lastPrice", "N/D")).append("\n");
                    info.append("Alta 24h: ").append(obj.optString("highPrice", "N/D")).append("\n");
                    info.append("Baixa 24h: ").append(obj.optString("lowPrice", "N/D")).append("\n");
                    info.append("Volume: ").append(obj.optString("volume", "N/D")).append("\n");
                    info.append("Variação 24h (%): ").append(obj.optString("priceChangePercent", "N/D")).append("\n");
                    info.append("Compra em dólar: ").append(obj.optString("bidPrice", "N/D")).append("\n");
                } else {
                    info.append("[Binance]\nPar não negociado ou indisponível.\n");
                }
            } catch (Exception e) {
                info.append("[Binance]\nErro ao buscar dados: ").append(e.getMessage()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(parent, info.toString(), "Detalhes de " + moeda, JOptionPane.INFORMATION_MESSAGE);
    }

    public void atualizarTodosDados() {
        String[] cotacaoUSD = buscarCotacaoUSD();
        double usdToBrl = 0;
        try {
            usdToBrl = Double.parseDouble(cotacaoUSD[0].replace(",", "."));
        } catch (Exception e) {
            usdToBrl = 0;
        }
        for (int i = 0; i < listaCriptos.size(); i++) {
            final int row = i;
            final String moeda = listaCriptos.get(i);
            String compraMB = "N/D";
            String vendaMB = "N/D";
            String compraBNB = "N/D";
            String vendaBNB = "N/D";

            if ("BRL".equals(moeda)) {
                String[] cotacoes = buscarCotacaoG20("BRL");
                compraMB = vendaMB = "1.00";
                compraBNB = vendaBNB = cotacoes[1]; // BRL→USD (spot, valor real da API)
            } else if (isStablecoin(moeda)) {
                compraMB = buscarValorUSDT("buy");
                vendaMB = buscarValorUSDT("sell");
                if (compraMB == null || compraMB.equals("N/D") || compraMB.equals("0") || compraMB.isEmpty())
                    compraMB = "Par não negociado";
                if (vendaMB == null || vendaMB.equals("N/D") || vendaMB.equals("0") || vendaMB.isEmpty())
                    vendaMB = "Par não negociado";
                double compraUSDT = 0, vendaUSDT = 0;
                try {
                    compraUSDT = Double.parseDouble(compraMB.replace(",", "."));
                } catch (Exception e) {
                }
                try {
                    vendaUSDT = Double.parseDouble(vendaMB.replace(",", "."));
                } catch (Exception e) {
                }
                if (compraUSDT > 0 && usdToBrl > 0) {
                    compraBNB = String.format(Locale.US, "%.4f", compraUSDT / usdToBrl);
                } else {
                    compraBNB = "N/D";
                }
                if (vendaUSDT > 0 && usdToBrl > 0) {
                    vendaBNB = String.format(Locale.US, "%.4f", vendaUSDT / usdToBrl);
                } else {
                    vendaBNB = "N/D";
                }
            } else if (MOEDAS_G20.contains(moeda)) {
                String[] cotacoes = buscarCotacaoG20(moeda);
                if ("BRL".equals(moeda)) {
                    compraMB = vendaMB = "1.00";
                    compraBNB = vendaBNB = cotacoes[1]; // BRL→USD (spot, valor real da API)
                } else if ("USD".equals(moeda)) {
                    compraMB = vendaMB = cotacoes[0]; // USD→BRL (spot)
                    compraBNB = vendaBNB = "1.00";
                } else {
                    compraMB = vendaMB = cotacoes[0]; // MOEDA→BRL (spot)
                    compraBNB = vendaBNB = cotacoes[1]; // MOEDA→USD (spot)
                }
                // Se algum valor vier vazio ou nulo, exibe N/D
                if (compraMB == null || compraMB.isEmpty())
                    compraMB = "N/D";
                if (vendaMB == null || vendaMB.isEmpty())
                    vendaMB = "N/D";
                if (compraBNB == null || compraBNB.isEmpty())
                    compraBNB = "N/D";
                if (vendaBNB == null || vendaBNB.isEmpty())
                    vendaBNB = "N/D";
            } else {
                boolean encontrado = false;
                String compraUSD = "N/D", vendaUSD = "N/D";
                try {
                    String simboloBinance = encontrarParNaBinance(moeda);
                    if (simboloBinance != null) {
                        String urlBNB = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=" + simboloBinance;
                        String json = cliente.buscaDados(urlBNB);
                        org.json.JSONObject obj = new org.json.JSONObject(json);
                        compraUSD = obj.optString("bidPrice", "N/D");
                        vendaUSD = obj.optString("askPrice", "N/D");
                        encontrado = true;
                    }
                } catch (Exception e) {
                }
                if (encontrado && usdToBrl > 0) {
                    try {
                        double compra = Double.parseDouble(compraUSD.replace(",", "."));
                        double venda = Double.parseDouble(vendaUSD.replace(",", "."));
                        compraMB = String.format(Locale.US, "%.2f", compra * usdToBrl);
                        vendaMB = String.format(Locale.US, "%.2f", venda * usdToBrl);
                        compraBNB = compraUSD;
                        vendaBNB = vendaUSD;
                    } catch (Exception e) {
                        compraMB = vendaMB = compraBNB = vendaBNB = "N/D";
                    }
                } else {
                    compraMB = vendaMB = compraBNB = vendaBNB = "N/D";
                }
            }
            final String finalCompraMB = formatarNumero(compraMB);
            final String finalVendaMB = formatarNumero(vendaMB);
            final String finalCompraBNB = formatarNumero(compraBNB);
            final String finalVendaBNB = formatarNumero(vendaBNB);
            SwingUtilities.invokeLater(() -> {
                tableModel.setValueAt(finalCompraMB, row, 1);
                tableModel.setValueAt(finalVendaMB, row, 2);
                tableModel.setValueAt(finalCompraBNB, row, 3);
                tableModel.setValueAt(finalVendaBNB, row, 4);
            });
        }
    }

    public void carregarMoedasSalvas() {
        listaCriptos.clear();
        tableModel.setRowCount(0);
        try {
            java.util.List<String> linhas = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(ARQUIVO_MOEDAS));
            for (String moeda : linhas) {
                moeda = moeda.trim().toUpperCase();
                if (!moeda.isEmpty() && !listaCriptos.contains(moeda)) {
                    listaCriptos.add(moeda);
                    tableModel.addRow(
                            new Object[] { moeda, "Carregando...", "Carregando...", "Carregando...", "Carregando..." });
                }
            }
        } catch (Exception e) {
            System.out.println("Nenhum arquivo de moedas salvo encontrado.");
        }
        atualizarTodosDados();
    }

    private boolean isStablecoin(String moeda) {
        return STABLECOINS_USD.contains(moeda);
    }

    private String encontrarParNaBinance(String simboloMoeda) {
        try {
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
                        return parAlvo;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro exchangeInfo Binance: " + e.getMessage());
        }
        return null;
    }

    private String[] buscarCotacaoG20(String moeda) {
        String compraEmReal = "N/D";
        String compraEmDolar = "N/D";
        try {
            // Busca sempre as duas cotações diretamente via exchangerate.host
            String url = "https://api.exchangerate.host/latest?base=" + moeda + "&symbols=BRL,USD";
            String json = cliente.buscaDados(url);
            org.json.JSONObject obj = new org.json.JSONObject(json);
            org.json.JSONObject rates = obj.getJSONObject("rates");
            if (rates.has("BRL"))
                compraEmReal = formatarNumero(String.valueOf(rates.getDouble("BRL")));
            if (rates.has("USD"))
                compraEmDolar = formatarNumero(String.valueOf(rates.getDouble("USD")));
        } catch (Exception e) {
            // fallback para frankfurter.app
            try {
                String url = "https://api.frankfurter.app/latest?from=" + moeda + "&to=BRL,USD";
                String json = cliente.buscaDados(url);
                org.json.JSONObject obj = new org.json.JSONObject(json);
                org.json.JSONObject rates = obj.getJSONObject("rates");
                if (rates.has("BRL"))
                    compraEmReal = formatarNumero(String.valueOf(rates.getDouble("BRL")));
                if (rates.has("USD"))
                    compraEmDolar = formatarNumero(String.valueOf(rates.getDouble("USD")));
            } catch (Exception ignored) {
                // fallback para open.er-api.com
                try {
                    String url = "https://open.er-api.com/v6/latest/" + moeda;
                    String json = cliente.buscaDados(url);
                    org.json.JSONObject obj = new org.json.JSONObject(json);
                    org.json.JSONObject rates = obj.getJSONObject("rates");
                    if (rates.has("BRL"))
                        compraEmReal = formatarNumero(String.valueOf(rates.getDouble("BRL")));
                    if (rates.has("USD"))
                        compraEmDolar = formatarNumero(String.valueOf(rates.getDouble("USD")));
                } catch (Exception ignored2) {
                }
            }
        }
        // Ajuste para BRL e USD: exibir 1.00 nas suas próprias colunas
        if ("BRL".equals(moeda)) {
            compraEmReal = "1.00";
        }
        if ("USD".equals(moeda)) {
            compraEmDolar = "1.00";
        }
        return new String[] { compraEmReal, compraEmDolar };
    }

    private void salvarMoedasEmArquivo() {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(ARQUIVO_MOEDAS), listaCriptos);
        } catch (Exception e) {
            System.err.println("Erro ao salvar moedas: " + e.getMessage());
        }
    }

    private String formatarNumero(String valor) {
        try {
            double numero = Double.parseDouble(valor);
            return String.format("%.2f", numero);
        } catch (Exception e) {
            return valor;
        }
    }

    private String[] buscarCotacaoUSD() {
        String brl = "N/D";
        String usd = formatarNumero("1.00");
        try {
            String url = "https://api.exchangerate.host/latest?base=USD&symbols=BRL,USD";
            String json = cliente.buscaDados(url);
            org.json.JSONObject obj = new org.json.JSONObject(json);
            org.json.JSONObject rates = obj.getJSONObject("rates");
            brl = rates.has("BRL") ? formatarNumero(String.valueOf(rates.getDouble("BRL"))) : "N/D";
        } catch (Exception e) {
        }
        if (!brl.equals("N/D"))
            return new String[] { brl, usd };
        try {
            String url = "https://api.frankfurter.app/latest?from=USD&to=BRL";
            String json = cliente.buscaDados(url);
            org.json.JSONObject obj = new org.json.JSONObject(json);
            org.json.JSONObject rates = obj.getJSONObject("rates");
            brl = rates.has("BRL") ? formatarNumero(String.valueOf(rates.getDouble("BRL"))) : "N/D";
        } catch (Exception e) {
        }
        if (!brl.equals("N/D"))
            return new String[] { brl, usd };
        try {
            String url = "https://open.er-api.com/v6/latest/USD";
            String json = cliente.buscaDados(url);
            org.json.JSONObject obj = new org.json.JSONObject(json);
            org.json.JSONObject rates = obj.getJSONObject("rates");
            brl = rates.has("BRL") ? formatarNumero(String.valueOf(rates.getDouble("BRL"))) : "N/D";
        } catch (Exception e) {
        }
        return new String[] { brl, usd };
    }

    private String buscarValorUSDT(String tipo) {
        try {
            String urlMB = "https://www.mercadobitcoin.net/api/USDT/ticker";
            String json = cliente.buscaDados(urlMB);
            org.json.JSONObject obj = new org.json.JSONObject(json).getJSONObject("ticker");
            String valor = obj.getString(tipo);
            if (!valor.equals("0") && !valor.isEmpty())
                return valor;
        } catch (Exception e) {
        }
        try {
            String simboloBinance = encontrarParNaBinance("USDT");
            if (simboloBinance != null) {
                String urlBNB = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=" + simboloBinance;
                String json = cliente.buscaDados(urlBNB);
                org.json.JSONObject obj = new org.json.JSONObject(json);
                String valor = tipo.equals("buy") ? obj.getString("bidPrice") : obj.getString("askPrice");
                if (!valor.equals("0") && !valor.isEmpty())
                    return valor;
            }
        } catch (Exception e) {
        }
        String[] cotacaoUSD = buscarCotacaoUSD();
        return cotacaoUSD[0];
    }

    // ...restante dos métodos utilitários, requisições, cálculos, persistência,
    // atualização de tabela, etc. (copiados da view original)
}
