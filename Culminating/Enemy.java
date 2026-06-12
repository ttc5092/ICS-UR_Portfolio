import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Enemy extends Entity {
    private final int oldx, oldy;
    private int pdist;
    private boolean alive = true;
    private int type;
    
    public Enemy(int x, int y, int typ, int pdist) {
        super(x,y,32,32);
        switch(typ) {
            case 0: //basic "Goomba"
                xvel = 2;
                width = 32;
                height = 32;
                break;
            case 1: //Bullet Bill
                xvel = 5;
                width = 32;
                height = 32;
                break;
            case 2: //Fast Bullet Bill
                xvel = 10;
                width = 32;
                height = 32;
                break;
            case 3: //Turtle
                xvel = 3;
                width = 32;
                height = 64;
                break;
            case 4: //Spiny
                xvel = 3;
                width = 32;
                height = 32;
                break;
        }
        this.oldx = x;
        this.oldy = y;
        this.type = typ;
        this.xvel = xvel;
        this.pdist = pdist;
    }
    
    public boolean isAlive() {return this.alive;}
    public void kill() {this.alive = false;}
    public int getType() {return this.type;}
    
    public int getMaxPX() { return this.oldx+this.pdist; }
    public int getMinPX() { return this.oldx-this.pdist; }
}
