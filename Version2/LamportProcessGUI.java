import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LamportProcessGUI {
    private JFrame frame;
    private JTextArea logArea;
    private Map<Integer, JLabel> processLabels;
    private Map<Integer, JLabel> timestampLabels;
    private Map<Integer, JLabel> queueLabels;
    private int totalProcesses;

    public LamportProcessGUI(int totalProcesses) {
        this.totalProcesses = totalProcesses;
        processLabels = new HashMap<>();
        timestampLabels = new HashMap<>();
        queueLabels = new HashMap<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Lamport Process Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(totalProcesses, 3));
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

        for (int i = 1; i <= totalProcesses; i++) {
            JLabel idLabel = new JLabel("Process " + i + ": Active");
            idLabel.setBorder(border);
            idLabel.setOpaque(true);
            idLabel.setBackground(Color.GREEN);
            processLabels.put(i, idLabel);

            JLabel timestampLabel = new JLabel("Timestamp: 0");
            timestampLabel.setBorder(border);
            timestampLabel.setOpaque(true);
            timestampLabel.setBackground(Color.LIGHT_GRAY);
            timestampLabels.put(i, timestampLabel);

            JLabel queueLabel = new JLabel("Queue: []");
            queueLabel.setBorder(border);
            queueLabel.setOpaque(true);
            queueLabel.setBackground(Color.LIGHT_GRAY);
            queueLabels.put(i, queueLabel);

            panel.add(idLabel);
            panel.add(timestampLabel);
            panel.add(queueLabel);
        }

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void updateProcessStatus(int id, String status) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = processLabels.get(id);
            if (label != null) {
                label.setText("Process " + id + ": " + status);
                if (status.equals("Failed")) {
                    label.setBackground(Color.RED);
                } else {
                    label.setBackground(Color.GREEN);
                }
            }
        });
    }

    public void updateTimestamp(int id, long timestamp) {
        SwingUtilities.invokeLater(() -> {
            JLabel timestampLabel = timestampLabels.get(id);
            if (timestampLabel != null) {
                timestampLabel.setText("Timestamp: " + timestamp);
                timestampLabel.setBackground(Color.CYAN);
            }
        });
        
    }

    public void updateQueue(int id, String queue) {
        SwingUtilities.invokeLater(() -> {
            JLabel queueLabel = queueLabels.get(id);
            if (queueLabel != null) {
                queueLabel.setText("Queue: " + queue);
                queueLabel.setBackground(Color.ORANGE);
            }
        });
    }

    public void logMessage(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }
}
