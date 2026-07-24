package po23s.updater;

import po23s.service.CryptoService;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CryptoTableUpdater {
    private final DefaultTableModel tableModel;
    // Removed unused field cryptoService

    public CryptoTableUpdater(DefaultTableModel tableModel, CryptoService cryptoService) {
        this.tableModel = tableModel;
        // Removed assignment to unused field cryptoService
    }

    public void atualizarTabela(List<String> moedas) {
        for (String moeda : moedas) {
            // Simulação de busca de dados (substitua pela lógica real)
            String preco = "R$ " + (Math.random() * 100000);
            String variacao = String.format("%.2f%%", (Math.random() * 10 - 5));
            tableModel.addRow(new Object[] { moeda, preco, variacao });
        }
    }
}
