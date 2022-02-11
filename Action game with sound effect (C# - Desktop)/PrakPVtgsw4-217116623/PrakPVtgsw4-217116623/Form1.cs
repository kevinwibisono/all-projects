using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using ClassLibrary1;
using System.Media;

namespace PrakPVtgsw4_217116623
{
    public partial class Form1 : Form
    {
        SoundPlayer soundeffect;
        List<Musuh> msh;
        List<int> highscores;
        Image[] player = new Image[10];
        Image[] enemyleft = new Image[3];
        Image[] enemyright = new Image[3];
        Random r = new Random();
        int standby = 0;
        int x = 100;
        int y = 75;
        int time = 1;
        int movenum = 0;
        int kloter = 0;
        int life = 3;
        int score = 0;
        int index = 0;
        int index2 = 0;
        int speed = 10;
        bool mainmenu = true;
        bool seehighscr = false;
        bool done = false;
        public Form1()
        {
            InitializeComponent();
            DoubleBuffered = true;
            soundeffect = new SoundPlayer("PUNCH.wav");
            msh = new List<Musuh>();
            player[0] = Image.FromFile("standby1.png");
            player[1] = Image.FromFile("standby2.png");
            player[2] = Image.FromFile("standby3.png");
            player[3] = Image.FromFile("bfratk.png");
            player[4] = Image.FromFile("attack.png");
            player[5] = Image.FromFile("bfratkleft.png");
            player[6] = Image.FromFile("attackleft.png");
            player[7] = Image.FromFile("bfratkright.png");
            player[8] = Image.FromFile("attackright.png");
            player[9] = Image.FromFile("playerdead.png");

            enemyleft[0] = Image.FromFile("enemyleft1.png");
            enemyleft[1] = Image.FromFile("enemyleft2.png");
            enemyleft[2] = Image.FromFile("enemydead.png");

            enemyright[0] = Image.FromFile("enemyright1.png");
            enemyright[1] = Image.FromFile("enemyright2.png");
            enemyright[2] = Image.FromFile("enemydead.png");
            highscores = new List<int>();
        }

        private void Form1_Paint(object sender, PaintEventArgs e)
        {
            //MessageBox.Show(standby.ToString());
            Graphics g = e.Graphics;
            if (seehighscr)
            {
                g.FillRectangle(new SolidBrush(Color.Gray), 50, 312, 75, 25);
                g.DrawString("Back to main menu", new Font("Arial", 6), new SolidBrush(Color.Black), 50, 314);
                g.DrawString("Highscores : ", new Font("Magneto", 20), new SolidBrush(Color.Gold), 0, 0);
                g.FillRectangle(new SolidBrush(Color.White), 50, 100, 200, 200);
                if(highscores.Count > 0)
                {
                    for (int i = 0; i < highscores.Count; i++)
                    {
                        g.DrawImage(enemyleft[0], 50, (100 + i * 25), 25, 25);
                        g.DrawString("x" + highscores[i], new Font("Arial", 14), new SolidBrush(Color.Black), 75, (100 + i * 25));
                    }
                }
                else
                {
                    g.DrawString("No highscores", new Font("Arial", 14), new SolidBrush(Color.Black), 50, 100);
                }
            }
            else if (mainmenu)
            {
                g.DrawString("Karate Man", new Font("Magneto", 20), new SolidBrush(Color.Red), 0, 0);
                g.FillRectangle(new SolidBrush(Color.Gray), 100, 200, 75, 25);
                g.DrawString("Play", new Font("Calibri", 10), new SolidBrush(Color.Black), 125, 202);
                g.FillRectangle(new SolidBrush(Color.Gray), 100, 250, 75, 25);
                g.DrawString("Highscore", new Font("Calibri", 10), new SolidBrush(Color.Black), 110, 252);
                g.FillRectangle(new SolidBrush(Color.Gray), 100, 300, 75, 25);
                g.DrawString("Quit", new Font("Calibri", 10), new SolidBrush(Color.Black), 125, 302);
            }
            else if (!mainmenu)
            {
                g.DrawImage(player[0], 0, 0, 50, 50);
                g.FillRectangle(new SolidBrush(Color.White), 50, 12, 25, 25);
                g.DrawString("x" + life, new Font("Calibri", 10), new SolidBrush(Color.Black), 50, 12);
                g.DrawImage(enemyleft[0], 75, 0, 50, 50);
                g.FillRectangle(new SolidBrush(Color.White), 125, 12, 25, 25);
                g.DrawString("x" + score, new Font("Calibri", 10), new SolidBrush(Color.Black), 125, 12);
                g.DrawImage(player[standby], x, y, 100, 100);

                for (int i = 0; i < msh.Count; i++)
                {
                    g.DrawImage(msh[i].Gambar[msh[i].Movenum], msh[i].X, msh[i].Y, 100, 100);
                }
            }
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if(standby == 9)
            {
                standby = 0;
            }
            if(standby >= 2)
            {
                standby = 0;
            }
            else
            {
                standby += 1;
            }
            Invalidate();
        }

        private void Form1_MouseClick(object sender, MouseEventArgs e)
        {
            if(e.Button == MouseButtons.Left)
            {
                if (seehighscr)
                {
                    Rectangle r = new Rectangle(50, 312, 75, 25);
                    if(r.Contains(e.X, e.Y))
                    {
                        mainmenu = true;
                        seehighscr = false;
                    }
                    Invalidate();
                }
                else if (mainmenu)
                {
                    Rectangle r = new Rectangle(100, 200, 75, 25);
                    Rectangle r2 = new Rectangle(100, 250, 75, 25);
                    Rectangle r3 = new Rectangle(100, 300, 75, 25);
                    if (r.Contains(e.X, e.Y))
                    {
                        BackgroundImage = PrakPVtgsw4_217116623.Properties.Resources.arena;
                        BackgroundImageLayout = ImageLayout.Stretch;
                        mainmenu = false;
                        life = 3;
                        score = 0;
                        speed = 10;
                        standby = 0;
                        timer1.Start();
                        timer5.Start();
                    }
                    else if(r2.Contains(e.X, e.Y))
                    {
                        mainmenu = false;
                        seehighscr = true;
                    }
                    else if (r3.Contains(e.X, e.Y))
                    {
                        this.Close();
                    }
                }
                else
                {
                    if (e.X < 100)
                    {
                        standby = 2;
                        timer1.Stop();
                        timer3.Start();
                    }
                    else if (e.X > 200)
                    {
                        standby = 2;
                        timer1.Stop();
                        timer4.Start();
                    }
                    else
                    {
                        standby = 2;
                        timer1.Stop();
                        timer2.Start();
                    }
                }
            }
            Invalidate();
        }

        private void timer2_Tick(object sender, EventArgs e)
        {
            if (standby >= 4)
            {
                timer2.Stop();
                standby = 0;
                for (int i = 0; i < msh.Count; i++)
                {
                    if(msh[i].X == x && (msh[i].Y >= y + 100 && msh[i].Y < y + 200))
                    {
                        soundeffect.Play();
                        score += 1;
                        index2 = i;
                        msh[i].Movenum = 2;
                        msh[i].Defeated = true;
                        if (score == 10)
                        {
                            speed *= 2;
                        }
                    }
                }
                timer1.Start();
            }
            else
            {
                standby += 1;
            }
            Invalidate();
        }

        private void Form1_KeyUp(object sender, KeyEventArgs e)
        {
        }

        private void timer3_Tick(object sender, EventArgs e)
        {
            if (standby >= 6)
            {
                timer3.Stop();
                standby = 0;
                timer1.Start();
            }
            if (standby == 2)
            {
                standby = 5;
                for (int i = 0; i < msh.Count; i++)
                {
                    if (msh[i].X == 0 && (msh[i].Y >= y + 100 && msh[i].Y < y + 200))
                    {
                        soundeffect.Play();
                        score += 1;
                        index = i;
                        msh[i].Movenum = 2;
                        msh[i].Defeated = true;
                        if (score == 10)
                        {
                            speed *= 2;
                        }
                    }
                }
            }
            else
            {
                standby += 1;
            }
            Invalidate();
        }

        private void timer4_Tick(object sender, EventArgs e)
        {
            if (standby >= 8)
            {
                timer4.Stop();
                standby = 0;
                timer1.Start();
            }
            if (standby == 2)
            {
                standby = 7;
                for (int i = 0; i < msh.Count; i++)
                {
                    if (msh[i].X == 200 && (msh[i].Y >= y + 100 && msh[i].Y < y + 200))
                    {
                        soundeffect.Play();
                        score += 1;
                        index = i;
                        msh[i].Movenum = 2;
                        msh[i].Defeated = true;
                        if (score == 10)
                        {
                            speed *= 2;
                        }
                    }
                }
            }
            else
            {
                standby += 1;
            }
            Invalidate();
        }

        private void timer5_Tick(object sender, EventArgs e)
        {
            if(msh.Count > 0)
            {
                for (int i = 0; i < msh.Count; i++)
                {
                    if (msh[i].Defeated)
                    {
                        msh.RemoveAt(i);
                    }
                }
                timer6.Start();
                Invalidate();
            }

            if(time % 2 == 0)
            {
                int randmusuh = r.Next(1, 4);
                for (int i = 1; i <= randmusuh; i++)
                {
                    Musuh m = new Musuh((i-1)* 100, 360 + kloter * 75);
                    if(m.X < 100)
                    {
                        m.Gambar = enemyleft;
                    }
                    else if(m.Y >= 200)
                    {
                        m.Gambar = enemyright;
                    }
                    else
                    {
                        m.Gambar = enemyleft;
                    }
                    msh.Add(m);
                }
            }
            time += 1;
            kloter += 1;
            Invalidate();
        }

        //timer6_Tick
        private void timer6_Tick_1(object sender, EventArgs e)
        {
            for (int i = 0; i < msh.Count; i++)
            {
                if (!msh[i].Defeated)
                {
                    if (msh[i].Movenum >= 1)
                    {
                        msh[i].Movenum = 0;
                    }
                    else
                    {
                        msh[i].Movenum += 1;
                    }
                    msh[i].Y -= speed;
                    if (msh[i].Y < y + 100)
                    {
                        if (life > 0)
                        {
                            life -= 1;
                        }
                        if (life == 0)
                        {
                            life = 0;
                            timer1.Stop();
                            timer5.Stop();
                            timer6.Stop();
                            timer7.Stop();
                            mainmenu = true;
                            BackgroundImage = null;
                            if (highscores.Count < 5)
                            {
                                highscores.Add(score);
                                sort(highscores);
                            }
                            else
                            {
                                int Min = 9999;
                                int index = 0;
                                if (highscores.Count >= 5)
                                {
                                    for (int j = 0; j < highscores.Count; j++)
                                    {
                                        if (highscores[j] < Min)
                                        {
                                            Min = highscores[j];
                                            index = j;
                                        }
                                    }
                                    if (score > Min)
                                    {
                                        highscores[index] = score;
                                        sort(highscores);
                                    }
                                }
                            }
                        }
                        standby = 9;
                        msh.Clear();
                        kloter = 0;
                        break;
                    }
                }
            }
            Invalidate();
            if(life == 0)
            {
                MessageBox.Show("You Lose");
            }
        }

        private void timer7_Tick(object sender, EventArgs e)
        {
            
        }
        
        public void sort(List<int> ints)
        {
            for (int i = 0; i < ints.Count -1; i++)
            {
                for (int j = i; j < ints.Count; j++)
                {
                    if(ints[i] < ints[j])
                    {
                        int temp = ints[i];
                        ints[i] = ints[j];
                        ints[j] = temp;
                    }
                }
            }
        }
    }
}
