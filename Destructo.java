package games;
import games.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.Timer;

// can try animation the cells dropping down by using flyingboxes.
// when the dropping box reach the location, it stops

public class Destructo extends games.Window implements ActionListener {
  public int bricksRemaining;
  public static Timer timer; // for the animation.
  public static final int nC = 13, nR = 15; //number of collums and rows
  public int[][] grid = new int[nC][nR]; // create a grid, because nC is x and nR is y cordinates
  public static Color[] color = {Color.LIGHT_GRAY, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.PINK};

  // random num generator
  public static Random RANDOM = new Random();
  public static int rnd(int k){return RANDOM.nextInt(k);}


  public Destructo(){
    super("Destructo", 1000, 700);
    rndColors(4); // test in the output if a grid of rand num is printed
    timer = new Timer(30, this); //30 frames per second
    timer.start();
    initRemaining();
  }

  public void createNewGame(){
    rndColors(3+rnd(3));
//    rndColors(2);
    initRemaining();
    xM = 100;
  }

  //paint the grid
  public void paintComponent(Graphics g){
    g.setColor(color[0]);
    g.fillRect(0,0,5000,5000);
    showGrid(g);
    bubbleSort();
    if(slideCol()){xM+=w/2;}//slides the grid towards the center, but not to the left
    showRemaining(g);
  }
  // window has many panles, and paint component is inside the panel class (JPanel that extended by the Window)
  // if use @override, compiler will . but now IDE will warn if the method is very close to but not the same as what you want.


  // mouse click routine
  public void mousePressed(MouseEvent me){
    int x = me.getX(), y = me.getY();
    if(x < xM || y < yM){return;} // if x and y less than margin
    int r = r(y), c = c(x);
    if(r < nR && c < nC){rcAction(r, c);} // legal x and y, do the action
    repaint(); // so that the changed color will show on the window
  }

  // keyboard to stop the game
  public void keyPressed(KeyEvent me){
    createNewGame();
    repaint();
  }

  public void rcAction(int r, int c){
//    grid[c][r] = 0; // stub. test for the mousePressed event.
    if(infectable(c, r)){infect(c, r, grid[c][r]);}
//    bubbleSort();
  }

  // can we only check the bottom row?
  // bubbling is in the time routine, so not sure when to check the bottom
  public boolean colIsEmpty(int c){
    for(int r = 0; r < nR; r++){
      if(grid[c][r] != 0 ){return false;}
    }
    return true;
  }

  // swap colums
  // c is non-empty, c-1 is empty
  public void swapCol(int c){
    for(int r = 0; r < nR; r++){
      grid[c-1][r] = grid[c][r];
      grid[c][r] = 0;
    }
  }

  // go through each col and slide col
  public boolean slideCol(){
    boolean res = false;
    for(int c=1; c < nC; c++){
      if(colIsEmpty(c-1) && !colIsEmpty(c)){swapCol(c);res = true;}
    }
    return res;
  }

  //set random colors fill in the grid
  // k = number of colors, [3, 6)
  public void rndColors(int k){
    for (int c = 0; c < nC; c++){
      for (int r = 0; r < nR; r++){
        grid[c][r] = 1 + rnd(k); // skip over 0, so add 1, that the color will differentiate from the background
        System.out.print(" "+ grid[c][r]);// 不换行
      }
      System.out.println(); // 换行
    }
  }

  // the neighbor cells that are infected by the clicked cell
  public void infect(int c, int r, int v){ // first value of the cell clicked on
    if(grid[c][r] != v){return;}

    // the only place we set cell to 0
    grid[c][r] = 0; // set the cell to background color
    bricksRemaining --;
    if (r > 0) {infect(c, r-1, v);} // subtract
    if (c > 0) {infect(c-1, r, v);}
    if (r < nR - 1) {infect(c, r+1, v);}
    if (c < nC - 1) {infect(c+1, r, v);}
  }

  // looks for any of neighbors that match the color
  public boolean infectable(int c, int r){
    int v = grid[c][r];
    if(v == 0){return false;}
    if(r > 0){if (grid[c][r-1] == v){return true;}}
    if(c > 0){if (grid[c-1][r] == v){return true;}}
    if(r < nR-1){if (grid[c][r+1] == v){return true;}}
    if(c > nC-1){if (grid[c+1][r] == v){return true;}}
    return false;
  }

  // bubble routine, shrink the cells, make the colored cell drop down
  public boolean bubble(int c){
    boolean res = false;
    for(int r = nR-1; r > 0; r--){// go through all the r values in the col, from bottom (larger row value) to the top
      if(grid[c][r] == 0 && grid[c][r-1] != 0){ // when do a swap
        grid[c][r] = grid[c][r-1];
        grid[c][r-1] = 0;
        res = true;
      }
    }
    return res;
  }

  public void bubbleSort(){
    for(int c = 0; c < nC; c++){
      // do the bubble for each col
//      while(bubble(c)){} // while loop not do anything. bubble has done the swap and decide whether to continusely do it
      bubble(c);
    }
  }

  //init the remaining number of cells
  public void initRemaining(){
    bricksRemaining = nR * nC;
  }

  //shows the bricks of remaining
  public void showRemaining(Graphics g){
    String str = "bricks remaining: " + bricksRemaining;
    if(noMorePlays()){str += " game over";}
    g.setColor(Color.BLACK);
    g.drawString(str, 50, 25);
  }

  public boolean noMorePlays(){
    for(int r = 0; r<nR; r++){
      for(int c = 0; c<nC; c++){
        if(infectable(c, r)){return false;}
      }
    }
    return true;
  }

  // show the gird
  public int w = 50, h = 30;
  public int xM = 100, yM = 100; //x margin and y margin

  // convert between x y and col row
  public int x(int c){return xM + c*w;}
  public int y(int r){return yM + r*h;}
  public int c(int x) {return (x - xM)/w;} //do truncation. negative nums also truncated to 0.
  public int r(int y) {return (y - yM)/h;}

  public void showGrid(Graphics g){
    for (int c = 0; c < nC; c++){
      for (int r = 0; r < nR; r++){
        g.setColor(color[grid[c][r]]);
        g.fillRect(x(c), y(r), w, h);
      }
    }
  }

  public static void main(String[] args){
    Window.PANEL = new Destructo();
    Window.launch();
  }

  @Override
  public void actionPerformed(ActionEvent e) { // for the animation
    repaint();
  }
}
