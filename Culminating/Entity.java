import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Entity extends Rectangle {
    public int xvel;
    
    public Entity(int x, int y, int w, int h) {
        super(x,y,w,h);
    }
    
    public int getXVel() { return this.xvel; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getXPos() { return this.x; }
    public int getYPos() { return this.y; }
    public void setXVel(int v) { this.xvel = v; }
}
