import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class MiniMario extends JPanel implements ActionListener, KeyListener {
    // Window settings
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Game logic timer
    private Timer timer;

    // Player variables
    private Rectangle player;
    private double playerX = 100;
    private double playerY = 400;
    private double velocityX = 0;
    private double velocityY = 0;
    private final double GRAVITY = 0.8;
    private final double JUMP_STRENGTH = -15;
    private final double SPEED = 5;
    private boolean isJumping = false;

    // Input flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Level elements
    private ArrayList<Rectangle> platforms;
    private ArrayList<Rectangle> coins;
    private Rectangle enemy;
    private double enemyVelocityX = 2;
    private boolean enemyAlive = true;

    // Game state
    private int score = 0;
    private int cameraX = 0;
    private boolean gameOver = false;

    public MiniMario() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 235)); // Sky blue
        setFocusable(true);
        addKeyListener(this);

        initLevel();

        // 60 FPS Game Loop
        timer = new Timer(16, this);
        timer.start();
    }

    private void initLevel() {
        player = new Rectangle((int)playerX, (int)playerY, 30, 40);
        platforms = new ArrayList<>();
        coins = new ArrayList<>();

        // Floor
        platforms.add(new Rectangle(-500, 500, 3000, 100));
        
        // Floating platforms
        platforms.add(new Rectangle(300, 400, 100, 20));
        platforms.add(new Rectangle(500, 300, 100, 20));
        platforms.add(new Rectangle(750, 250, 150, 20));

        // Coins
        coins.add(new Rectangle(335, 360, 20, 20));
        coins.add(new Rectangle(535, 260, 20, 20));
        coins.add(new Rectangle(800, 210, 20, 20));
        coins.add(new Rectangle(600, 460, 20, 20));

        // Enemy (patrols the floor)
        enemy = new Rectangle(600, 470, 30, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        updatePlayer();
        updateEnemy();
        checkCollisions();
        updateCamera();

        repaint();
    }

    private void updatePlayer() {
        // Horizontal movement
        if (leftPressed) velocityX = -SPEED;
        else if (rightPressed) velocityX = SPEED;
        else velocityX = 0;

        playerX += velocityX;

        // Vertical movement (Gravity)
        velocityY += GRAVITY;
        playerY += velocityY;

        player.setLocation((int) playerX, (int) playerY);
    }

    private void updateEnemy() {
        if (enemyAlive) {
            enemy.x += enemyVelocityX;
            // Simple patrol logic: reverse direction if it moves too far
            if (enemy.x > 800 || enemy.x < 500) {
                enemyVelocityX *= -1;
            }
        }
    }

    private void checkCollisions() {
        // Platform collisions (Simplified for top-down landing)
        isJumping = true; // Assume falling until we hit a floor
        for (Rectangle platform : platforms) {
            if (player.intersects(platform)) {
                // If falling and hitting the top of a platform
                if (velocityY > 0 && playerY + player.height - velocityY <= platform.y) {
                    playerY = platform.y - player.height;
                    velocityY = 0;
                    isJumping = false;
                }
            }
        }
        player.setLocation((int) playerX, (int) playerY);

        // Coin collection
        Iterator<Rectangle> iter = coins.iterator();
        while (iter.hasNext()) {
            Rectangle coin = iter.next();
            if (player.intersects(coin)) {
                iter.remove();
                score += 10;
            }
        }

        // Enemy collision
        if (enemyAlive && player.intersects(enemy)) {
            // Check if player stomped the enemy (falling from above)
            if (velocityY > 0 && playerY + player.height - velocityY <= enemy.y) {
                enemyAlive = false;
                velocityY = JUMP_STRENGTH / 2; // Bounce off enemy
                score += 50;
            } else {
                // Player got hit
                gameOver = true;
            }
        }

        // Death by falling
        if (playerY > HEIGHT) {
            gameOver = true;
        }
    }

    private void updateCamera() {
        // Keep the player roughly in the center of the screen
        cameraX = (int) playerX - (WIDTH / 2) + (player.width / 2);
        
        // Prevent camera from scrolling past the starting line
        if (cameraX < 0) cameraX = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Apply camera offset
        g2d.translate(-cameraX, 0);

        // Draw Platforms
        g2d.setColor(new Color(34, 139, 34)); // Forest Green
        for (Rectangle platform : platforms) {
            g2d.fillRect(platform.x, platform.y, platform.width, platform.height);
        }

        // Draw Coins
        g2d.setColor(Color.YELLOW);
        for (Rectangle coin : coins) {
            g2d.fillOval(coin.x, coin.y, coin.width, coin.height);
        }

        // Draw Enemy
        if (enemyAlive) {
            g2d.setColor(Color.RED);
            g2d.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }

        // Draw Player
        g2d.setColor(Color.BLUE);
        g2d.fillRect((int)playerX, (int)playerY, player.width, player.height);

        // Remove camera offset to draw UI elements (Score)
        g2d.translate(cameraX, 0);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);

        if (gameOver) {
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            g2d.setColor(Color.RED);
            g2d.drawString("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2);
        }
    }

    // Input Handling
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_SPACE && !isJumping) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Main Method to run the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Java Swing Platformer");
        MiniMario game = new MiniMario();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}