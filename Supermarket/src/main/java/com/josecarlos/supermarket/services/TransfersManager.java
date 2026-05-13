package com.josecarlos.supermarket.services;

import com.josecarlos.supermarket.view.products.TransferProcessViewDialog;
import com.josecarlos.supermarket.model.graphs.PathResult;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.model.product.Product;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author LENOVO
 */
public class TransfersManager {

    private List<TransferProcess> activeTransfers;
    private static TransfersManager instance;

    private TransfersManager() {
        this.activeTransfers = new ArrayList<>();
    }

    public static TransfersManager getInstance() {
        if (instance == null) {
            instance = new TransfersManager();
        }
        return instance;
    }

    public void startTransfer(JFrame parentFrame, Product product, Agency origin,
            Agency destination, PathResult path) {
        TransferProcessViewDialog dialog = new TransferProcessViewDialog(
                parentFrame,
                product,
                origin,
                destination,
                path);
        dialog.setVisible(true);

        activeTransfers.add(dialog.getTransferProcess());
    }

    public List<TransferProcess> getActiveTransfers() {
        activeTransfers.removeIf(transfer -> !transfer.isRunning());
        return activeTransfers;
    }

    public int getActiveTransferCount() {
        return getActiveTransfers().size();
    }

    public void cancelAllTransfers() {
        for (TransferProcess transfer : activeTransfers) {
            if (transfer.isRunning()) {
                transfer.stopTransfer();
            }
        }
        activeTransfers.clear();
    }
}
