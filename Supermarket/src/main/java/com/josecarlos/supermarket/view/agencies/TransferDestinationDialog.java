package com.josecarlos.supermarket.view.agencies;

import com.josecarlos.supermarket.model.graphs.ComparationMode;
import com.josecarlos.supermarket.model.graphs.Dijkstra;
import com.josecarlos.supermarket.model.graphs.Graph;
import com.josecarlos.supermarket.model.graphs.PathResult;
import com.josecarlos.supermarket.model.graphs.Vertex;
import com.josecarlos.supermarket.model.lists.DoubleNode;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.model.product.Product;
import com.josecarlos.supermarket.services.TransfersManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Optional;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TransferDestinationDialog extends JDialog {

    private Product product;
    private Vertex originVertex;
    private Graph graph;
    private Vertex selectedDestination;
    private ComparationMode selectedMode;

    public TransferDestinationDialog(JFrame parent, Product product, Vertex originVertex, Graph graph) {
        super(parent, "Seleccionar Destino del Traslado", false);
        this.product = product;
        this.originVertex = originVertex;
        this.graph = graph;

        initComponents();
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel productLabel = new JLabel("Producto: " + product.getName());
        JLabel originLabel = new JLabel("Origen: " + originVertex.getAgency().getName());
        productLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        originLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(productLabel);
        infoPanel.add(originLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel destinationsPanel = new JPanel();
        destinationsPanel.setLayout(new BoxLayout(destinationsPanel, BoxLayout.Y_AXIS));
        JLabel destinationsLabel = new JLabel("Selecciona una sucursal destino:");
        destinationsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        destinationsPanel.add(destinationsLabel);
        destinationsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        DoubleNode<Vertex> current = graph.getAdjacencyList().getHead();
        while (current != null) {
            Vertex vertex = current.getValue();
            if (!vertex.getKey().equals(originVertex.getKey())) {
                JButton destButton = createDestinationButton(vertex);
                destinationsPanel.add(destButton);
                destinationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            current = current.getNext();
        }

        JScrollPane scrollPane = new JScrollPane(destinationsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
        modePanel.add(new JLabel("Criterio de traslado: "));

        JButton timeButton = new JButton("Menor tiempo");
        timeButton.addActionListener(e -> selectedMode = ComparationMode.TIME);

        JButton costButton = new JButton("Menor costo");
        costButton.addActionListener(e -> selectedMode = ComparationMode.PRICE);

        modePanel.add(timeButton);
        modePanel.add(Box.createHorizontalStrut(5));
        modePanel.add(costButton);

        mainPanel.add(modePanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createDestinationButton(Vertex vertex) {
        JButton button = new JButton(vertex.getAgency().getName());
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            selectedDestination = vertex;
            if (selectedMode == null) {
                selectedMode = ComparationMode.TIME; // Por defecto
            }
            initiateTransfer();
        });
        return button;
    }

    private void initiateTransfer() {
        // Calcular ruta usando Dijkstra
        Dijkstra dijkstra = new Dijkstra();
        Optional<PathResult> pathOpt = dijkstra.shortestPath(
                graph,
                originVertex.getKey(),
                selectedDestination.getKey(),
                selectedMode);

        if (pathOpt.isPresent()) {
            // Usar el gestor de transferencias para iniciar la transferencia
            TransfersManager.getInstance().startTransfer(
                    (JFrame) getParent(),
                    product,
                    originVertex.getAgency(),
                    selectedDestination.getAgency(),
                    pathOpt.get());
            setVisible(false);
        } else {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "No hay ruta disponible a la sucursal seleccionada",
                    "Sin ruta",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }
}
