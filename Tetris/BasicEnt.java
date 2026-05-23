import java.util.Random;

//Basic abstract class for movable objects

public abstract class BasicEnt {

    public abstract void setX(int index, int x);
    public abstract void setY(int index, int y);
    public abstract int x(int index);
    public abstract int y(int index);

    public abstract int minY();
}
