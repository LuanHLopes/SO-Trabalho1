package Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConsumerGUI {
    private Consumer consumer;
    private JTextArea outputArea;
    private JTextField productionAmountField;
    private JTextField productionIntervalField;
    private Timer updateTimer;
    private boolean isConsuming = false;

    public ConsumerGUI() {
        JFrame frame = new JFrame("Consumer Message Passing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel("Consumer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Painel de configurações em uma linha
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new FlowLayout());

        // Configurar Production Amount
        configPanel.add(new JLabel("Items per time:"));
        productionAmountField = new JTextField("1", 5);
        configPanel.add(productionAmountField);

        // Configurar Production Interval
        configPanel.add(new JLabel("Time (ms):"));
        productionIntervalField = new JTextField("1000", 5);
        configPanel.add(productionIntervalField);

        mainPanel.add(configPanel, BorderLayout.CENTER);

        // Painel de saída para as mensagens consumidas
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Consumed Messages"));
        outputArea = new JTextArea(13, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões abaixo da área de saída
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Botão para iniciar o consumidor
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(80, 30));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startConsumer();
            }
        });

        // Botão para parar o consumidor
        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(80, 30));
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopConsumer();
            }
        });

        // Botão para limpar a saída
        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(80, 30));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputArea.setText("");
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(clearButton);

        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    public void appendOutputText(String text) {
        SwingUtilities.invokeLater(() -> outputArea.append(text + "\n"));
    }

    private void startConsumer() {
        if (isConsuming) return; // Evita múltiplas execuções do consumidor
        try {
            int productionAmount = Integer.parseInt(productionAmountField.getText());
            int productionInterval = Integer.parseInt(productionIntervalField.getText());

            consumer = new Consumer(this);
            consumer.setConsumerSpeed(productionAmount, productionInterval);

            consumer.start();
            isConsuming = true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numbers in all fields.");
        }
    }

    private void stopConsumer() {
        if (isConsuming && consumer != null) {
            consumer.interrupt(); // Interrompe a thread do consumidor
            isConsuming = false;
            if (updateTimer != null) {
                updateTimer.stop(); // Para o timer de atualização
            }
            JOptionPane.showMessageDialog(null, "Consumer stopped.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConsumerGUI::new);
    }
}