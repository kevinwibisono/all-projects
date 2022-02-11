/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daytimemap;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
/**
 *
 * @author User
 */
public class frameone extends javax.swing.JFrame {

    /**
     * Creates new form frameone
     */
    weapon_shop we1 = new weapon_shop();
    shop1 s1 = new shop1();
    shop2 s2 = new shop2();
    worldmap w1 = new worldmap();
    frontschool f1 = new frontschool();
    hallway h1 = new hallway();
    classroom c1 = new classroom();
    dorm d1 = new dorm();
    pharmacy p1 = new pharmacy();
    tartarusGround tg = new tartarusGround();
    tartarusG t1 = new tartarusG();
    startpanel start =  new startpanel();
    Boolean throughconv = false, throughfloorup = false;
    int jumfloor = 1, rand_map = 0, rand_before = -1;
    public frameone() {
        initComponents();
        this.setSize(1000, 500);
        this.setResizable(false);
        this.add(p1);
        this.add(we1);
        this.add(s1);
        this.add(s2);
        this.add(w1);
        this.add(f1);
        this.add(h1);
        this.add(c1);
        this.add(d1);
        this.add(tg);
        this.add(t1);
        this.add(start);
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        h1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
        //t1.setVisible(false);
        tofront();
    }
    
    public void tobattle(){
        JOptionPane.showMessageDialog(null, "Entering the battle...");
    }

    public void tohall(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(true);
        start.setVisible(false);
    }
    
    public void tofront(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(true);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void toclass(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(true);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void todorm(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(true);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void toworld(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(true);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void toshop1(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(true);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void toshop2(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(true);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void toweshop(){
        p1.setVisible(false);
        we1.setVisible(true);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void tophar(){
        p1.setVisible(true);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
    }
    
    public void tobossbattle(){
        
    }
    
    public void totar(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        tg.setVisible(false);
        start.setVisible(false);
        rand_map = (int)(Math.random()*5);
        while(rand_map == rand_before){
            rand_map = (int)(Math.random()*5);
        }
        t1.mapnum = rand_map;
        t1.background.setIcon(t1.tarmap[rand_map][0]);
        t1.partnum = 0;
        if(rand_map==0){
            t1.p1.lblplayer.setBounds(900, 400, 50, 50);
            t1.mapnum = 0;
        }
        else if(rand_map==1){
            t1.p1.lblplayer.setBounds(900, 280, 50, 50);
            t1.mapnum = 1;
        }
        else if(rand_map==2){
            t1.p1.lblplayer.setBounds(600, 430, 50, 50);
            t1.mapnum = 2;
        }
        else if(rand_map==3){
            t1.p1.lblplayer.setBounds(50, 155, 50, 50);
            t1.p1.mapnum = 3;
        }
        else if(rand_map==4){
            t1.p1.lblplayer.setBounds(870, 170, 50, 50);
            t1.p1.mapnum = 4;
        }
        t1.setVisible(true);
        /*if(throughfloorup){
            jumfloor+=1;
            throughfloorup = false;
        }*/
        t1.title.setText("Floor "+t1.floor);
        t1.t.start();
        rand_before = rand_map;
    }
    
    public void totarg(){
        p1.setVisible(false);
        we1.setVisible(false);
        s1.setVisible(false);
        s2.setVisible(false);
        w1.setVisible(false);
        f1.setVisible(false);
        c1.setVisible(false);
        d1.setVisible(false);
        h1.setVisible(false);
        if(throughconv){
            ImageIcon rawImg = new ImageIcon("image/nighttime.png");
            Image img = rawImg.getImage();
            Image newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
            tg.background.setIcon(new ImageIcon(newImg));
            for (int i = 0; i < tg.npcs.size(); i++) {
                tg.npcs.get(i).labelunit.setVisible(false);
            }
            tg.shown = false;
        }
        tg.setVisible(true);
        start.setVisible(false);
        t1.t.stop();
        t1.setVisible(false);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        char temp = evt.getKeyChar();
        if(temp=='e' && !t1.isVisible()){
            this.toworld();
        }
        else if(temp=='w' && t1.isVisible()){
            System.out.println("w");
            t1.toup();
        }
        else if(temp=='a' && t1.isVisible()){
            t1.toleft();
        }
        else if(temp=='s' && t1.isVisible()){
            t1.todown();
        }
        else if(temp=='d' && t1.isVisible()){
            t1.toright();
        }
    }//GEN-LAST:event_formKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frameone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frameone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frameone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frameone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frameone().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}


