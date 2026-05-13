package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.product.Product;
import com.josecarlos.supermarket.model.product.Agency;

public class TransferProduct {

    private Product product;
    private Agency origin;
    private Agency destination;
    private PathResult path;
    private long startTime;
    private long estimatedArrivalTime;
    private int currentStep;
    private TransferStatus status;

    public enum TransferStatus {
        PENDING,
        IN_QUEUE,
        IN_TRANSIT,
        ARRIVED,
        PROCESSING
    }

    public TransferProduct(Product product, Agency origin, Agency destination, PathResult path) {
        this.product = product;
        this.origin = origin;
        this.destination = destination;
        this.path = path;
        this.status = TransferStatus.PENDING;
        this.currentStep = 0;
        this.startTime = System.currentTimeMillis();
        this.estimatedArrivalTime = calculateETA();
    }

    private long calculateETA() {
        long totalTime = 0;
        java.util.List<Agency> agencies = path.getPath();

        for (int i = 0; i < agencies.size(); i++) {
            Agency agency = agencies.get(i);

            if (i == agencies.size() - 1) {
                totalTime += agency.getEnterTime() * 1000;
            } else {
                totalTime += agency.getPrepareTime() * 1000;
                totalTime += agency.getDispatchInterval() * 1000;
            }
        }
        totalTime += (long) (path.getTotalCost() * 1000);

        return startTime + totalTime;
    }

    public long getRemainingTime() {
        return Math.max(0, (estimatedArrivalTime - System.currentTimeMillis()) / 1000);
    }

    public int getProgressPercentage() {
        long elapsed = System.currentTimeMillis() - startTime;
        long total = estimatedArrivalTime - startTime;
        if (total == 0)
            return 0;
        return (int) Math.min(100, (elapsed * 100) / total);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Agency getOrigin() {
        return origin;
    }

    public void setOrigin(Agency origin) {
        this.origin = origin;
    }

    public Agency getDestination() {
        return destination;
    }

    public void setDestination(Agency destination) {
        this.destination = destination;
    }

    public PathResult getPath() {
        return path;
    }

    public void setPath(PathResult path) {
        this.path = path;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(long estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s: %s → %s (ETA: %ds)",
                product.getName(),
                origin.getName(),
                destination.getName(),
                getRemainingTime());
    }
}
