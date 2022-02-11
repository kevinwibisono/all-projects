/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daytimemap;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.util.*;

/**
 *
 * @author User
 */
public class tartarusGround extends javax.swing.JPanel {

    /**
     * Creates new form tartarusGround
     */
    ArrayList<npc> npcs = new ArrayList<npc>();
    JLabel background = new JLabel();
    Boolean shown = true;
    JTextArea area = new JTextArea();
    public tartarusGround() {
        initComponents();
        this.setSize(1000, 500);
        ImageIcon rawImg = new ImageIcon("image/ground-floor.jpg");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(newImg));
        this.add(background);
        background.setSize(1000, 500);
        int[] no_npc = new int[7];
        int[] rand = new int[7];
        for (int i = 0; i < 7; i++) {
            Boolean kembar;
            no_npc[i] = (int)(Math.random()*7);
            if(i>=1){
                do{
                    kembar = false;
                    for (int j = 0; j < i; j++) {
                        if(no_npc[i]==no_npc[j]){
                           kembar = true;
                        }
                    }
                    if(kembar){
                        no_npc[i] = (int)(Math.random()*6);
                    }
                }while(kembar);
            }
            if(no_npc[i]==0){
                noel h = new noel();
                h.x = (int)(Math.random()*12+4)*50;
                h.y = (int)(Math.random()*4+4)*50;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            else if(no_npc[i]==1){
                shiki h = new shiki();
                h.x = (int)(Math.random()*12+4)*50;
                h.y = (int)(Math.random()*4+4)*50;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            else if(no_npc[i]==2){
                hime h = new hime();
                h.x = (int)(Math.random()*12+4)*50;
                h.y = (int)(Math.random()*4+4)*50;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            else if(no_npc[i]==3){
                inori h = new inori();
                h.x = (int)(Math.random()*12+4)*50;
                h.y = (int)(Math.random()*4+4)*50;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            else if(no_npc[i]==4){
                sakura h = new sakura();
                h.x = 450;
                h.y = 100;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            else if(no_npc[i]==5){
                sou h = new sou();
                h.x = (int)(Math.random()*12+4)*50;
                h.y = (int)(Math.random()*4+4)*50;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            else if(no_npc[i]==6){
                akio h = new akio();
                h.x = (int)(Math.random()*12+4)*50;
                h.y = (int)(Math.random()*4+4)*50;
                if(npcs.size()>=0){
                    for (int j = 0; j < npcs.size(); j++) {
                        while(h.x == npcs.get(j).x && h.y == npcs.get(j).y){
                            h.x = (int)(Math.random()*12+4)*50;
                            h.y = (int)(Math.random()*4+4)*50;
                        }
                    }
                }
                if(h.x>500){
                    h.labelunit = new JLabel(h.gambar[0]);
                }
                else{
                    h.labelunit = new JLabel(h.gambar[1]);
                }
                rand[i] = (int)(Math.random()*4);
                npcs.add(h);
            }
            background.add(npcs.get(i).labelunit);
            npcs.get(i).labelunit.setBounds(npcs.get(i).x, npcs.get(i).y, 75,75);
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 419, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(null, "X: "+evt.getX()+"\nY: "+evt.getY());
        if(shown){
            for (int i =30; i <= 100; i++) {
                for (int j = 650; j <= 730; j++) {
                    if(evt.getY()==i && evt.getX()==j){
                        frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                        f.totar();
                    }
                }
            }
            
            for (int i =190; i <= 235; i++) {
                for (int j = 365; j <= 410; j++) {
                    if(evt.getY()==i && evt.getX()==j){
                        jFileChooser1.showSaveDialog(null);
                        String nm = jFileChooser1.getSelectedFile().toString();
                        try{
                            PrintWriter fo = new PrintWriter(new FileOutputStream(nm, false));
                        }
                        catch(IOException e){
                            System.out.println(e);
                        }
                    }
                }
            }
        }
        else{
            shown = true;
            ImageIcon rawImg = new ImageIcon("image/ground-floor.jpg");
            Image img = rawImg.getImage();
            Image newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
            background.setIcon(new ImageIcon(newImg));
            for (int i = 0; i < npcs.size(); i++) {
                npcs.get(i).labelunit.setVisible(true);
            }
        }
    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    // End of variables declaration//GEN-END:variables
}

class saveddata implements Serializable{
    player a;
    int floor, mapnum;
    
}


