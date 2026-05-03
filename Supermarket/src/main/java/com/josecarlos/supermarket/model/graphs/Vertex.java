package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.hash.Almacenable;
import com.josecarlos.supermarket.model.lists.AgenciesList;
import com.josecarlos.supermarket.model.product.Agency;

/**
 *
 * @author LENOVO
 */
public class Vertex implements Almacenable<Vertex> {

    private Agency agency;
    private AgenciesList<Edge<Vertex>> destionations;

    public Vertex(Agency agency, AgenciesList destionations) {
        this.agency = agency;
        this.destionations = destionations;
    }

    public Vertex(Agency agency) {
        this.agency = agency;
        this.destionations = new AgenciesList<>();
    }

    @Override
    public int compareTo(Vertex o) {
        return agency.getKey().compareTo(o.getKey());
    }

    @Override
    public String getKey() {
        return agency.getId();
    }

    public boolean addDestination(Vertex destination, double time, double price) {
        return destionations.add(new Edge<>(destination, time, price));
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public AgenciesList<Edge<Vertex>> getDestionations() {
        return destionations;
    }

    public void setDestionations(AgenciesList<Edge<Vertex>> destionations) {
        this.destionations = destionations;
    }

}
