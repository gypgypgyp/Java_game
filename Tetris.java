package games;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Tetris extends Window implements ActionListener {
  public static final int H = 20, W = 10, C = 25;
  public static Color[] colors = {
      Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.BLACK, Color.PINK
  };
  public static final int iBkCol = 7, zap = 8; //i background color, add another background color
  public static int[][] well = new int[W][H];
  public static Shape[] shapes = {Shape.Z, Shape.S, Shape.J, Shape.L, Shape.I, Shape.O, Shape.T};
  public static Timer timer;
  public static int time = 1, iShape = 0;
  public static Shape shape;
  public static final int xM = 50, yM = 50;

  public Tetris(){
    super("Tetris", 1000, 700);
//    shape = shapes[G.rnd(7)];
    Shape.dropNewShape();
    timer = new Timer(30, this);
    timer.start();
    clearWell();
  }

  public void paintComponent(Graphics g){
    G.fillBackGround(g);
//    time++;
//    if(time==60){ //2 seconds gap
//      time = 0;
//      iShape = (iShape+1)%7; //change to another shape
//    }
//    if(time==30){
//      shapes[iShape].rot();
//    }
//    shapes[iShape].show(g);

    //drop blocks as time runs
    time++;
    if(time==30){
      time = 0;
      shape.drop();
    }

    unzapWell();
    showWell(g);
    shape.show(g);
  }

  public void keyPressed(KeyEvent ke){
    int vk = ke.getKeyCode();
    if(vk==KeyEvent.VK_LEFT){shape.slide(G.LEFT);}
    if(vk==KeyEvent.VK_RIGHT){shape.slide(G.RIGHT);}
    if(vk==KeyEvent.VK_UP){shape.safeRot();}
    if(vk==KeyEvent.VK_DOWN){shape.drop();}
    repaint();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  //消除某一行
  public static void zapWell(){
    for(int y=0;y<H;y++){zapRow(y);}
  }
  public static void zapRow(int y){
    for(int x=0;x<W;x++){if(well[x][y]==iBkCol){return;}}
    for(int x=0;x<W;x++){well[x][y]=zap;}
  }
  public static void unzapWell(){
    boolean done = false;
    for(int y=1;y<H;y++){
      for(int x=0;x<W;x++){
        if(well[x][y-1]!=zap && well[x][y]==zap){
          //when find a line to move
          done = true;
          well[x][y] = well[x][y-1];
          well[x][y-1] = (y==1 ? iBkCol : zap);
        }
      }
      if(done){return;}
    }
  }

  //deal with well
  public static void clearWell(){
    //set the cell to background color
    for(int x=0; x<W; x++){
      for(int y=0; y<H; y++){
        well[x][y] = iBkCol;
      }
    }
  }
  public static void showWell(Graphics g){
    for(int x=0; x<W; x++){
      for(int y=0; y<H; y++){
        g.setColor(colors[well[x][y]]);
        int xx = xM+C*x, yy = yM+C*y;
        g.fillRect(xx, yy, C, C);
        g.setColor(Color.BLACK);
        g.drawRect(xx, yy, C, C);
      }
    }
  }
  public static void main(String[] args){
    PANEL = new Tetris();
    Window.launch();
  }

  //-------------------------Shape--------------
  //shape of different blocks
  public static class Shape{
    public static Shape Z = new Shape(new int[]{0,0,1,0,1,1,2,1},0),
        S = new Shape(new int[]{0,1,1,0,1,1,2,0},1),
        J = new Shape(new int[]{0,0,0,1,1,1,2,1},2),
        L = new Shape(new int[]{0,1,1,1,2,1,2,0},3),
        I = new Shape(new int[]{0,0,1,0,2,0,3,0},4),
        O = new Shape(new int[]{0,0,1,0,0,1,1,1},5),
        T = new Shape(new int[]{0,1,1,0,1,1,2,1},6);

    public int iColor;
    public G.V[] a = new G.V[4]; //
    public G.V loc = new G.V(0,0);
    public static G.V temp = new G.V(0,0); //for rotation
    public Shape(int[] xy, int iColor){
      this.iColor = iColor;
      for(int i = 0;i<4;i++){
        a[i] = new G.V(xy[2*i], xy[2*i+1]);
      }
    }
    //detect if the rotation is illegal-->come back to the original place
    public void safeRot(){
      rot();
      cdsSet();
      if(collisionDetected()){rot();rot();rot();}
    }
    public void rot(){ //for rotation
      //prevent showing negative nums
      temp.set(0,0);
      for(int i=0; i<4; i++){
        a[i].set(-a[i].y, a[i].x);
        if(temp.x>a[i].x){temp.x = a[i].x;}
        if(temp.y>a[i].y){temp.y = a[i].y;}
      }
      temp.set(-temp.x, -temp.y);
      for(int i=0; i<4; i++){
        a[i].add(temp);
      }
    }


    public static Shape cds = new Shape(new int[]{0,0,0,0,0,0,0,0}, 0); // collision detection
    public static boolean collisionDetected(){
      for(int i=0; i<4; i++){
        G.V v = cds.a[i];//first square at the list
        //copy a shape to the cds, check if it could be rotated
        if(v.x<0 || v.x>=W || v.y<0 || v.y>=H){return true;}
        // hit anything in the well, if hit anything that are not background color
        if(well[v.x][v.y]!=iBkCol){return true;}
      }
      return false;
    }
    public void cdsSet(){
      for(int i=0;i<4;i++){
        cds.a[i].set(a[i]);
        cds.a[i].add(loc); // make the location in the correct place
      }
    }
    public void cdsGet(){for(int i=0;i<4;i++){a[i].set(cds.a[i]);}}
    public void cdsAdd(G.V v){for(int i=0;i<4;i++){cds.a[i].add(v);}}

    public void drop(){
      cdsSet();
      cdsAdd(G.DOWN);
//      if(collisionDetected()){return;}
      // when stop dropping, copy to the well, drop a new shape
      if(collisionDetected()){
        copyToWell();
        zapWell();
        dropNewShape();
      }else{
        loc.add(G.DOWN);
      }
    }

    public static void dropNewShape() {
      shape = shapes[G.rnd(7)];
      shape.loc.set(4, 0); //set the shape int the middle
    }

    public void copyToWell() {
      for(int i=0;i<4;i++){
        well[a[i].x+loc.x][a[i].y+loc.y] = iColor;
      }
    }

    public void slide(G.V dx){
      cdsSet();//copy things to collision detection
      cdsAdd(dx);
      if(collisionDetected()){return;}
//      cdsGet();
      loc.add(dx);
    }
    public void show(Graphics g){
      g.setColor(colors[iColor]);
      for(int i=0;i<4;i++){
        g.fillRect(x(i), y(i), C, C);
      }
      g.setColor(Color.BLACK);
      for(int i=0;i<4;i++){
        g.drawRect(x(i), y(i), C, C);
      }
    }

    public int x(int i){return xM+C*(a[i].x+loc.x);}
    public int y(int i){return yM+C*(a[i].y+loc.y);}


  }
}
