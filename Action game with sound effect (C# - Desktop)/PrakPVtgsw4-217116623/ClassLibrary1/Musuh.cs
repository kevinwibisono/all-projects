using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;

namespace ClassLibrary1
{
    public class Musuh
    {
        private int x;
        private int y;
        private int movenum;
        private Image[] gambar = new Image[3];
        private bool defeated;

        public Musuh(int x, int y)
        {
            this.x = x;
            this.Y = y;
            this.Movenum = 0;
            this.Defeated = false;
        }

        public int X { get => x; set => x = value; }
        public int Y { get => y; set => y = value; }
        public Image[] Gambar { get => gambar; set => gambar = value; }
        public int Movenum { get => movenum; set => movenum = value; }
        public bool Defeated { get => defeated; set => defeated = value; }
    }
}
