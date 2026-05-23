import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The Board class handles the main game loop, rendering, input listening,
 * and game state (collision, lines cleared, leveling).
 */
public class Board extends JPanel implements ActionListener {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int INITIAL_DELAY = 400; // Base speed in milliseconds
    //Declare variables and constants
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int level = 1;
    private int score = 0;
    private int highscore = 0;
    private int curX = 0;
    private int curY = 0;
    private Shape curPiece;
    private Shape.Tetrominoes[] board;
    private Tetris parent;
    private PlayAudio pl;

    public Board(Tetris parent) {
        this.parent = parent;
        initBoard();
    }

    private void initBoard() {
        setFocusable(true);
        addKeyListener(new TAdapter());
        board = new Shape.Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        //initialize board
    }

    public void start() {
        //start of game
        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        //level = parent.getScoreManager().loadScore(parent.getCurrentPlayer(), 2);
        highscore = parent.getScoreManager().loadScore(parent.getCurrentPlayer(), 1);
        level = 1;
        score = 0;
        clearBoard();
        updateStatusBar();
        newPiece();
        timer = new Timer(INITIAL_DELAY, this);
        pl = new PlayAudio();
        timer.start();
    }

    private void pause() { //pause screen
        if (!isStarted) return;
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game paused. Press P to resume.");
            parent.getStatusBar().setText(" GAME PAUSED. Press P to resume.");
        } else {
            timer.start();
            updateStatusBar();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();
        //draw the board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Shape.Tetrominoes shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                //if (shape != Shape.Tetrominoes.NoShape) {
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
                //}
            }
        }

        if (curPiece.getShape() != Shape.Tetrominoes.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
            }
        }
    }

    //features to drop pieces and clear board
    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            newY--;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Shape.Tetrominoes.NoShape;
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
        curPiece = new Shape();
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Shape.Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            parent.getStatusBar().setText(" Game Over | Final Score: " + score);
            parent.getScoreManager().saveScore(parent.getCurrentPlayer(), score, level);
            JOptionPane.showMessageDialog(this, "Game Over!\nScore saved.");
        }
    }
    
    public void manualSave() {
        parent.getScoreManager().saveScore(parent.getCurrentPlayer(), score, level);
        JOptionPane.showMessageDialog(this, "Score saved.\nYou may now close the game.");
    }
    
    //attempt to move piece (check if touching ground physics)
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Shape.Tetrominoes.NoShape) {
                return false;
            }
        }
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;
        //check for filled lines and clear them
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Shape.Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            score += (numFullLines * 100) * level; 
            parent.getStatusBar().setText(" LINE CLEARED! ");
            // Difficulty Scaling: Level up every 5 lines
            level = (numLinesRemoved / 5) + 1;
            
            // Speed up the timer as level increases (capping at 50ms to keep it playable)
            int newDelay = Math.max(50, INITIAL_DELAY - (level * 35));
            timer.setDelay(newDelay);

            isFallingFinished = true;
            curPiece.setShape(Shape.Tetrominoes.NoShape);
            repaint();
        }
        updateStatusBar();
    }

    private void updateStatusBar() {
        parent.getStatusBar().setText(" Player: " + parent.getCurrentPlayer() + 
                " | " + score + " | HIGH " + highscore);
    }

    //draws individual tiles
    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoes shape) {
        Color[] colors = {new Color(60,60,102), new Color(240, 60, 60), //fixed colours to make game look better
                new Color(120, 200, 60), new Color(102, 120, 240),
                new Color(240, 200, 60), new Color(180, 102, 240),
                new Color(60, 180, 240), new Color(240, 120, 0)};
                
        Color[] colors_alt = {new Color(240,240,240), new Color(200, 60, 60), //fixed colours to make game look better
                new Color(120, 200, 60), new Color(102, 120, 240),
                new Color(240, 200, 60), new Color(180, 102, 240),
                new Color(60, 180, 240), new Color(240, 120, 0)};

        Color color = colors[shape.ordinal()];
        if(isFallingFinished) color = colors_alt[shape.ordinal()]; //flash screen when clearing a line
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    @Override //Determine if piece is being soft dropped or is done falling
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private int squareWidth() { return (int) getSize().getWidth() / BOARD_WIDTH; }
    private int squareHeight() { return (int) getSize().getHeight() / BOARD_HEIGHT; }
    private Shape.Tetrominoes shapeAt(int x, int y) { return board[(y * BOARD_WIDTH) + x]; }

    class TAdapter extends KeyAdapter { //key detector
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted || curPiece.getShape() == Shape.Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_P) {
                pause();
                return;
            }
            if (isPaused) return;

            switch (keycode) { //process keycode commands to allow controls to work
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY); // Soft drop alternative
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_D:
                    oneLineDown();
                    break;
                case KeyEvent.VK_S:
                    manualSave();
                    break;
            }
        }
    }
}
