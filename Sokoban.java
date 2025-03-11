package games;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class Sokoban extends games.Window {
  public Board board = new Board();
  public Sokoban(){
    super("Sokoban", 1000, 700);
    board.loadStringArray(lev3);
  }

  public void paintComponent(Graphics g){
    G.fillBackGround(g);
    board.show(g);
  }

  public void keyPressed(KeyEvent ke){
    int vk = ke.getKeyCode(); // virtual key
    if(vk==KeyEvent.VK_LEFT){board.go(G.LEFT);}
    if(vk==KeyEvent.VK_RIGHT){board.go(G.RIGHT);}
    if(vk==KeyEvent.VK_UP){board.go(G.UP);}
    if(vk==KeyEvent.VK_DOWN){board.go(G.DOWN);}
    repaint();
  }

  public void keyTyped(KeyEvent ke){
    char c = ke.getKeyChar();
    if(c==' '){board.reload();}
    if(c==0x00 || c==0x0A){board.nextPuzzle();}
    repaint();
  }

  public static void main(String[] args){
    PANEL = new Sokoban();
    games.Window.launch();
  }

  //------------------board-----------------------
  public static class Board{
    public static String[][] puzzles = {lev1, lev2, lev3};
    public int curPuzzel = 0;
    public static final int N = 25;
    public static String boardStates = " WPCGgE"; //G: EMPTY GOAL STATE // g: THING WE COVER
    //wall, player, container(crates), goaled(where we want to put the container to, E(error))
    public static Color[] colors = {Color.WHITE, Color.DARK_GRAY, Color.GREEN, Color.ORANGE,
        Color.CYAN, Color.BLUE, Color.RED};
    public static final int xM = 50, yM = 50, W = 40;
    public static final G.V dest = new G.V(); //a place to hold the temp destination
    public char[][] b = new char[N][N];
    public G.V person = new G.V();
    public static boolean onGoal = false; // tracks if the player is on goal square
    // when player is standing on a goal, may lose the info of the cell;
    // this is used to keep track of the original goal state

    public Board(){clear();}
    public void nextPuzzle(){
      curPuzzel = (curPuzzel+1)%puzzles.length;
      reload();
    }
    public void reload(){
      clear();
      loadStringArray(puzzles[curPuzzel]);
    }

    public char ch(G.V v){
      // tell the value of the board
      return b[v.x][v.y];
    }

    public void set(G.V v, char c){
      b[v.x][v.y] = c;
    }

    public void movePerson(){ // not safe, does not test for valid destination
      boolean res = ch(dest)=='G';// ch(dest)=='G' : where the person moves to
      set(person, onGoal?'G':' ');
      set(dest, 'P');
      person.set(dest);
      onGoal = res;
    }

    public void go(G.V dir){
      //set the destination vector(where the person want to move to)
      dest.set(person);
      dest.add(dir);
      //if person want to go to wall or error
      if(ch(dest)=='W' || ch(dest)=='E'){return;}
      if(ch(dest)==' ' || ch(dest)=='G'){movePerson();return;}
      if(ch(dest)=='C' || ch(dest)=='g'){ // attempting to move the container
        dest.add(dir); // dest is now box destination
        if(ch(dest)!=' ' && ch(dest)!='G'){return;} // if cannot move the container
        set(dest, ch(dest)=='G'?'g':'C');//can move container. if G, then it becomes g; otherwise it becomes box
        //move the person
        dest.set(person);//the box square becomes person square
        dest.add(dir);
        set(dest, ch(dest)=='g'?'G':' ');//reset the box
        movePerson();
      }
    }

    public void show(Graphics g){
      for(int c=0; c<N; c++) {
        for (int r=0; r<N; r++) {
          int ndx = boardStates.indexOf(b[c][r]);///////????????????
          g.setColor(colors[ndx]);
          g.fillRect(xM+c*W, yM+r*W, W, W);
        }
      }
    }
    public void clear(){
      for(int i=0; i<N; i++){
        for(int j=0; j<N; j++){
          b[i][j] = ' ';
        }
      }
    }

    public void loadStringArray(String[] a){
      person.set(0,0);
      for(int r=0; r<a.length; r++){
        String s = a[r];
        for(int c=0; c<s.length(); c++){
          char ch = s.charAt(c);
          b[c][r] = (boardStates.indexOf(ch) > -1)? ch : 'E'; //if the ch is in the boardStates
          if(ch=='P' && person.x == 0){
            person.x = c;
            person.y = r;
          }
        }
      }
    }
  }

  public static String[] lev1 = {
      "  WWWWW",
      "WWW   W",
      "WGPC  W",
      "WWW CGW",
      "WGWWC WW",
      "W W G WW",
      "WC gCCGW",//g for filled goal
      "W   G  W",
      "WWWWWWWW"
  };

  public static String[] lev2 = {
      "    WWWWW",
      "    W   W",
      "    WC  W",
      "  WWW  CWW",
      "  W  C C W",
      "WWW W WW W   WWWWWW",
      "W   W WW WWWWW  GGW",
      "W C  C          GGW",
      "WWWWW WWW WPWW  GGW",
      "    W     WWWWWWWWW",
      "    WWWWWWW        ",
  };

  public static String[] lev3 = {
      "WWWWWWWWWWWW  ",
      "WGG  W     WWW",
      "WGG  W C  C  W",
      "WGG  WCWWWW  W",
      "WGG    P WW  W",
      "WGG  W W  C WW",
      "WWWWWW WWC C W",
      "  W C  C C C W",
      "  W    W     W",
      "  WWWWWWWWWWWW",
  };

}
