package com.josecarlos.supermarket.services;

import com.josecarlos.supermarket.model.graphs.PathResult;
import com.josecarlos.supermarket.model.graphs.TransferProduct;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.model.product.Product;
import java.util.List;

public class TransferProcess {

    private TransferProduct transfer;
    private int currentAgencyIndex;
    private boolean isRunning;
    private TransferListener listener;

    public interface TransferListener {

        void onStatusUpdate(String message);

        void onStepChanged(int step, String agencyName, String action);

        void onTransferComplete();

        void onProgressUpdate(int percentage);

        void onQueueUpdate(int step, int queueSize);

        void onTimingUpdate(double totalTime, double currentTime);
    }

    private double[] agencyTimes;

    public TransferProcess(Product product, Agency origin, Agency destination, PathResult path) {
        this.transfer = new TransferProduct(product, origin, destination, path);
        this.currentAgencyIndex = 0;
        this.isRunning = false;
        this.agencyTimes = new double[path.getPath().size()];
        calculateAgencyTimes(path);
    }

    private void calculateAgencyTimes(PathResult path) {
        List<Agency> agencies = path.getPath();
        for (int i = 0; i < agencies.size(); i++) {
            Agency agency = agencies.get(i);
            if (i == 0) {
                agencyTimes[i] = agency.getPrepareTime() + agency.getDispatchInterval();
            } else if (i == agencies.size() - 1) {
                agencyTimes[i] = agency.getEnterTime();
            } else {
                agencyTimes[i] = agency.getEnterTime() + agency.getPrepareTime() + agency.getDispatchInterval();
            }
        }
    }

    public void startTransfer(TransferListener listener) {
        this.listener = listener;
        isRunning = true;

        new Thread(() -> {
            try {
                List<Agency> agencies = transfer.getPath().getPath();
                Agency originAgency = agencies.get(0);

                originAgency.getCatalog().onProductDeleted(transfer.getProduct());

                double cumulativeTime = 0;
                double totalTime = calculateTotalTime();

                for (int i = 0; i < agencies.size(); i++) {
                    if (!isRunning) {
                        break;
                    }

                    Agency currentAgency = agencies.get(i);
                    transfer.setCurrentStep(i);
                    currentAgencyIndex = i;

                    int queueSize = i > 0 ? Math.max(0, (int) (Math.random() * 3)) : 0;
                    if (listener != null) {
                        listener.onQueueUpdate(i, queueSize);
                    }

                    if (i == 0) {
                        processFirstAgency(currentAgency);
                    } else if (i == agencies.size() - 1) {
                        processDestinationAgency(currentAgency);
                    } else {
                        processIntermediateAgency(currentAgency);
                    }

                    cumulativeTime += agencyTimes[i];
                    if (listener != null) {
                        listener.onTimingUpdate(totalTime, cumulativeTime);
                    }
                }

                if (isRunning) {
                    Agency destinationAgency = agencies.get(agencies.size() - 1);
                    destinationAgency.getCatalog().onProductCreated(transfer.getProduct());

                    transfer.setStatus(TransferProduct.TransferStatus.ARRIVED);
                    if (listener != null) {
                        listener.onStepChanged(agencies.size() - 1,
                                destinationAgency.getName(),
                                "Entregado");
                        listener.onTransferComplete();
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                isRunning = false;
            }
        }).start();
    }

    private double calculateTotalTime() {
        double total = 0;
        for (double time : agencyTimes) {
            total += time;
        }
        return total;
    }

    private void processFirstAgency(Agency agency) throws InterruptedException {
        transfer.setStatus(TransferProduct.TransferStatus.IN_QUEUE);
        if (listener != null) {
            listener.onStepChanged(0, agency.getName(), "Preparando para envío...");
        }

        long prepareTime = agency.getPrepareTime().longValue() * 1000;
        simulateQueueProcessing(agency, prepareTime, "Preparando");

        long dispatchTime = agency.getDispatchInterval().longValue() * 1000;
        simulateQueueProcessing(agency, dispatchTime, "En cola de salida");

        transfer.setStatus(TransferProduct.TransferStatus.IN_TRANSIT);
        if (listener != null) {
            listener.onStepChanged(0, agency.getName(), "Enviado");
        }
    }

    private void processIntermediateAgency(Agency agency) throws InterruptedException {
        transfer.setStatus(TransferProduct.TransferStatus.IN_QUEUE);
        if (listener != null) {
            listener.onStepChanged(currentAgencyIndex, agency.getName(), "Recibido - Procesando entrada...");
        }

        long enterTime = agency.getEnterTime().longValue() * 1000;
        simulateQueueProcessing(agency, enterTime, "En cola de ingreso");

        long prepareTime = agency.getPrepareTime().longValue() * 1000;
        simulateQueueProcessing(agency, prepareTime, "Preparando para siguiente envío");

        long dispatchTime = agency.getDispatchInterval().longValue() * 1000;
        simulateQueueProcessing(agency, dispatchTime, "En cola de salida");

        transfer.setStatus(TransferProduct.TransferStatus.IN_TRANSIT);
        if (listener != null) {
            listener.onStepChanged(currentAgencyIndex, agency.getName(), "Enviado a siguiente sucursal");
        }
    }

    private void processDestinationAgency(Agency agency) throws InterruptedException {
        transfer.setStatus(TransferProduct.TransferStatus.PROCESSING);
        if (listener != null) {
            listener.onStepChanged(currentAgencyIndex, agency.getName(), "Recibido - Procesando entrada...");
        }

        long enterTime = agency.getEnterTime().longValue() * 1000;
        simulateQueueProcessing(agency, enterTime, "En cola de ingreso final");

        transfer.setStatus(TransferProduct.TransferStatus.ARRIVED);
        if (listener != null) {
            listener.onStepChanged(currentAgencyIndex, agency.getName(), "Entregado");
        }
    }

    private void simulateQueueProcessing(Agency agency, long totalTime, String action) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long updateInterval = 50;
        long scaledTime = (long) (totalTime * 0.3);

        while (System.currentTimeMillis() - startTime < scaledTime && isRunning) {
            int percentage = transfer.getProgressPercentage();
            if (listener != null) {
                listener.onProgressUpdate(percentage);
            }

            Thread.sleep(updateInterval);
        }
    }

    public void stopTransfer() {
        isRunning = false;
    }

    public TransferProduct getTransfer() {
        return transfer;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
