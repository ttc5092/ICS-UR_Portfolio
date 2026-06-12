import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Powerup extends Entity {
    private boolean alive = true;
    private int type;
    
    public Powerup(int x, int y, int typ) {
        super(x,y,32,32);

        this.type = typ;
    }
    
    public boolean isAlive() {return this.alive;}
    public int getType() {return this.type;};
    public int get() {
        this.alive = false;
        return this.type;
    };
}
