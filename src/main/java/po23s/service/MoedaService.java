package po23s.service;

import po23s.common.MoedaRepository;

import java.util.List;

public class MoedaService {
    private final MoedaRepository moedaRepository;

    public MoedaService(MoedaRepository moedaRepository) {
        this.moedaRepository = moedaRepository;
    }

    public MoedaService() {
        this.moedaRepository = new MoedaRepository(System.getProperty("user.dir") + "/moedas.txt");
    }

    // Métodos utilitários de moeda, validação, normalização, etc.
    // ...

    public boolean isMoedaValida(String moeda) {
        return MoedaRepository.STABLECOINS_USD.contains(moeda)
                || MoedaRepository.MOEDAS_G20.contains(moeda)
                || moedaRepository.existeNaBinance(moeda)
                || moedaRepository.existeNoMercadoBitcoin(moeda);
    }

    public void salvarMoedasEmArquivo(List<String> listaCriptos) {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(System.getProperty("user.dir") + "/moedas.txt"),
                    listaCriptos);
        } catch (Exception e) {
            System.err.println("Erro ao salvar moedas: " + e.getMessage());
        }
    }

    public List<String> carregarMoedasDoArquivo() {
        return moedaRepository.carregarMoedas();
    }
}
