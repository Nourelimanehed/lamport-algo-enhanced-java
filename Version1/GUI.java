import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GUI extends JFrame {
    private JTable log;
    private JTable queue;
    private JTable procs;
    private DefaultTableModel logModel;
    private DefaultTableModel queueModel;
    private DefaultTableModel procsModel;

    public GUI() {
        setTitle("Algorithme de Lamport 78");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);
        
        // Panel for the top two tables with GridLayout
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.add(topPanel, BorderLayout.CENTER);
        
        // Panel for the bottom table
        JPanel bottomPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Configuration du tableau des logs
        String[] logColumns = { "Source", "Destination", "Evènement" };
        logModel = new DefaultTableModel(logColumns, 0);
        log = new JTable(logModel);
        customizeTable(log, Color.decode("#FFD700")); // Gold color for log table
        log.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Configuration du tableau des files d'attente
        String[] queueColumns = { "File d'attente" };
        queueModel = new DefaultTableModel(queueColumns, 0);
        queue = new JTable(queueModel);
        customizeTable(queue, Color.decode("#00BFFF")); // DeepSkyBlue color for queue table
        queue.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Configuration du tableau des processus
        String[] procsColumns = { "Processus", "Estampille", "Etat" };
        procsModel = new DefaultTableModel(procsColumns, 0);
        procs = new JTable(procsModel);
        customizeTable(procs, Color.decode("#7CFC00")); // LawnGreen color for procs table
        procs.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Adding tables to the corresponding panels with scroll panes
        topPanel.add(new JScrollPane(log));
        topPanel.add(new JScrollPane(queue));
        bottomPanel.add(new JScrollPane(procs), BorderLayout.CENTER);
        
        // Scroll automatique vers le bas du log
        JScrollBar verticalScrollBar = ((JScrollPane) log.getParent().getParent()).getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        log.getModel().addTableModelListener(e -> {
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });

        setVisible(true);
    }

    // Méthode pour personnaliser les tables
    private void customizeTable(JTable table, Color headerColor) {
        table.getTableHeader().setBackground(headerColor);
        Font headerFont = table.getTableHeader().getFont();
        table.getTableHeader().setFont(headerFont.deriveFont(Font.BOLD, 18f)); // Larger font for header
        table.setRowHeight(40); // Larger row height

        Font cellFont = table.getFont();
        table.setFont(cellFont.deriveFont(16f)); // Larger font for cell
    }

    // Méthode pour ajouter un processus avec une file d'attente vide par défaut
    public void addProc(String proc, String Estampille, String Etat) {
        this.procsModel.addRow(new Object[] { proc, Estampille, Etat });
        this.queueModel.addRow(new Object[] { "" }); // Ajouter une ligne de file d'attente pour chaque processus
    }

    // Méthode pour éditer l'état d'un processus
    public synchronized void editState(int i, String Etat) {
        this.procsModel.setValueAt(Etat, i - 1, 2);
    }

    // Méthode pour éditer l'estampille d'un processus
    public void editH(int i, String Estampille) {
        this.procsModel.setValueAt(Estampille, i - 1, 1);
    }

    // Méthode pour ajouter un événement dans le log
    public synchronized void addEvent(String src, String dest, String event) {
        this.logModel.addRow(new Object[] { src, dest, event });
    }

    // Méthode pour éditer la file d'attente d'un processus
    public synchronized void editQueue(int i, String queue) {
        this.queueModel.setValueAt(queue, i - 1, 0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
