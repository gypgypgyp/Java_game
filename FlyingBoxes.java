package games;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class FlyingBoxes extends Window implements ActionListener {

  //static vars
  public static Box.List boxes = new Box.List(); // it should be put before creating any box
  public static Random RND = new Random(); // it should be put before creating a box
  public static Box bkg = new Box(Color.WHITE,0,0,5000,5000,0,0); //creating background box

  static{bkg.v.x = 0; bkg.v.y = 0; bkg.acc.x = 0; bkg.acc.y = 0;}// make the bkg not move. set the vars as static.
  // in a big system static blocks may be difficult to debug. timing bugs. always put all the static
  public static Box dude = new Box(100,100,100,100);

  public static int xMax = 900;
  public static int yMax = 700;
  public static Timer timer; // adding timer


  //non-static vars/ member vars

  //constructor
  public FlyingBoxes(){
    super("Flying Boxes", 1000, 700);

    //create some boxes, and every box will be added to the box list
    for(int i = 0; i < 100; i++){
      new Box(rnd(800), rnd(500), 100, 100);
    }

    timer = new Timer(30, this); // 30 millisecond. this(the box) is listening.
    timer.setInitialDelay(2000); // 2 seconds
    timer.start(); //every 30 millisecond, call actionPerformed, the listener is this, and repaint
  }

  //member functions (non-static)
  public void paintComponent(Graphics g){
//    g.setColor(Color.WHITE);
//    g.fillRect(0,0,5000,5000);
//    g.setColor(Color.RED);
//    g.fillRect(100,100,100,100);
    bkg.show(g);
//    dude.show(g);
    boxes.show(g);
  }

  //static functions
  public static void main(String[] Args){
    PANEL = new FlyingBoxes();
    Window.launch();
  }

  /**
   * generate random color
   * @param max
   * @return
   */
  public static int rnd(int max){return RND.nextInt(max);}

  public static int rndV(){return rnd(20) - 10;} // give a random num between -10 and 10

  @Override
  public void actionPerformed(ActionEvent e) {repaint();}


  //nested classes
  //---------------------------------Box-----------------------------
  public static class Box{
    public Point loc, size;
    public Point v; // = new Point(2, 5); //velocity, will create a class var
    public Color color;
    public Point acc = new Point(0, 1); // acceleration, gravity


    // constructor for the background
    public Box(Color c, int x, int y, int w, int h, int vx, int vy) {
      color = c;
      loc = new Point(x, y);
      size = new Point(w, h);
//      Point v = new Point(); // will create a local var in the constructor
      v = new Point(vx, vy);
    }

    // constructor for other boxes
    public Box(int x, int y, int w, int h) {
//      color = c;
      color = new Color(rnd(255), rnd(255), rnd(255));
      loc = new Point(x, y);
      size = new Point(w, h);
//      Point v = new Point(); // will create a local var in the constructor
//      v = new Point(4, 7);
      v = new Point(rndV(), rndV());
      boxes.add(this);
      // put the box on the list
      //give a random color
      //give a different velocity

    }

    public void show(Graphics g){
      g.setColor(color);
      g.fillRect(loc.x, loc.y, size.x, size.y);
      // making the movement as the side effect of show routine
      move();
    }

    public void move(){
      loc.x += v.x; loc.y += v.y;
      v.x += acc.x; v.y += acc.y;
      if(loc.y > yMax && v.y > 0){v.y = -v.y; v.y+=2;} //2 is how it is losing velocity
      if(loc.x > xMax && v.x > 0){v.x = -v.x;}
      if(loc.x < 0 && v.x < 0){v.x = -v.x;}
      if(shouldStop()) { // stop when y is too small
        loc.y = yMax;
        v.y = 0;
        acc.y = 0;
      }
    }

    // when the y is too small, it still moving in y axis ----> stop when y is too small
    private boolean shouldStop() {
      // judge if the y value is close enough to yMAX
      return ((Math.abs(loc.y - yMax) < 5) && (Math.abs(v.y) < 3));
    }

    //------------------------------List---------------------------
    // list of boxes
    public static class List extends ArrayList<Box> {

      /**
       * show the box in the list
       * @param g
       */
      public void show(Graphics g){for(Box b: this){b.show(g);}}
    }
  }
}
