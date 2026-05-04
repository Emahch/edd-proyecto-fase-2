/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.josecarlos.supermarket.view.listeners;

import com.josecarlos.supermarket.model.graphs.ComparationMode;
import com.josecarlos.supermarket.model.graphs.Vertex;

/**
 *
 * @author LENOVO
 */
public interface SelectDestinationListener {
    void onVertexSelected(Vertex agency, ComparationMode mode);
}
