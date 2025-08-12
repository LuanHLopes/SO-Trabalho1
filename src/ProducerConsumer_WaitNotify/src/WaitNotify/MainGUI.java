package WaitNotify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI {
    private JFrame frame;
    private JTextArea producerArea, consumerArea;
    private JTextField producerItemsField, producerSpeedField, producerTotalField;
    private JTextField consumerSpeedField, consumerTotalField;
    private JTextField bufferSizeField;
    private JProgressBar bufferProgress;
    private Buffer buffer;
    private Producer producer;
    private Consumer consumer;
    private Timer bufferTimer;

    public MainGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("ProducerConsumer Wait/Notify");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 720);
        frame.setLayout(new BorderLayout());

        // Painel do produtor
        JPanel producerPanel = new JPanel(new BorderLayout());
        producerPanel.setPreferredSize(new Dimension(frame.getWidth() * 2 / 5, frame.getHeight()));
        producerPanel.setBorder(BorderFactory.createTitledBorder("Producer"));
        producerArea = new JTextArea();
        producerArea.setEditable(false);
        producerPanel.add(new JScrollPane(producerArea), BorderLayout.CENTER);

        JPanel producerSettings = new JPanel(new GridLayout(3, 2));
        producerItemsField = new JTextField("1");
        producerSpeedField = new JTextField("1000");
        producerTotalField = new JTextField("20");

        producerSettings.add(new JLabel("Items per time:"));
        producerSettings.add(producerItemsField);
        producerSettings.add(new JLabel("Produce time (ms):"));
        producerSettings.add(producerSpeedField);
        producerSettings.add(new JLabel("Total itens:"));
        producerSettings.add(producerTotalField);
        producerPanel.add(producerSettings, BorderLayout.NORTH);

        // Painel do buffer
        JPanel bufferPanel = new JPanel(new BorderLayout());
        bufferPanel.setPreferredSize(new Dimension(frame.getWidth() / 5, frame.getHeight() * 2 / 3));
        bufferPanel.setBorder(BorderFactory.createTitledBorder("Buffer"));

        JPanel bufferSettings = new JPanel(new GridLayout(3, 2));
        bufferSizeField = new JTextField("5");
        bufferSettings.add(new JLabel("Buffer size:"));
        bufferSettings.add(bufferSizeField);
        bufferPanel.add(bufferSettings, BorderLayout.NORTH);

        bufferProgress = new JProgressBar(SwingConstants.VERTICAL);
        bufferProgress.setMinimum(0);
        bufferProgress.setMaximum(100);
        bufferProgress.setForeground(Color.GREEN);
        bufferPanel.add(bufferProgress, BorderLayout.CENTER);

        // Painel do consumidor
        JPanel consumerPanel = new JPanel(new BorderLayout());
        consumerPanel.setPreferredSize(new Dimension(frame.getWidth() * 2 / 5, frame.getHeight()));
        consumerPanel.setBorder(BorderFactory.createTitledBorder("Consumer"));
        consumerArea = new JTextArea();
        consumerArea.setEditable(false);
        consumerPanel.add(new JScrollPane(consumerArea), BorderLayout.CENTER);

        JPanel consumerSettings = new JPanel(new GridLayout(3, 2));
        consumerTotalField = new JTextField("1");
        consumerSettings.add(new JLabel("Items per time:"));
        consumerSettings.add(consumerTotalField);
        consumerSpeedField = new JTextField("1000");
        consumerSettings.add(new JLabel("Consume time (ms):"));
        consumerSettings.add(consumerSpeedField);
        consumerSettings.add(new JLabel("")); // Empty JLabel for spacing
        consumerPanel.add(consumerSettings, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JButton clearButton = new JButton("Clean");

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);

        startButton.addActionListener(new StartButtonListener());
        stopButton.addActionListener(new StopButtonListener());

        clearButton.addActionListener(e -> {
            producerArea.setText("");
            consumerArea.setText("");
        });

        frame.add(producerPanel, BorderLayout.WEST);
        frame.add(bufferPanel, BorderLayout.CENTER);
        frame.add(consumerPanel, BorderLayout.EAST);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void appendProducerText(String text) {
        SwingUtilities.invokeLater(() -> producerArea.append(text + "\n"));
    }

    public void appendConsumerText(String text) {
        SwingUtilities.invokeLater(() -> consumerArea.append(text + "\n"));
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (producer == null && consumer == null) {
            	int bufferSize = Integer.parseInt(bufferSizeField.getText());
            	
            	buffer = new Buffer(bufferSize, MainGUI.this);
                   
                producer = new Producer(buffer);
                consumer = new Consumer(buffer);

                int producerItems = Integer.parseInt(producerItemsField.getText());
                int producerSpeed = Integer.parseInt(producerSpeedField.getText());
                int producerTotal = Integer.parseInt(producerTotalField.getText());

                int consumerSpeed = Integer.parseInt(consumerSpeedField.getText());
                int consumerTotal = Integer.parseInt(consumerTotalField.getText());

                producer.setItensProduction(producerTotal);
                producer.setProducerSpeed(producerItems, producerSpeed);
                consumer.setConsumerSpeed(consumerTotal, consumerSpeed);

                bufferTimer = new Timer(500, e1 -> {
                	bufferProgress.setValue((int) buffer.getBufferUsage());
                	bufferProgress.setForeground(buffer.getBufferUsage() == 100 ? Color.RED : Color.GREEN);
                });
                   
                bufferTimer.start();
                producer.start();
                consumer.start();
            }
         
        }
    }

    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (bufferTimer != null) bufferTimer.stop();
            producer.interrupt();
            consumer.interrupt();
            producer = null;
            consumer = null;
            buffer = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}