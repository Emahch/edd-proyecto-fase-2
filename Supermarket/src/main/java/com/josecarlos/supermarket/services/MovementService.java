package com.josecarlos.supermarket.services;

import com.josecarlos.supermarket.model.graphs.PathResult;
import com.josecarlos.supermarket.model.graphs.Vertex;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.view.agencies.ProcessViewDialog;
import java.awt.Color;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author LENOVO
 */
public class MovementService {

    public static final int SPEED = 1000;

    private PathResult path;
    private Vertex destination;

    public MovementService(PathResult path, Vertex destination) {
        this.path = path;
        this.destination = destination;
    }

    public void run() {
        List<Agency> agencies = path.getPath();
        ProcessViewDialog dialog = new ProcessViewDialog(new JFrame(), agencies);
        dialog.setVisible(true);

        new Thread(() -> {
            for (int i = 0; i < agencies.size(); i++) {
                Agency current = agencies.get(i);
                try {
                    if (current.compareTo(destination.getAgency()) == 0) {
                        dialog.updateStep(i, "Entrando", new Color(0, 150, 0));
                        Thread.sleep(current.getEnterTime().longValue() * SPEED);
                    } else {
                        dialog.updateStep(i, "Pasando", Color.BLUE);
                        Thread.sleep(current.getPrepareTime().longValue() * SPEED);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            dialog.markAsFinished();
        }).start();
    }

}
