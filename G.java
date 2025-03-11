package games;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class G {
  //directions of movement
  public static V LEFT = new V(-1, 0);
  public static V RIGHT = new V(1, 0);
  public static V UP = new V(0, -1);
  public static V DOWN = new V(0, 1);

  //random number generator
  public static Random RANDOM = new Random();
  public static int rnd(int m){return RANDOM.nextInt(m);}
  public static void fillBackGround(Graphics g){
    g.setColor(Color.WHITE);
    g.fillRect(0,0,5000,5000);
  }

  //-----------------V-------------------------
  public static class V{
    public int x, y;
    public V(){}
    public V(int x, int y) {
      set(x, y);
    }
    public V(V v){set(v.x, v.y);} // constructer when passing in a Vector

    public void set(int x, int y){
      this.x = x;
      this.y = y;
    }

    public void set(V v){
      x = v.x;
      y = v.y;
    }

    public void add(V v){
      x += v.x;
      y += v.y;
    }
  }

  //--------------------VS(VectorsSize)----------------------------
  // location and size of the vector
  public static class VS {
    public V loc, size;
    public VS(int x, int y, int w, int h){
      loc = new V(x, y);
      size = new V(w, h);
    }

    //(for mouse reaction)
    public boolean contains(int x, int y){
      return x>loc.x && y>loc.y && x<(loc.x+size.x) && y<(loc.y+size.y);
    }

    //fill the rect
    public void fill(Graphics g, Color c){
      g.setColor(c);
      g.fillRect(loc.x, loc.y, size.x, size.y);
    }

    //draw the outline of rect
    public void draw(Graphics g, Color c){
      g.setColor(c);
      g.drawRect(loc.x, loc.y, size.x, size.y);
    }
  }

  //-----------------BUTTON-----------------
  public static abstract class Button {

    public abstract void act();

    public boolean enabled = true, bordered = true;
    public String text = "";
    public VS vs = new VS(0, 0, 0, 0);
    public int dyText = 0;
    public LookAndFeel lmf = new LookAndFeel();

    public Button(Button.List list, String str) {
      if (list != null) {
        list.add(this);
      }
      text = str;
    }

    public void show(Graphics g){
      if(vs.size.x==0){setSize(g);}
      vs.fill(g, lmf.back);
      if(bordered){vs.draw(g,lmf.border);}
      g.setColor(enabled ? lmf.text : lmf.disable); //color for text
      g.drawString(text, vs.loc.x + lmf.M.x, vs.loc.y+dyText);
    }

    public void setSize(Graphics g){
      FontMetrics fm = g.getFontMetrics();
      vs.size.set(2*lmf.M.x+fm.stringWidth(text), 2*lmf.M.y+fm.getHeight());
      dyText = fm.getAscent()+lmf.M.y;
    }

    public boolean hit(int x, int y){return vs.contains(x, y);}

    public void click(){if (enabled){act();}}

    public void set(int x, int y){vs.loc.set(x,y);}


      //---------------LOOK AND FEEL
      public static class LookAndFeel {

        public static Color text = Color.BLACK, back = Color.WHITE,
            border = Color.BLACK, disable = Color.GRAY;
        public static V M = new V(5, 3); //margin of the text square
      }

      //-----------------Button.list
      public static class List extends ArrayList<Button> {

        public Button hit(int x, int y) {
          for (Button b : this) {
            if (b.hit(x, y)) {
              return b;
            }
          }
          return null;
        }

        public boolean clicked(int x, int y) {
          Button b = hit(x,y);
          if(b==null){return false;}
          b.click();
          return true;
        }

        public void show (Graphics g){
          for(Button b: this){b.show(g);}
        }
      }
  }
}
