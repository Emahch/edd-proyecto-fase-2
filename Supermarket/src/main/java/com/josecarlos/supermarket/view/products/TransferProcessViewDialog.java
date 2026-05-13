package com.josecarlos.supermarket.view.products;

import com.josecarlos.supermarket.model.graphs.PathResult;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.model.product.Product;
import com.josecarlos.supermarket.services.TransferProcess;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Diálogo que visualiza el proceso de transferencia de un producto en tiempo
 * real
 */
public class TransferProcessViewDialog extends JDialog implements TransferProcess.TransferListener {

    private TransferProcess transferProcess;
    private JPanel stepsPanel;
    private JProgressBar overallProgress;
    private JLabel statusLabel;
    private JLabel etaLabel;
    private JLabel totalTimeLabel;
    private JLabel totalCostLabel;
    private JLabel timingLabel;
    private JButton cancelButton;
    private boolean isCompleted = false;
    private PathResult pathResult;

    public TransferProcessViewDialog(JFrame parent, Product product, Agency origin, Agency destination,
            PathResult path) {
        super(parent, "Proceso de Transferencia de Producto", false);
        this.transferProcess = new TransferProcess(product, origin, destination, path);
        this.pathResult = path;

        initComponents(product, origin, destination, path);
        setSize(800, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        transferProcess.startTransfer(this);
    }

    private void initComponents(Product product, Agency origin, Agency destination, PathResult path) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Transferencia: " + product.getName());
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel routeLabel = new JLabel(origin.getName() + " → " + destination.getName());
        routeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(routeLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        statusLabel = new JLabel("Estado: Iniciando transferencia...");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(statusLabel);

        etaLabel = new JLabel("ETA: Calculando...");
        etaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(etaLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        totalTimeLabel = new JLabel("Tiempo Total: Calculando...");
        totalTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(totalTimeLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        totalCostLabel = new JLabel("Costo Total: " + String.format("%.2f", pathResult.getTotalCost()));
        totalCostLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(totalCostLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        timingLabel = new JLabel("Tiempo Acumulado: 0s");
        timingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(timingLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel de progreso general
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Progreso General"));

        overallProgress = new JProgressBar(0, 100);
        overallProgress.setStringPainted(true);
        overallProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        progressPanel.add(overallProgress);

        mainPanel.add(progressPanel, BorderLayout.CENTER);

        // Panel central - Pasos
        JPanel centerPanel = new JPanel(new BorderLayout());
        stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));

        // Crear etapas para cada sucursal
        List<Agency> agencies = path.getPath();
        for (int i = 0; i < agencies.size(); i++) {
            stepsPanel.add(createStepPanel(i, agencies.get(i)));
            stepsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        stepsPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(stepsPanel);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel inferior - Botón cancelar
        JPanel buttonPanel = new JPanel();
        cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> {
            transferProcess.stopTransfer();
            dispose();
        });
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStepPanel(int index, Agency agency) {
        JPanel stepPanel = new JPanel();
        stepPanel.setLayout(new BoxLayout(stepPanel, BoxLayout.Y_AXIS));
        stepPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                (index + 1) + ". " + agency.getName()));
        stepPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        stepPanel.setOpaque(true);
        stepPanel.setBackground(new Color(240, 240, 240));
        stepPanel.setName("step_" + index);

        JLabel actionLabel = new JLabel("Esperando...");
        actionLabel.setName("action_" + index);
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(String.format(
                "Ingreso: %.1fs | Preparación: %.1fs | Intervalo: %.1fs",
                agency.getEnterTime(),
                agency.getPrepareTime(),
                agency.getDispatchInterval()));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setForeground(Color.GRAY);

        JLabel queueLabel = new JLabel("Productos en cola: 0");
        queueLabel.setName("queue_" + index);
        queueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        queueLabel.setForeground(new Color(200, 100, 0));

        stepPanel.add(actionLabel);
        stepPanel.add(infoLabel);
        stepPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        stepPanel.add(queueLabel);

        return stepPanel;
    }

    @Override
    public void onStatusUpdate(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Estado: " + message);
        });
    }

    @Override
    public void onStepChanged(int step, String agencyName, String action) {
        SwingUtilities.invokeLater(() -> {
            // Actualizar el label de acción para este paso
            JPanel stepPanel = (JPanel) stepsPanel.getComponent(step * 2); // Cada paso ocupa 2 espacios (panel +
                                                                           // rigidArea)
            for (Component comp : stepPanel.getComponents()) {
                if (comp.getName() != null && comp.getName().equals("action_" + step)) {
                    ((JLabel) comp).setText("→ " + action);
                    ((JLabel) comp).setForeground(Color.BLUE);
                    break;
                }
            }

            // Actualizar estado
            statusLabel.setText("Estado: Procesando en " + agencyName);
        });
    }

    @Override
    public void onTransferComplete() {
        SwingUtilities.invokeLater(() -> {
            isCompleted = true;
            statusLabel.setText("Estado: ¡Entregado!");
            statusLabel.setForeground(new Color(0, 150, 0));
            overallProgress.setValue(100);
            cancelButton.setText("Cerrar");
        });
    }

    @Override
    public void onProgressUpdate(int percentage) {
        SwingUtilities.invokeLater(() -> {
            overallProgress.setValue(percentage);

            // Actualizar ETA
            long remainingSeconds = transferProcess.getTransfer().getRemainingTime();
            long minutes = remainingSeconds / 60;
            long seconds = remainingSeconds % 60;

            Date eta = new Date(transferProcess.getTransfer().getEstimatedArrivalTime());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            etaLabel.setText(String.format("ETA: %s (%d:%02d restantes)",
                    sdf.format(eta), minutes, seconds));
        });
    }

    @Override
    public void onQueueUpdate(int step, int queueSize) {
        SwingUtilities.invokeLater(() -> {
            JPanel stepPanel = (JPanel) stepsPanel.getComponent(step * 2);
            for (Component comp : stepPanel.getComponents()) {
                if (comp.getName() != null && comp.getName().equals("queue_" + step)) {
                    ((JLabel) comp).setText("Productos en cola: " + queueSize);
                    break;
                }
            }
        });
    }

    @Override
    public void onTimingUpdate(double totalTime, double currentTime) {
        SwingUtilities.invokeLater(() -> {
            totalTimeLabel.setText(String.format("Tiempo Total: %.2fs", totalTime));
            timingLabel.setText(String.format("Tiempo Acumulado: %.2fs / %.2fs", currentTime, totalTime));
        });
    }

    /**
     * Obtiene el proceso de transferencia asociado a este diálogo
     */
    public TransferProcess getTransferProcess() {
        return transferProcess;
    }
}
