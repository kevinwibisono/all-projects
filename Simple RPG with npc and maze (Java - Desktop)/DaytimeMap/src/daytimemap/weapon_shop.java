/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daytimemap;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Background;
import javax.swing.event.ListDataListener;
/**
 *
 * @author User
 */
public class weapon_shop extends javax.swing.JPanel {

    /**
     * Creates new form weapon_shop
     */
    tartarusG t1 = new tartarusG();
    JLabel background = new JLabel();
    JList area = new JList();
    JLabel title = new JLabel();
    ArrayList<String> namaitem = new ArrayList<String>();
    ArrayList<Integer> tieritem = new ArrayList<Integer>();
    ArrayList<String> typeitem = new ArrayList<String>();
    ArrayList<Integer> hargaitem = new ArrayList<Integer>();
    Font titlef = new Font("Times New Roman", 50, 35);
    DefaultListModel<String> lm = new DefaultListModel<String>();
    public weapon_shop() {
        initComponents();
        namaitem.add("Wooden sword");
        namaitem.add("Fleuret");
        namaitem.add("Practice bow");
        namaitem.add("Shinai");
        namaitem.add("Hockey stick");
        namaitem.add("Brass knuckle");
        namaitem.add("Lex");
        namaitem.add("Grimoire");
        typeitem.add("1h sword");
        typeitem.add("Rapier");
        typeitem.add("Bow");
        typeitem.add("Katana");
        typeitem.add("Naginata");
        typeitem.add("Knuckle");
        typeitem.add("2h sword");
        typeitem.add("Book");
        for (int i = 0; i < 8; i++) {
            hargaitem.add(300);
            tieritem.add(1);
            lm.addElement(namaitem.get(i)+"     "+hargaitem.get(i)+"$");
        }
        
        this.setSize(1000, 500);
        ImageIcon rawImg = new ImageIcon("image/weaponshop.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(newImg));
        this.add(background);
        background.setSize(1000, 500);
        
        title.setText("Weapon Shop");
        title.setForeground(Color.yellow);
        title.setFont(titlef);
        title.setBounds(450, 0, 200, 200);
        background.add(title);
        area.setModel(lm);
        background.add(area);
        area.setBounds(450, 400, 200, 100);
        
        area.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                int dialogbutton = 0;
                if(lm.size()>0){
                    dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to buy the selected items?", "Warning", 0);
                    if(dialogbutton == 0){
                        if(t1.p1.money<hargaitem.get(area.getSelectedIndex())){
                            JOptionPane.showMessageDialog(null, "Uang anda tidak mencukupi");
                        }
                        else if(dialogbutton == 1){
                            t1.p1.money-=hargaitem.get(area.getSelectedIndex());
                            t1.p1.bag.add(namaitem.get(area.getSelectedIndex()));
                            lm.removeElementAt(area.getSelectedIndex());
                            namaitem.remove(area.getSelectedIndex());
                            typeitem.remove(area.getSelectedIndex());
                            tieritem.remove(area.getSelectedIndex());
                            hargaitem.remove(area.getSelectedIndex());
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "Tidak ada item untuk dipilih");
                }
            }
        });
    }

    public void addItem(){
        if(t1.p1.lv>10){
            namaitem.add("Void Sword");
            namaitem.add("Cronus");
            namaitem.add("Cernos");
            namaitem.add("Prisma");
            namaitem.add("Glaive");
            namaitem.add("Repulser");
            namaitem.add("Caliburn");
            namaitem.add("Solomon");
            typeitem.add("1h sword");
            typeitem.add("Rapier");
            typeitem.add("Bow");
            typeitem.add("Katana");
            typeitem.add("Naginata");
            typeitem.add("Knuckle");
            typeitem.add("2h sword");
            typeitem.add("Book");
            for (int i = 16; i < 24; i++) {
                hargaitem.add(1000);
                tieritem.add(3);
                lm.addElement(namaitem.get(i)+"     "+hargaitem.get(i)+"$");
            }
        }
        else if(t1.p1.lv>5){
            namaitem.add("Ether");
            namaitem.add("Venka");
            namaitem.add("Boltor");
            namaitem.add("Nikana");
            namaitem.add("Hikou");
            namaitem.add("Tetra");
            namaitem.add("Galatine");
            namaitem.add("Picatrix");
            typeitem.add("1h sword");
            typeitem.add("Rapier");
            typeitem.add("Bow");
            typeitem.add("Katana");
            typeitem.add("Naginata");
            typeitem.add("Knuckle");
            typeitem.add("2h sword");
            typeitem.add("Book");
            for (int i = 8; i < 16; i++) {
                hargaitem.add(500);
                tieritem.add(2);
                lm.addElement(namaitem.get(i)+"     "+hargaitem.get(i)+"$");
            }
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

        jButton1 = new javax.swing.JButton();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jButton1.setText("Exit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(339, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(266, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_formMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        frameone f = (frameone)this.getParent().getParent().getParent().getParent();
        f.toshop1();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}


