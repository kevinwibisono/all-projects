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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author User
 */
public class tartarusG extends javax.swing.JPanel {

    /**
     * Creates new form tartarusG
     */
    JLabel background = new JLabel();
    JLabel menutitle = new JLabel();
    JButton[] mbutton = new JButton[4];
    JPanel menu = new JPanel();
    menu m = new menu();
    player p1 = new player();
    int min =5, sec = 0, floor = 1, rand_map = 0, arah = -1, percent = 1000, mapnum, partnum;
    JLabel title = new JLabel();
    Font titlef = new Font("Times New Roman", 0, 35);
    Font buttonf = new Font("Times New Roman", 0, 15);
    Timer t;
    animasi anime;
    ImageIcon[][] tarmap = new ImageIcon[5][4];
    Boolean encounter;
    public tartarusG() {
        initComponents();
        this.setSize(1000, 500);
        this.add(menu);
        menu.setSize(216, 96);
        menutitle.setFont(buttonf);
        menutitle.setForeground(Color.red);
        menutitle.setText("Menu");
        menutitle.setLocation(0, 0);
        menu.add(menutitle);
        menu.setVisible(false);
        for (int i = 0; i < 4; i++) {
            mbutton[i] = new JButton();
            mbutton[i].setSize(73, 23);
            mbutton[i].setFont(buttonf);
            menu.add(mbutton[i]);
        }
        mbutton[1].setText("Item");
        mbutton[1].setLocation(116, 41);
        mbutton[2].setText("Equip");
        mbutton[2].setLocation(25, 76);
        mbutton[3].setText("Persona");
        mbutton[3].setLocation(116, 76);
        
        this.add(background);
        background.setSize(1000, 500);
        background.add(p1.lblplayer);
        jLabel1.setText(min+":"+sec);
        ActionListener act;
        act = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(min<=0 && sec<=0){
                    t.stop();
                    JOptionPane.showMessageDialog(null, "Time's out!");
                    min = 5;
                    sec = 0;
                    p1.lblplayer.setLocation(500, 450);
                    calltotar();
                    if(floor >= 25){
                        floor = 26;
                    }
                    else if(floor >= 20){
                        floor = 21;
                    }
                    else if(floor >= 15){
                        floor = 16;
                    }
                    else if(floor >= 10){
                        floor = 11;
                    }
                    else if(floor >= 5){
                        floor = 6;
                    }
                    else{
                        floor = 1;
                    }
                }
                if(sec<=0){
                    min--;
                    sec = 59;
                    if(min==0){
                        JOptionPane.showMessageDialog(null, "Time is running out!");
                    }
                }
                else{
                    sec--;
                }
                jLabel1.setText(min+":"+sec);
                
            }
        };
        t = new Timer(300, act);
        title.setText("Floor "+floor);
        title.setForeground(Color.yellow);
        title.setFont(titlef);
        title.setBounds(880, -75, 200, 200);
        background.add(title);
        
        ImageIcon rawImg = new ImageIcon("image/map1-1.jpg");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[0][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map1-2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[0][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map1-3.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[0][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map1-4.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[0][3] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map2-1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[1][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map2-2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[1][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map2-3.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[1][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map2-4.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[1][3] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map3-1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[2][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map3-2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[2][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map3-3.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[2][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map3-4.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[2][3] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map4-1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[3][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map4-2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[3][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map4-3.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[3][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map4-4.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[3][3] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map5-1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[4][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map5-2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[4][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map5-3.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[4][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/map5-4.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        tarmap[4][3] = new ImageIcon(newImg);
        
        p1.lblplayer.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if(menu.isVisible()){
                    menu.setVisible(false);
                }
                else{
                    menu.setLocation(p1.lblplayer.getX()+50, p1.lblplayer.getY()-106);
                    if(menu.getX()>784){
                        menu.setLocation(784, menu.getY());
                    }
                    if(menu.getY()<0){
                        menu.setLocation(menu.getX(), 0);
                    }
                    menu.setVisible(true);
                }
            }
        });
    }

    public void calltotar(){
        frameone f = (frameone)this.getParent().getParent().getParent().getParent();
        f.throughconv = false;
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

        jLabel1 = new javax.swing.JLabel();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(356, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(275, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null, "X: "+evt.getX()+"\nY: "+evt.getY());
    }//GEN-LAST:event_formMouseClicked

    public void toleft(){
        menu.setVisible(false);
        encounter = true;
        arah = 0;
        anime = new animasi(p1.lblplayer, p1.gambar, arah);
        anime.start();
        percent-=1;
        if(percent < 0){
            percent = 0;
        }
        int chance = (int)(Math.random()*1000);
        if(p1.lblplayer.getX()>0){
            p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
        }
        System.out.println("Per: "+percent);
        System.out.println("Ch: "+chance);
        if(chance < percent){
            encounter = false;
        }
        if(encounter){
            frameone f = (frameone)this.getParent().getParent().getParent().getParent();
            f.tobattle();
            percent = 1000;
        }
        if(mapnum == 0){
            if(partnum == 0){
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, 400);
                    }
                }
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<760){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
                for (int i = 120; i < 253; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<760){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 128; i < 244; i++) {
                    for (int j = 259; j < 484; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 100);
                    }
                }
                for (int i = 127; i < 253; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<756){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
                for (int i = 372; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<756){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                
                for (int i = 128; i < 244; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            
        }
        else if(mapnum == 1){
            if(partnum==0){
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 255; i < 366; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<509){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
            else if (partnum==1){
                for (int i = 380; i < 500; i++) {
                    for (int j = 760; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 128; i < 378; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<761 && p1.lblplayer.getX()>491){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==2){
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==3){
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
        }
        else if(mapnum == 2){
            if(partnum==0){
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 0; i < 254; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<508){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<508){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==1){
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 122; i < 253; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>192 && p1.lblplayer.getX()<511){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==2){
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 9; i < 120; i++) {
                    for (int j = 763; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 244; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<259){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
                for (int i = 244; i < 378; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>442 && p1.lblplayer.getX()<762){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
                    }
                }
            }
        }
        else if(mapnum == 3){
            if(partnum == 0){
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 253; i < 366; i++) {
                    for (int j = 740; j < 983; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
        else if(mapnum == 4){
            if(partnum == 0){
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 130; i < 246; i++) {
                    for (int j = 16; j < 260; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
    }
    
    public void toright(){
        menu.setVisible(false);
        encounter = true;
        arah = 1;
        anime = new animasi(p1.lblplayer, p1.gambar, arah);
        anime.start();
        percent-=1;
        if(percent <0){
            percent = 0;
        }
        int chance = (int)(Math.random()*1000);
        if(p1.lblplayer.getX()<950){
            p1.lblplayer.setLocation(p1.lblplayer.getX()+20, p1.lblplayer.getY());
        }
        System.out.println("Per: "+percent);
        System.out.println("Ch: "+chance);
        if(chance < percent){
            encounter = false;
        }
        if(encounter){
            frameone f = (frameone)this.getParent().getParent().getParent().getParent();
            f.tobattle();
            percent = 1000;
        }
        if(mapnum == 0){
            if(partnum == 0){
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, 400);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 128; i < 244; i++) {
                    for (int j = 259; j < 484; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                    
                }
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 100);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                
                for (int i = 128; i < 244; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            
        }
        else if(mapnum == 1){
            if(partnum==0){
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if (partnum==1){
                for (int i = 380; i < 500; i++) {
                    for (int j = 760; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum==2){
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==3){
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
        }
        else if(mapnum == 2){
            if(partnum==0){
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 0; i < 254; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>687){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
                    }
                }
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>687){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==1){
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 122; i < 253; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>192 && p1.lblplayer.getX()<511){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
                    }
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>692){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==2){
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                if(p1.lblplayer.getX()>189){
                    p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
                }
            }
            else if(partnum == 3){
                for (int i = 9; i < 120; i++) {
                    for (int j = 763; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 244; i < 378; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>442 && p1.lblplayer.getX()<762){
                        p1.lblplayer.setLocation(p1.lblplayer.getX()-20, p1.lblplayer.getY());
                    }
                }
            }
        }
        else if(mapnum == 3){
            if(partnum == 0){
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 253; i < 366; i++) {
                    for (int j = 740; j < 983; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
        else if(mapnum == 4){
            if(partnum == 0){
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 130; i < 246; i++) {
                    for (int j = 16; j < 260; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
    }
    
    public void toup(){
        menu.setVisible(false);
        encounter = true;
        arah = 2;
        anime = new animasi(p1.lblplayer, p1.gambar, arah);
        anime.start();
        percent-=1;
        if(percent < 0){
            percent = 0;
        }
        int chance = (int)(Math.random()*1000);
        if(p1.lblplayer.getY()>0){
            p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
        }
        System.out.println("Per: "+percent);
        System.out.println("Ch: "+chance);
        if(chance < percent){
            encounter = false;
        }
        if(encounter){
            frameone f = (frameone)this.getParent().getParent().getParent().getParent();
            f.tobattle();
            percent = 1000;
        }
        if(mapnum == 0){
            if(partnum == 0){
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
                if(p1.lblplayer.getY()<380){
                    p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                }
            }
            else if(partnum == 1){
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, 400);
                    }
                }
                for (int i = 0; i < 757; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254 && p1.lblplayer.getY()>123){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 128; i < 244; i++) {
                    for (int j = 259; j < 484; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                    
                }
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 100);
                    }
                }
                for (int i = 489; i < 758; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<255){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                for (int i = 0; i < 258; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<255){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                if(p1.lblplayer.getY()<127){
                    p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                }
            }
            else if(partnum == 3){
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                
                for (int i = 128; i < 244; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            
        }
        else if(mapnum == 1){
            if(partnum==0){
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 740; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                for (int i = 0; i < 739; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<128){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
            }
            else if (partnum==1){
                for (int i = 380; i < 500; i++) {
                    for (int j = 760; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                if(p1.lblplayer.getY()<128){
                    p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                }
            }
            else if(partnum==2){
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==3){
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
        }
        else if(mapnum == 2){
            if(partnum==0){
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 0; i < 509; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                for (int i = 738; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
            }
            else if(partnum==1){
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 242; i < 512; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254 && p1.lblplayer.getY()>121){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                for (int i = 742; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254 && p1.lblplayer.getY()>121){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
            }
            else if(partnum==2){
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<380){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                
            }
            else if(partnum == 3){
                for (int i = 9; i < 120; i++) {
                    for (int j = 763; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 492; i < 763; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<377){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
                for (int i = 0; i < 763; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<129){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
                    }
                }
            }
        }
        else if(mapnum == 3){
            if(partnum == 0){
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 253; i < 366; i++) {
                    for (int j = 740; j < 983; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
        else if(mapnum == 4){
            if(partnum == 0){
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 130; i < 246; i++) {
                    for (int j = 16; j < 260; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
    }
    
    public void todown(){
        menu.setVisible(false);
        encounter = true;
        arah = 3;
        anime = new animasi(p1.lblplayer, p1.gambar, arah);
        anime.start();
        percent-=1;
        if(percent < 0){
            percent = 0;
        }
        int chance = (int)(Math.random()*1000);
        if(p1.lblplayer.getY()<450){
            p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()+20);
        }
        System.out.println("Per: "+percent);
        System.out.println("Ch: "+chance);
        if(chance < percent){
            encounter = false;
        }
        if(encounter){
            frameone f = (frameone)this.getParent().getParent().getParent().getParent();
            f.tobattle();
            percent = 1000;
        }
        if(mapnum == 0){
            if(partnum == 0){
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 400);
                    }
                }
                for (int i = 377; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, 400);
                    }
                }
                for (int i = 0; i < 757; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>73 && p1.lblplayer.getY()<254){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>320){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 128; i < 244; i++) {
                    for (int j = 259; j < 484; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                    
                }
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 985; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, 100);
                    }
                }
                for (int i = 489; i < 758; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>321){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
                for (int i = 0; i < 258; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>321){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 378; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                
                for (int i = 128; i < 244; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            
        }
        else if(mapnum == 1){
            if(partnum==0){
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 509; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>317){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
                for (int i = 0; i < 508; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>194){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
            }
            else if (partnum==1){
                for (int i = 380; i < 500; i++) {
                    for (int j = 760; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 10; i < 238; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 380; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum==2){
                for (int i = 260; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 760; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum==3){
                for (int i = 130; i < 245; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 268; i < 495; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
        }
        else if(mapnum == 2){
            if(partnum==0){
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 0; i < 509; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>320){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
                for (int i = 738; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>320){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
            }
            else if(partnum==1){
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 255; i < 370; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                if(p1.lblplayer.getY()>320){
                    p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                }
                for (int i = 242; i < 512; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254 && p1.lblplayer.getY()>71){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
                for (int i = 742; i < 1000; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<254 && p1.lblplayer.getY()>71){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
            }
            else if(partnum==2){
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 12; i < 242; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 9; i < 120; i++) {
                    for (int j = 763; j < 1000; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 379; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 508; i < 737; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 0; i < 260; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>194){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
                for (int i = 491; i < 762; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>194 && p1.lblplayer.getY()<379){
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), p1.lblplayer.getY()-20);
                    }
                }
                
            }
        }
        else if(mapnum == 3){
            if(partnum == 0){
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 253; i < 366; i++) {
                    for (int j = 740; j < 983; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 253; i < 368; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 8; i < 120; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 511; i < 740; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 370; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
        else if(mapnum == 4){
            if(partnum == 0){
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum = 3;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 1){
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()<25){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(900, p1.lblplayer.getY());
                    }
                }
                for (int i = 11; i < 241; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
            }
            else if(partnum == 2){
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()>425){
                        partnum+=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 100);
                    }
                }
                for (int i = 249; i < 500; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
            else if(partnum == 3){
                for (int i = 130; i < 246; i++) {
                    for (int j = 16; j < 260; j++) {
                        if(p1.lblplayer.getX()==j && p1.lblplayer.getY()==i){
                            int dialogbutton;
                            dialogbutton = JOptionPane.showConfirmDialog(null, "Do you want to proceed to next floor?", "Warning",0);
                            System.out.println(dialogbutton);
                            if(dialogbutton == 0){
                                min = 5;
                                sec = 0;
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                floor+=1;
                                f.totar();
                            }
                            else{
                                frameone f = (frameone)this.getParent().getParent().getParent().getParent();
                                f.totarg();
                                if(floor >= 25){
                                    floor = 26;
                                }
                                else if(floor >= 20){
                                    floor = 21;
                                }
                                else if(floor >= 15){
                                    floor = 16;
                                }
                                else if(floor >= 10){
                                    floor = 11;
                                }
                                else if(floor >= 5){
                                    floor = 6;
                                }
                                else{
                                    floor = 1;
                                }
                            }
                        }
                    }
                }
                for (int i = 259; i < 490; i++) {
                    if(p1.lblplayer.getX()==i && p1.lblplayer.getY()<25){
                        partnum-=1;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(p1.lblplayer.getX(), 400);
                    }
                }
                for (int i = 131; i < 246; i++) {
                    if(p1.lblplayer.getY()==i && p1.lblplayer.getX()>925){
                        partnum = 0;
                        background.setIcon(tarmap[mapnum][partnum]);
                        p1.lblplayer.setLocation(100, p1.lblplayer.getY());
                    }
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

class player{
    ImageIcon[][] gambar = new ImageIcon[4][3];
    JLabel lblplayer = new JLabel();
    animasi anime;
    ArrayList<String> bag = new ArrayList<String>();
    int ctr =-1, arah=0, percent = 0, mapnum, money, lv = 1;
    public player(){
        money = 1000;
        
        ImageIcon rawImg = new ImageIcon("image/rleft.jpg");
        Image img = rawImg.getImage();
        Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rleft1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rleft2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[0][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rright.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[1][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rright1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[1][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rright2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[1][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rup.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[2][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rup1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[2][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rup2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[2][2] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rdown.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[3][0] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rdown1.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[3][1] = new ImageIcon(newImg);
        
        rawImg = new ImageIcon("image/rdown2.jpg");
        img = rawImg.getImage();
        newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        gambar[3][2] = new ImageIcon(newImg);
        
        int randimg = (int)(Math.random()*4);
        lblplayer.setIcon(gambar[randimg][0]);
    }
    
}


class animasi extends java.lang.Thread implements Serializable{
    JLabel lblUnit;
    ImageIcon[][] gambar = new ImageIcon[4][3];
    int ctrGerak, cmd;
    public animasi(JLabel lblUnit, ImageIcon[][] gambar, int cmd){
        this.ctrGerak = 0;
        this.lblUnit = lblUnit;
        this.gambar = gambar;
        this.cmd = cmd;
    }
    
    @Override
    public void run() {
        while(ctrGerak < 5){
            ctrGerak++;
            
            if(ctrGerak == 5 || ctrGerak == 0){
                this.lblUnit.setIcon(gambar[cmd][0]);
            }else
            if(ctrGerak == 1 || ctrGerak == 3){
                this.lblUnit.setIcon(gambar[cmd][1]);
            }else
            if(ctrGerak == 2){
                this.lblUnit.setIcon(gambar[cmd][2]);
            }
            
            //setLoc();
            
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    }
}
