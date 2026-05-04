package com.josecarlos.supermarket.view.agencies;

import com.josecarlos.supermarket.model.product.Agency;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessViewDialog extends JDialog {

    private List<JLabel> agencyLabels = new ArrayList<>();
    private JPanel container;
    private JLabel statusLabel;

    public ProcessViewDialog(Frame owner, List<Agency> path) {
        super(owner, "Progreso de Envío", false);
        setLayout(new BorderLayout());

        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (Agency a : path) {
            JLabel label = new JLabel(" ○ " + a.getName());
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            agencyLabels.add(label);
            container.add(label);
            container.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        statusLabel = new JLabel("Iniciando trayecto...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(new JScrollPane(container), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(300, 400);
        setLocationRelativeTo(owner);
    }

    public void updateStep(int index, String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = agencyLabels.get(index);
            label.setText(" ● " + label.getText().substring(3) + " (" + status + ")");
            label.setForeground(color);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            statusLabel.setText("Estado: " + status + " en " + label.getText().split(" ")[1]);
        });
    }

    public void markAsFinished() {
        SwingUtilities.invokeLater(() -> statusLabel.setText("¡Proceso Finalizado!"));
    }
}
