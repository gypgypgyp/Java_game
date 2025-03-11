package games;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.Timer;

public class Snake extends Window implements ActionListener {
  public static Timer timer;

  public static Color cFood = Color.GREEN, cSnake = Color.BLUE, cBad = Color.RED; //colors. hangaria
  public static Cell crash = null; // when snake crash to itself
  public static Cell food = new Cell();
  public static Cell.List snake = new Cell.List();
  public Snake(){
    super("Snake", 1000, 700);
    startGame();
    timer = new Timer(200,this); // listener is snake(this)
    timer.start();

  }

  public void startGame(){
    snake.stop();
    snake.iHead = 0;
    snake.clear();
    snake.growList();
    food.rndLoc(); //change the place of food
    crash = null; //put this line at the end. if put this at first and without stoppd, will come upt race condition. if time is up

  }

  public static void moveSnake(){ // checks everything
    if(crash!=null){return;} // not move the snake if there is a crash
    snake.move();
    Cell head = snake.head();
    if(head.hits(food)){snake.growList();food.rndLoc();return;}
    if(!head.inbounds()){crash = head; snake.stop();return;}
    if(snake.hits(head)){crash = head; snake.stop();return;}

  }

  public void paintComponent(Graphics g){
    G.fillBackGround(g);
//    g.getColor(Color.RED);
//    g.fillRect(100,100,100,100);
    g.setColor(cSnake);
    snake.show(g);
    g.setColor(cFood);
    food.show(g);
    Cell.drawBoundary(g);

    if (crash != null){g.setColor(cBad);crash.show(g);}
  }

  public void keyPressed(KeyEvent ke){
    int vk = ke.getKeyCode(); //virtual key equal to
    //keytyped will get characters, nut not keyPressed
    if(vk==KeyEvent.VK_LEFT){snake.direction = G.LEFT;}
    if(vk==KeyEvent.VK_RIGHT){snake.direction = G.RIGHT;}
    if(vk==KeyEvent.VK_UP){snake.direction = G.UP;}
    if(vk==KeyEvent.VK_DOWN){snake.direction = G.DOWN;}
//    if(vk==KeyEvent.VK_SPACE){moveSnake();} // move the snake
//    if(vk==KeyEvent.VK_A){snake.growList();} // make snake longer
    if(vk==KeyEvent.VK_SPACE){startGame();}
    repaint();
  }

  public static void main(String[] args){
    PANEL = new Snake();
    Window.launch();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    moveSnake();
    repaint();
  }

  //--------------Cell--------------
  public static class Cell extends G.V implements I.Show {
    public static final int xM = 35, yM = 35, nX = 30, nY = 20, W = 30;

    public Cell(){rndLoc();} //give a random location

    public Cell(Cell c){set(c);} // set cell at a given place

    public void rndLoc(){
      x = G.rnd(nX);
      y = G.rnd(nY);
    }

    public boolean hits(Cell c){return c.x == x && c.y == y;} // if one cell hit to another, they have same coordination
    public boolean inbounds(){return x >= 0 && x < nX && y >= 0 && y < nY;}

    public static void drawBoundary(Graphics g){
      int xMax = xM+nX*W, yMax = yM+nY*W;
      g.setColor(Color.BLACK);
      g.drawLine(xM, yM, xM, yMax);
      g.drawLine(xMax, yM, xMax, yMax);
      g.drawLine(xM, yM, xMax, yM);
      g.drawLine(xM, yMax, xMax, yMax);
    }

    public void show (Graphics g){
      g.fillRect(xM+x*W, yM+y*W, W, W);
    }

    //-----------List----------------
    public static class List extends ArrayList<Cell>{

      public static G.V STOPPED = new G.V(0,0);
      public static G.V direction = STOPPED;

      public int iHead = 0; // the head of the snake

      public void show(Graphics g){
        for (Cell c:this){c.show(g);}
      }

      // move the snake, only change the head and tail
      public void move(){
        if (direction == STOPPED){return;}
        int iTail = (iHead+1) % size(); //index of tail
        Cell tail = get(iTail); // cell of tail
        tail.set(get(iHead)); // move the tail to the head, becomes the new head
        tail.add(direction); // move the new head one step forward
        iHead = iTail; //move the head to the new head
      }

      public Cell head(){return get(iHead);}
      public void stop(){direction=STOPPED;}
      public boolean hits(Cell t){ // if any of the cell in the list hits the target cell
        for(Cell c : this){
          if( c!=t && c.hits(t)) {return true;} // if 2 cell at same location but not the same object
        }
        return false;
      }
      public void growList(){
        Cell cell = (size()==0)? new Cell() : new Cell(get(0)); //if the list is empty, create a new rnd cell; or get the 0th cell of the list
        add(cell); // add the cell to the list
      }
    }

  }
}