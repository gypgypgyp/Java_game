package games;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Cryptogram extends games.Window {

  // have a list of cells
  public static Cell.List cells = new Cell.List();
  static Font font = new Font("Verdana", Font.PLAIN, 20); // set the font
  public static final int xM = 50, yM = 50; // margins
  public static final int LINEGAP = 10, W = 20, H = 45; // if spread multiple lines of cells, create a space between lines

  public static final int EOL = 850; //end of line
  //find the baseline to input text. calculated from the cell size and the y distance.字母与cell的y轴距离
  public static final int DCODE = 18, DGUESS = 40; // delta code and guess from the cell corner
  public static G.V SPACE = new G.V(W, 0);//use for space char
  public static G.V START = new G.V(xM, yM);// where the whole phrase starts
  public static G.V NEWLINE = new G.V(0, LINEGAP+H);// for the new line
  public Cryptogram(){
    super("Cryptogram", 1000, 400);

//    // put char in the cell list
//    Cell c = new Cell(Pair.alpha[0]);
////    cells.add(c);
////    cells.add(new Cell(Pair.alpha[3]));
//    new Cell(Pair.alpha[3]);
//    c.p.guess = 'Z';  // assuming user has a guessed value
//    Cell.newLine();
////    cells.add(new Cell(Pair.alpha[0]));
//    new Cell(Pair.alpha[0]);
//
//    Cell.selected = c; // set the selected cell as the first one we have on the list
    loadQuote("NOW IS THE TIME FOR ALL GOOD MEN TO COME TO THE AID OF THE PARTY");

  }

  //quotation
  public void loadQuote(String q){
    Cell.init();
    Pair.init();
    for(int i = 0; i < q.length(); i++){
      char c = q.charAt(i);
      int iAlpha = c-'A'; //index on the alphabet
      if(c>='A' && c<='Z'){
        new Cell(Pair.alpha[iAlpha]);
      }else{
        Cell.space();
        if(Cell.nextLoc.x > EOL){
          Cell.newLine();
        }
      }
    }
  }

  public void paintComponent(Graphics g){
    G.fillBackGround(g);
    g.setFont(font);
    // fill the cells
    cells.show(g);
  }

 //(for mouse reaction)
  public void mouseClicked(MouseEvent e){
    int x = e.getX(), y = e.getY();
    Cell.selected = cells.hit(x, y);
    repaint();
  }

  public void keyTyped(KeyEvent ke){
    char c = ke.getKeyChar();
    //convert lowercase to uppercase
    if(c >= 'a' && c <= 'z'){
      c = (char)(c-'a'+'A'); //character orithmetic
    }

    //whether have selected cell
    if(Cell.selected != null){
      Cell.selected.p.setGuess((c>='A' && c<='Z')? c : ' ');
    }
    repaint();
  }

  public void keyPressed(KeyEvent ke){
    int vk = ke.getKeyCode(); //virtual key
    if(Cell.selected != null) {// if have a cell selected
      System.out.println(Cell.selected.ndx);
      if (vk == KeyEvent.VK_LEFT) { // when press left arrow
//        System.out.println("left");
        Cell.select(Cell.selected.ndx-1);
      }
      if (vk == KeyEvent.VK_RIGHT) { // when press right arrow
//        System.out.println("right");
        Cell.select(Cell.selected.ndx+1);
      }
    }
    repaint();
  }

  public static void main(String[] args){
    PANEL = new Cryptogram(); // PANEL static member in Window
    games.Window.launch();
  }

  //-----------------Pair------------------
  public static class Pair{
    // an array to represent alphabet
    public static Pair[] alpha = new Pair[26];
    static{
      for(int i = 0; i < 26; i++){
        alpha[i] = new Pair((char)('A'+i)); //casting the num to char
      }
    }
    public char actual, code, guess = ' ';
    private Pair(char c){actual = c; code = c;}

    public static void init(){
      for(int i = 0; i<26; i++){
        Pair p = alpha[i];
        p.guess = ' ';
        Pair x = alpha[G.rnd(26)];

        //swap code value around
        char c = p.code;
        p.code = x.code;
        x.code = c;
      }
    }

    public void setGuess(char c){
      for(int i = 0; i<26;i++){
        Pair p = alpha[i];

        //if has some other char get the same cypher value
        if(p.guess==c){
          p.guess = ' ';
        }
      }
      guess = c;
    }

  }

  //-----------------Cell------------------
  public static class Cell{
    public static G.V SIZE = new G.V(15, 30); //cell size. use this to make the cell
//    public static final int dCode = 10, dGuess = 20; //find the baseline to input text. calculated from the cell size and the y distance.字母与cell的y轴距离

    //keep track of WHERE the last cell, and know where the next cell is
    public static G.V nextLoc = new G.V(START), nextLine = new G.V(START);

    public G.V loc = new G.V(0,0); //loc of the cell
    public Pair p;
    public int ndx; // index. pointer to keep track of ith object in the list
    public static G.VS selectedVS = new G.VS(0, 0, W, H); //keep track of the selected cell. the size not change. only the location changes
    public static Cell selected = null;
    public Cell(Pair pair){
      p = pair;
      loc = new G.V(nextLoc);
      space();//update next location
      ndx = cells.size(); //size is the next index for cell that will be created
      cells.add(this); //add the cell just constracted
    }

    public static void init(){nextLoc.set(START); nextLine.set(START); cells.clear();}

    // index
    public static void select(int n){
      // whether n is valid
      if(n>=0 && n<cells.size()){selected = cells.get(n);}
    }

    // (for mouse reaction) hit detection
    public boolean hit(int x, int y){
      selectedVS.loc.set(loc);
      return selectedVS.contains(x, y);
      //whether this cell matches the selectedVS
    }

    public void show(Graphics g){
      //test whether the xx is the selected
      if(this == Cell.selected){
        selectedVS.loc.set(loc); // set the selecting cell at this loc
        selectedVS.draw(g, Color.RED);
      }
//      selectedVS.loc.set(loc); //pass in the location of THIS cell
//      selectedVS.fill(g, Color.ORANGE);
      g.setColor(Color.BLACK);
      g.drawString(""+p.code, loc.x, loc.y+DCODE);
      g.drawString(""+p.guess, loc.x, loc.y+DGUESS);

    }

    public static void newLine(){nextLine.add(NEWLINE);nextLoc.set(nextLine);}
    public static void space(){nextLoc.add(SPACE);}
    //------------------------List-------------------
    public static class List extends ArrayList<Cell>{

      //show a list of cells
      public void show(Graphics g){
        for(Cell c: this){ // for the cell in the list
          c.show(g);
        }
        }
      //(for mouse reaction)whether we hit a cell
      public Cell hit(int x, int y){
        // go through the list to find which cell is clicked
        for(Cell c: this){
          if(c.hit(x, y)){return c;}
        }
        return null;
      }
    }
  }
}
