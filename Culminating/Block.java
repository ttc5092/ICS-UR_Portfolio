import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Block extends Rectangle {
    private boolean alive = true;
    private int type;
    
    public Block(int x, int y, int typ) {
        super(x,y,32,32);
        this.type = typ;
    }
    
    public boolean isAlive() {return this.alive;}
    public int hit() {
        //if(this.type!=0) Powerup pwr = new Powerup(this.x, this.y-40, this.type); summon powerup if not a coin
        this.alive = false;
        return this.type;
    }
    public int getXPos() { return this.x; }
    public int getYPos() { return this.y; }
}
