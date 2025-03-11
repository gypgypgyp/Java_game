package games;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class Skunk extends Window {
  public static String AiName= "Archie";
  public static G.Button.List cmds = new G.Button.List();//command
  public static G.Button PASS = new G.Button(cmds, "Pass"){public void act(){pass();}};
  public static G.Button ROLL = new G.Button(cmds, "Roll"){public void act(){roll();}};
  public static G.Button AGAIN = new G.Button(cmds, "Again"){public void act(){again();}};


//  static{PASS.set(100,100);ROLL.set(150,100);}

  public static int M = 0, E = 0, H = 0; // scores  my, enemy,hand
  public static Boolean myTurn = true;
  public static int D1, D2; //dice values
  public static int xM = 50, yM = 50; //margins
  public static void roll(){
    rollDice();
    analyseDice(); // update the scores
  }


  public static void rollDice(){
    D1 = 1+G.rnd(6);
    D2 = 1+G.rnd(6);
  }

  public static String skunkMessage;
  public static void analyseDice(){
    PASS.enabled = true; ROLL.enabled=true;
    if(D1==1&&D2==1){totalSkunked();skunkMessage=" Totally skunked!"; return;}
    if(D1==1||D2==1){skunked();skunkMessage="  Skunked.";return;}
    normalHand();
    skunkMessage="";
  }
  public static void totalSkunked(){if(myTurn){M=0;}else{E=0;}skunked();}
  public static void skunked(){H=0;ROLL.enabled=false;}
  public static void normalHand(){
    H+=D1+D2;
    if(isGameOver()){
      skunkMessage = playerName()+"WIN!";
      gameOver();
    }else{
      if(!myTurn&&ROLL.enabled){setAiButton();}
    }
  }

  public static void setAiButton(){
    if(gottaRoll()){PASS.enabled=false;}
    else{ROLL.enabled=false;}
  }
  public static boolean gottaRoll(){return H<=20;}
  public static boolean isGameOver(){return 100<=H+(myTurn?M:E);}
  public static void gameOver(){PASS.set(-100,-100);ROLL.set(-100,-100);AGAIN.set(100,100);}
  public static void showRoll(Graphics g){
    g.setColor(Color.BLACK);
    String str = playerName() + " roll: " + D1+", "+D2+skunkMessage;
    g.drawString(str, xM, yM);
  }

  public static String playerName(){return myTurn?"Your":AiName+"'s";}


  public static void showScore(Graphics g){
    g.setColor(Color.BLACK);
    g.drawString(scoreString(), xM, yM + 40);
  }

  public static String scoreString(){return (" Hand: " + H + "   Your score: "+M+" "+AiName+"'s score: " + E);}

  public static void pass(){
    if (myTurn){M+=H;}else{E+=H;}
    H=0;
    myTurn = !myTurn;
    roll();
  }
  public static void again(){
    M = 0; E = 0; H = 0;
    myTurn = (G.rnd(2) == 0);
    PASS.set(100,100);ROLL.set(150,100);AGAIN.set(-100, -100);
    roll();
  }

  public Skunk(){
    super("Skunk", 1000,700);
    again();
  }
  public static int nConverge = 0;
  public void paintComponent(Graphics g){
    G.fillBackGround(g);
    if(showStrategy){
//      converge(100_000);
      showStrategy(g);
      System.out.println(""+ ++nConverge + "  " + P[90][0][10] + "  " + p(90,0,10));
    }else{
      //    PASS.show(g);
      cmds.show(g);
      showRoll(g);
      showScore(g);
    }
  }

  public void mousePressed(MouseEvent me){
    int x = me.getX(), y = me.getY();
    if(cmds.clicked(x,y)){repaint();return;}
  }

  public static void main (String[] args){
    PANEL = new Skunk();
    Window.launch();
  }
//------------------------AI------------------------
  public static double[][][] P= new double[100][100][100]; //probability table
  static{
    for(int m=0;m<100;m++){
      for(int e=0;e<100;e++){
        for(int h=0;h<100;h++){
          P[m][e][h] = G.rnd(1000)/1000.0;
        }
      }
    }
  }
  public static double p(int m, int e, int h){ //my score, enemy score, hand
    if (m+h>=100){return 1.0;} //you've won
    if (e>=100){return 0.0;} //you've lost
    return P[m][e][h];
  }
  public static double wPass(int m, int e, int h){ //probability of winning if pass the dices
    return 1.0-p(e,m+h,0); //1-probability of enemy winning
  }
  public static double wTS(int m, int e, int h){ //when I am totally skunked
    return 1.0-p(e,0,0);
  }
  public static double wS(int m, int e, int h){ //skunked
    return 1.0-p(e,m,0);
  }
  //mixed states. calculate the weight of getting different numbers of rolling dices
  public static double[] w = {1.0/36, 2.0/36, 3.0/36, 4.0/36, 5.0/36,
                              4.0/36, 3.0/36, 2.0/36, 1.0/36};
  public static double wR(int m, int e, int h){
    double res = wTS(m,e,h)/36 + wS(m,e,h)/3.6;
    System.out.println(res);
    for(int i=0;i<9;i++){
//      res += p(m,e,h+i+4) * w[i];
      int newHandValue = h + i + 2; // Corrected from h + i + 4
      if (newHandValue < 100) { // Ensure we don't exceed game limits
        res += p(m, e, newHandValue) * w[i];
      }
    }
    System.out.println(res);
    return res;
  }
  public static boolean shouldPass = true; //side effect of wOptimal
  public static double wOptimal(int m, int e, int h){ //probability of winning when take optimal choice
    double wp = wPass(m,e,h), wr = wR(m,e,h); //winning probability
    boolean b = wp>wr;
    System.out.println(""+b);
    return (shouldPass == (wp>wr)) ? wp : wr;
  }
  public static void converge(int n){
    for(int i=0;i<n;i++){
      int m = G.rnd(100), e = G.rnd(100), h = G.rnd(100-m);
      P[m][e][h] = wOptimal(m,e,h);
    }
  }

  //visualize the calculation.
  public static final int W = 7; //width of cell
  public static boolean showStrategy = true; //for testing
  public static void showStrategy(Graphics g){showStops(g);showGrid(g);showColorMap(g);}
  public static final int nC = 45; //number of colors
  public static Color[] stopColors = new Color[nC];
  static {for(int i=0; i<nC; i++){stopColors[i]=new Color(G.rnd(255), G.rnd(255), G.rnd(255));}}
  public static void showColorMap(Graphics g){
    int x = xM+100*W+30;
    for(int i=0; i<nC; i++){
      g.setColor(stopColors[i]);
      g.fillRect(x,yM+15*i,15,13);
      g.setColor(Color.BLACK);
      g.drawString(""+i, x+20, yM+15*i+10);
    }
  }
  public static void showGrid(Graphics g){
    g.setColor(Color.BLACK);
    for(int k=0;k<=10;k++){
      int d = 10*W*k;
      g.drawLine(xM,yM+d,xM+100*W,yM+d);
      g.drawLine(xM+d,yM,xM+d,yM+100*W);
    }
  }
  public static void showStops(Graphics g){
    for(int m=0;m<100;m++){
      for(int e=0;e<100;e++){
        int k = firstStop(m,e);
        g.setColor(stopColors[k]);
        g.fillRect(xM+W*m,yM+W*e,W,W);
      }
    }
  }
  public static int firstStop(int m, int e){
    for(int h=0;h<100-m;h++){
      wOptimal(m,e,h);
      if(shouldPass){
        return (h>=nC) ? 0 : h;
      }
    }
    return 0;
  }

}
