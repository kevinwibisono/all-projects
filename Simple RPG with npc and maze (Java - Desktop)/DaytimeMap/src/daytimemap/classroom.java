/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daytimemap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class classroom extends javax.swing.JPanel {

    /**
     * Creates new form classroom
     */
    String[] conv = new String[24];
    JLabel background = new JLabel();
    ArrayList<npc> npcs = new ArrayList<npc>();
    JTextArea area = new JTextArea();
    int ctr = -1, curctr;
    Timer t;
    JLabel tohall = new JLabel();
    JLabel tohall2 = new JLabel();
    Font classf = new Font("Times New Roman", 50, 20);
    public classroom() {
        initComponents();
        this.setSize(1000, 500);
        conv[0] = "Oh, Hayase! It's you!";
        conv[1] = "Listen, i heard the rumor about the ghost in the toilet";
        conv[2] = "Why don't we check it out?";
        conv[3] = "C'mon, it'll be fun";
        conv[4] = "Good morning, Hayase. How are you today?";
        conv[5] = "Hey, i heard there is a good restaurant that just opened around here";
        conv[6] = "It sells any kind of food in quite reachable price!";
        conv[7] = "Ugh...My stomach can't hold on anymore. C'mon, let's go over there";
        conv[8] = "Hello Hayase";
        conv[9] = "I have a hotnews! You will like it";
        conv[10] = "This morning when i arrives, Ms. Sakura told me that we're going on a vacation next week!";
        conv[11] = "So hurry hayase! Pack your things up, it's gonna be a fun vacation";
        conv[12] = "Hayase! Hayase!";
        conv[13] = "Please help me, i'm in a big trouble";
        conv[14] = "I get a bad score on my last test, and now my mom confiscate my phone";
        conv[15] = "Could you come to my house and persuade my mom? I'll treat you";
        conv[16] = "Hayase, can you help me?";
        conv[17] = "Ms Sakura give me an assignment today";
        conv[18] = "It's so hard, i can't do it by myself";
        conv[19] = "I hope you can teach me about it, Hayase";
        conv[20] = "Okay, today class is over!";
        conv[21] = "Don't forget to do the homework i gave you today";
        conv[22] = "Hayase, can you come to my office after school?";
        conv[23] = "I have something to talk about";
        ImageIcon rawImg = new ImageIcon("image/classroom.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(newImg));
        this.add(background);
        background.setSize(1000, 500);
        background.add(area);
        area.setBounds(250,400,500,100);
        area.setEditable(false);
        area.setVisible(false);
        int jum_npc_today = (int)(Math.random()*3+3);
        ActionListener act = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(ctr>=curctr+4){
                    t.stop();
                    t.setRepeats(false);
                    area.setText("");
                    area.setVisible(false);
                    int dialogbutton =0;
                    dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to spend the day with this person?", "Warning", dialogbutton);
                    if(dialogbutton == 0){
                        calltotar();
                    }
                }
                else{
                    ctr++;
                    area.setText(conv[ctr]);
                }
            }
        };
        t = new Timer(3000, act);
        int[] no_npc = new int[jum_npc_today];
        int[] rand = new int[jum_npc_today];
        for (int i = 0; i < jum_npc_today; i++) {
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
        }
        
        for (int i = 0; i < npcs.size(); i++) {
            if(!npcs.get(i).nama.equals("Sakura")){
                npcs.get(i).labelunit.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int randconv = (int)(Math.random()*5);
                        if(randconv == 0){
                            ctr = -1;
                            curctr = -1;
                        }
                        else if(randconv == 1){
                            ctr = 3;
                            curctr = 3;
                        }
                        else if(randconv == 2){
                            ctr = 7;
                            curctr = 7;
                        }
                        else if(randconv == 3){
                            ctr = 11;
                            curctr = 11;
                        }
                        else if(randconv == 4){
                            ctr = 15;
                            curctr = 15;
                        }
                        area.setVisible(true);
                        t.start();
                        t.setRepeats(true);
                    }
                });
            }
            else{
                npcs.get(i).labelunit.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        ctr = 19;
                        curctr = 19;
                        area.setVisible(true);
                        t.start();
                        t.setRepeats(true);
                    }
                });
            }
            background.add(npcs.get(i).labelunit);
            npcs.get(i).labelunit.setBounds(npcs.get(i).x, npcs.get(i).y, 75,75);
        }
        tohall.setText("To hallway");
        tohall.setForeground(Color.blue);
        tohall.setFont(classf);
        tohall.setBounds(475, 390, 200, 25);
        background.add(tohall);
        tohall2.setText("     v    ");
        tohall2.setForeground(Color.blue);
        tohall2.setFont(classf);
        tohall2.setBounds(475, 415, 200, 25);
        background.add(tohall2);
    }
    
    public void calltotar(){
        frameone f = (frameone)this.getParent().getParent().getParent().getParent();
        f.throughconv = true;
        f.totarg();
    }
    /**

     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(null, "X: "+evt.getX()+"\nY: "+evt.getY());
        for (int i =430; i <= 450; i++) {
            for (int j = 400; j <= 600; j++) {
                if(evt.getY()==i && evt.getX()==j){
                    frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                    f.tohall();
                }
            }
        }
    }//GEN-LAST:event_formMouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // TODO add your handling code here:
        int index = 0;
        int mouseX = evt.getX();
        int mouseY = evt.getY();
        for (int i = 0; i < npcs.size(); i++) {
            if(npcs.get(i).labelunit.getX()==mouseX && npcs.get(i).labelunit.getY()==mouseY){
                index = i;
            }
        }
        npcs.get(0).labelunit.setLocation(mouseX, mouseY);
    }//GEN-LAST:event_formMouseDragged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

class npc{
    public int x,y, mood;
    public String nama;
    public String[] conv = new String[4];
    public ImageIcon[] gambar = new ImageIcon[2];
    public JLabel labelunit;
    public npc(){
        conv[0] = "a";
        conv[1] = "b";
        conv[2] = "c";
        conv[3] = "d";
    }
    public String talk(int n){
        return conv[n];
    }
}

class sou extends npc{
    public sou(){
        this.nama = "Souji";
        ImageIcon rawImg = new ImageIcon("image/sou.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/sou2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
    
}

class hime extends npc{
    public hime(){
        this.nama = "Himeko";
        ImageIcon rawImg = new ImageIcon("image/hime.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/hime2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
}

class shiki extends npc{
    public shiki(){
        this.nama = "Shiki";
        ImageIcon rawImg = new ImageIcon("image/shiki.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/shiki2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
}

class noel extends npc{
    public noel(){
        this.nama = "Noel";
        ImageIcon rawImg = new ImageIcon("image/noel.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/noel2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
}

class inori extends npc{
    public inori(){
        this.nama = "Inori";
        ImageIcon rawImg = new ImageIcon("image/inori.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/inori2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 75, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
}

class sakura extends npc{
    public sakura(){
        this.nama = "Sakura";
        ImageIcon rawImg = new ImageIcon("image/sakura.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/sakura2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
}

class akio extends npc{
    public akio(){
        this.nama = "Akio";
        ImageIcon rawImg = new ImageIcon("image/akio.png");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0] = rawImg;

        ImageIcon rawImg2 = new ImageIcon("image/akio2.png");
        Image img2 = rawImg2.getImage();
        Image newImg2 = img2.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
        gambar[1] = new ImageIcon(newImg2);
    }
}