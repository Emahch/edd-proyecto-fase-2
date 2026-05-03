package com.josecarlos.supermarket.view.agencies;

import com.josecarlos.supermarket.model.product.Agency;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;

/**
 *
 * @author LENOVO
 */
public class AgencyButton extends JButton{

    private final Agency agency;

    public AgencyButton(Agency agency) {
        super(agency.getName() + " -- Id: " + agency.getId());
        this.agency = agency;
        this.setForeground(Color.BLACK);
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(30, 30));
    }
    
    
}
