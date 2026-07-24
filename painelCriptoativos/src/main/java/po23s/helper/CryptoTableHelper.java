package po23s.helper;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CryptoTableHelper {
    private final DefaultTableModel tableModel;
    private final List<String> listaCriptos;

    public CryptoTableHelper(DefaultTableModel tableModel, List<String> listaCriptos) {
        this.tableModel = tableModel;
        this.listaCriptos = listaCriptos;
    }

    public CryptoTableHelper() {
        this.tableModel = null;
        this.listaCriptos = null;
    }

    public boolean adicionarMoeda(String moeda) {
        if (listaCriptos.contains(moeda))
            return false;
        listaCriptos.add(moeda);
        tableModel.addRow(new Object[] { moeda, "Carregando...", "Carregando...", "Carregando...", "Carregando..." });
        return true;
    }

    public boolean removerMoedaPorLinha(int linha) {
        if (linha < 0 || linha >= tableModel.getRowCount())
            return false;
        String moeda = (String) tableModel.getValueAt(linha, 0);
        listaCriptos.remove(moeda);
        tableModel.removeRow(linha);
        return true;
    }

    public boolean removerMoedaPorNome(String moeda) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(moeda)) {
                tableModel.removeRow(i);
                listaCriptos.remove(moeda);
                return true;
            }
        }
        return false;
    }

    public void carregarMoedas(List<String> moedas) {
        listaCriptos.clear();
        for (String moeda : moedas) {
            adicionarMoeda(moeda);
        }
    }

    public void adicionarLinha(DefaultTableModel tableModel, String moeda) {
        tableModel.addRow(new Object[] { moeda, "Carregando...", "Carregando...", "Carregando...", "Carregando..." });
    }
}
