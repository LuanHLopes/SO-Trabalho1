package Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProducerGUI {
    private Producer producer;
    private JTextArea outputArea;
    private JTextField mailboxSizeField;
    private JTextField productionAmountField;
    private JTextField productionIntervalField;
    private Timer updateTimer;
    private boolean isProducing = false;

    public ProducerGUI() {
        JFrame frame = new JFrame("Producer Message Passing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel("Producer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Painel de configurações em uma linha
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new FlowLayout());

        // Configurar Mailbox Size
        configPanel.add(new JLabel("Mailbox Size:"));
        mailboxSizeField = new JTextField("10", 5);
        configPanel.add(mailboxSizeField);

        // Configurar Production Amount
        configPanel.add(new JLabel("Items per time:"));
        productionAmountField = new JTextField("1", 5);
        configPanel.add(productionAmountField);

        // Configurar Production Interval
        configPanel.add(new JLabel("Time (ms):"));
        productionIntervalField = new JTextField("1000", 5);
        configPanel.add(productionIntervalField);

        mainPanel.add(configPanel, BorderLayout.CENTER);

        // Painel de saída para as mensagens produzidas
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Produced Messages"));
        outputArea = new JTextArea(13, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões abaixo da área de saída
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Botão para iniciar o produtor
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(80, 30));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startProducer();
            }
        });

        // Botão para parar o produtor
        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(80, 30));
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopProducer();
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

    private void startProducer() {
        if (isProducing) return; // Evita múltiplas execuções do produtor
        try {
            int mailboxSize = Integer.parseInt(mailboxSizeField.getText());
            int productionAmount = Integer.parseInt(productionAmountField.getText());
            int productionInterval = Integer.parseInt(productionIntervalField.getText());

            producer = new Producer(mailboxSize, this);
            producer.setProducerSpeed(productionAmount, productionInterval);

            producer.start();
            isProducing = true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numbers in all fields.");
        }
    }

    private void stopProducer() {
        if (isProducing && producer != null) {
            producer.interrupt(); // Interrompe a thread do produtor
            isProducing = false;
            if (updateTimer != null) {
                updateTimer.stop(); // Para o timer de atualização
            }
            JOptionPane.showMessageDialog(null, "Producer stopped.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProducerGUI::new);
    }
}