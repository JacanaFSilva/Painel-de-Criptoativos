package po23s.service;

import po23s.http.ClienteHttp;
import po23s.common.MoedaRepository;

public class CotacaoService {
    private final ClienteHttp clienteHttp;
    private final MoedaRepository moedaRepository;

    public CotacaoService(ClienteHttp clienteHttp, MoedaRepository moedaRepository) {
        this.clienteHttp = clienteHttp;
        this.moedaRepository = moedaRepository;
    }

    public CotacaoService() {
        this.clienteHttp = null;
        this.moedaRepository = null;
    }

    // Métodos de obtenção de cotações, conversão, fallback, etc.
    // ...

    // Exemplo de uso do clienteHttp para evitar warning de campo não utilizado
    public ClienteHttp getClienteHttp() {
        return clienteHttp;
    }

    // Getter para evitar warning de campo não utilizado
    public MoedaRepository getMoedaRepository() {
        return moedaRepository;
    }
}
