package games;

//import reaction.Gesture;

import java.awt.*;

// I'm nothing but abstract functions, a list of AF
// abstract class is half interface & half function

public interface I {
  public interface Area{
    public boolean hit(int x, int y);
    // function signature with no body no implementation
    public void dn(int x, int y);    // dn for down
    public void up(int x, int y);
    public void drag(int x, int y);
  }

  public interface Show{public void show(Graphics g);}
//  public interface Act{public void act(Gesture g);}    // Gesture in reaction
//  public interface React extends Act {public int bid(Gesture g);}
}
