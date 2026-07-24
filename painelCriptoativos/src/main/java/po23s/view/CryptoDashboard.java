package po23s.view;

import po23s.controller.CryptoDashboardController;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.io.*;

public class CryptoDashboard extends JFrame {
    private JTextField inputMoeda;
    private JButton btnAdicionar, btnRemover;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private List<String> listaCriptos = new ArrayList<>();
    private ScheduledExecutorService scheduler;
    private JCheckBox cbModoEscuro;
    private boolean modoEscuroAtivo = false;
    private CryptoDashboardController controller;

    public CryptoDashboard() {
        setTitle("Painel de Criptoativos");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/cryptoDashboardIcon.png")));
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Moeda (ex: BTC, ETH): "));
        inputMoeda = new JTextField(10);
        inputMoeda.addActionListener(e -> btnAdicionar.doClick());
        btnAdicionar = new JButton("Adicionar");
        btnRemover = new JButton("Remover");
        topPanel.add(inputMoeda);
        topPanel.add(btnAdicionar);
        topPanel.add(btnRemover);
        cbModoEscuro = new JCheckBox("Modo Escuro");
        topPanel.add(cbModoEscuro);
        add(topPanel, BorderLayout.NORTH);

        String[] colunas = { "Moeda", "Compra em real", "Venda em real", "Compra em dólar", "Venda em dólar" };
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        controller = new CryptoDashboardController(tableModel, listaCriptos, this);

        btnAdicionar.addActionListener(e -> controller.adicionarCripto(inputMoeda.getText()));
        btnRemover.addActionListener(e -> controller.removerSelecionado(tabela));
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row != -1) {
                        String moeda = (String) tableModel.getValueAt(row, 0);
                        controller.exibirDetalhesMoeda(moeda);
                    }
                }
            }
        });
        carregarConfiguracao();
        aplicarTemaFlatLaf();
        cbModoEscuro.addActionListener(e -> {
            modoEscuroAtivo = cbModoEscuro.isSelected();
            aplicarTemaFlatLaf();
            salvarConfiguracao();
        });
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(controller::atualizarTodosDados, 0, 60, TimeUnit.SECONDS);
        controller.carregarMoedasSalvas();
    }

    private void aplicarTemaFlatLaf() {
        try {
            if (modoEscuroAtivo) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao aplicar tema FlatLaf: " + e.getMessage());
        }
        cbModoEscuro.setSelected(modoEscuroAtivo);
    }

    private void salvarConfiguracao() {
        try {
            Properties props = new Properties();
            props.setProperty("modoEscuro", String.valueOf(modoEscuroAtivo));
            try (FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + "/config.properties")) {
                props.store(out, "Configurações do CryptoDashboard");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar configuração: " + e.getMessage());
        }
    }

    private void carregarConfiguracao() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/config.properties")) {
            props.load(in);
            modoEscuroAtivo = Boolean.parseBoolean(props.getProperty("modoEscuro", "false"));
            if (cbModoEscuro != null) {
                cbModoEscuro.setSelected(modoEscuroAtivo);
            }
        } catch (IOException e) {
            System.out.println("Arquivo de configuração não encontrado. Usando tema claro.");
            modoEscuroAtivo = false;
            if (cbModoEscuro != null) {
                cbModoEscuro.setSelected(false);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Erro ao aplicar FlatLightLaf no início: " + e.getMessage());
            }
            new CryptoDashboard().setVisible(true);
        });
    }
}
