import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MiniMario extends JPanel implements ActionListener, KeyListener {
    // Window settings
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Game logic timer
    private Timer timer;

    // Player variables
    private Rectangle player;
    private double playerX = 100;
    private double playerY = 300;
    private double velocityX = 0;
    private double velocityY = 0;
    private final double GRAVITY = 0.8;
    private final double JUMP_STRENGTH = -15;
    private double SPEED = 3;
    private boolean isJumping = false;

    // Input flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean mushroom = false;

    // Level elements
    private ArrayList<Rectangle> platforms;
    private ArrayList<Rectangle> semisolid;
    private ArrayList<Rectangle> bricks;
    private ArrayList<Block> blocks;
    private ArrayList<Rectangle> coins;
    private ArrayList<Enemy> enemies;
    private ArrayList<Powerup> powerups;
    private Rectangle flagpole;
    private int winTick = 0; //level clear timer
    private int star = 0; //star power timer
    private int lives = 3; //Number of lives

    // Game state
    private int score = 0;
    private int world = 1;
    private int level = 1;
    private int cameraX = 0;
    private boolean gameOver = false;
    private boolean victory = false;
    private boolean left = false;
    private boolean pause = true; //Pause at start for title screen
    //Animation state
    private int animTick = 0;
    private int animIndex = 0;
    private int enemyTick = 0;
    private int enemyIndex = 0;
    private final int animSpeed = 15; //shared value
    private String currentPlayer; //username
    
    //Static sprites
    private BufferedImage grassTexture;
    private BufferedImage tileTexture;
    private BufferedImage brickTexture;
    private BufferedImage blockTexture;
    private BufferedImage coinTexture;
    private BufferedImage mushroom1;
    private BufferedImage mushroom2;
    private BufferedImage starTexture;
    private BufferedImage flagTexture;
    private BufferedImage marioTexture;
    private BufferedImage empty;
    //Mario animation
    private BufferedImage[] walkLeft = new BufferedImage[2];
    private BufferedImage[] walkRight = new BufferedImage[2];
    private BufferedImage idleLeft;
    private BufferedImage idleRight;
    private BufferedImage jumpLeft;
    private BufferedImage jumpRight;
    //Enemy animation
    private BufferedImage[] koopaLeft = new BufferedImage[2];
    private BufferedImage[] koopaRight = new BufferedImage[2];
    private BufferedImage[] spinyLeft = new BufferedImage[2];
    private BufferedImage[] spinyRight = new BufferedImage[2];
    private BufferedImage[] goomba = new BufferedImage[2];
    private BufferedImage[] billLeft = new BufferedImage[2];
    private BufferedImage[] billRight = new BufferedImage[2];
    //Fonts
    Font hud, heading;
    private ScoreManager sm = new ScoreManager("./scores.log");
    
    public void loadSprites() {
        try {
            idleLeft = ImageIO.read(new File("./idle0.png"));
            idleRight = flip(idleLeft);
            jumpLeft = ImageIO.read(new File("./jump0.png"));
            jumpRight = flip(jumpLeft);
            //Load all dynamic (animated) sprites
            walkLeft[0] = ImageIO.read(new File("./walk01.png"));
            walkLeft[1] = ImageIO.read(new File("./walk02.png"));
            walkRight[0] = flip(walkLeft[0]);
            walkRight[1] = flip(walkLeft[1]);
            koopaLeft[0] = ImageIO.read(new File("./koopa1.png"));
            koopaLeft[1] = ImageIO.read(new File("./koopa2.png"));
            koopaRight[0] = flip(koopaLeft[0]);
            koopaRight[1] = flip(koopaLeft[1]);
            spinyLeft[0] = ImageIO.read(new File("./spiny1.png"));
            spinyLeft[1] = ImageIO.read(new File("./spiny2.png"));
            spinyRight[0] = flip(spinyLeft[0]);
            spinyRight[1] = flip(spinyLeft[1]);
            goomba[0] = ImageIO.read(new File("./goomba1.png"));
            goomba[1] = ImageIO.read(new File("./goomba2.png"));
            billLeft[0] = ImageIO.read(new File("./bill1.png"));
            billLeft[1] = ImageIO.read(new File("./bill2.png"));
            billRight[0] = flip(billLeft[0]);
            billRight[1] = flip(billLeft[1]);
            //Load all static sprites
            empty = ImageIO.read(getClass().getResource("./empty.png"));
            grassTexture = ImageIO.read(getClass().getResource("./grass.png"));
            tileTexture = ImageIO.read(getClass().getResource("./shroom.png"));
            brickTexture = ImageIO.read(getClass().getResource("./brick.png"));
            blockTexture = ImageIO.read(getClass().getResource("./block.png"));
            coinTexture = ImageIO.read(getClass().getResource("./coin.png"));
            flagTexture = ImageIO.read(getClass().getResource("./flag.png"));
            marioTexture = ImageIO.read(getClass().getResource("./mario_flag.png"));
            mushroom1 = ImageIO.read(getClass().getResource("./mushroom1.png"));
            mushroom2 = ImageIO.read(getClass().getResource("./mushroom2.png"));
            starTexture = ImageIO.read(getClass().getResource("./star.png"));
            //manage fonts
            hud = Font.createFont(Font.TRUETYPE_FONT, new File("nintendo-nes-font.ttf")).deriveFont(20.0f);
            heading = Font.createFont(Font.TRUETYPE_FONT, new File("nintendo-nes-font.ttf")).deriveFont(40.0f);
        } catch (Exception e) {
            System.out.println("Image loading failed.");
            hud = new Font("Arial", Font.BOLD, 24);
            heading = new Font("Arial", Font.BOLD, 48);
            e.printStackTrace();
        }
    }
    
    
    public MiniMario() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(150, 200, 240)); // Sky blue
        setFocusable(true);
        addKeyListener(this);
        // Simple Login System
        currentPlayer = JOptionPane.showInputDialog(this, 
                "Enter username to play:", 
                "Game Login", JOptionPane.QUESTION_MESSAGE).toUpperCase();
        
        if (currentPlayer == null || currentPlayer.trim().isEmpty()) {
            currentPlayer = "MARIO";
        }
        loadSprites();
        initLevel();
        // 60 FPS Game Loop
        timer = new Timer(16, this);
        timer.start();
    }
    
    private void loadSave() { //call ScoreManager methods
        score = sm.loadScore(currentPlayer, 1);
        level = sm.loadScore(currentPlayer, 2);
        //world = sm.loadScore(currentPlayer, 3); //TBA
        JOptionPane.showMessageDialog(this, "Save file loaded for "+currentPlayer);
        pause = false;
        initLevel();
    }

    private void initLevel() {
        player = new Rectangle((int)playerX, (int)playerY, 32, 32);
        platforms = new ArrayList<>();
        semisolid = new ArrayList<>();
        coins = new ArrayList<>();
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        bricks = new ArrayList<>();
        blocks = new ArrayList<>();
        String filename = world+"-"+level+".lvl";
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                
                switch(Integer.parseInt(line[0])) {
                    case 0: //solid platform
                        platforms.add(new Rectangle(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4])));
                        break;
                    case 1: //semisolid platform
                        semisolid.add(new Rectangle(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4])));
                        break;
                    case 2: //enemies
                        enemies.add(new Enemy(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[4]), Integer.parseInt(line[3])));
                        break;
                    case 3: //coins
                        coins.add(new Rectangle(Integer.parseInt(line[1]), Integer.parseInt(line[2]), 32, 32));
                        break;
                    case 4: //? blocks
                        blocks.add(new Block(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3])));
                        break;
                    case 5: //breakable bricks
                        bricks.add(new Rectangle(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4])));
                        break;
                    case 6: //flagpole
                        flagpole = new Rectangle(Integer.parseInt(line[1]), Integer.parseInt(line[2]), 32, 160);
                        break;
                }
            }
            scanner.close();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
        //Physically move player to the start of the level
            playerX = 100;
            playerY = 300;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        
        if (gameOver||pause||victory) return;

        updatePlayer(); //handle player X movement
        updateEnemy(); //handle enemy X movement
        checkCollisions(); //handle Y movement and all collisions
        updateCamera();
    }

    private void updatePlayer() {
        // Horizontal movement
        if (leftPressed) velocityX = -SPEED;
        else if (rightPressed) velocityX = SPEED;
        else velocityX = 0;

        playerX += velocityX;
        player.x = (int) playerX;
        // Vertical movement (Gravity)
        velocityY += GRAVITY; // IF NOT TOUCHING GROUND, GRAVITY CANCELS OUT
        
        //Star power timeout
        if(star>0) star--; //increment the localized counter
        if(star<=0) star = 0; //Power runs out
        
        if (velocityX > 0) {
            left = false;
        } else if (velocityX < 0) {
            left = true;
        }

        if (!isJumping && velocityX != 0 || star>0) {
            animTick++;
            if (animTick >= animSpeed) {
                animTick = 0;
                animIndex++;
                if (animIndex >= walkLeft.length) {
                    animIndex = 0; 
                }
            }
        } else {
            animIndex = 0; // Reset if idle or jumping
        }
    }

    private void updateEnemy() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.setX((int) enemy.getXPos()+enemy.getXVel());
                // Simple patrol logic: reverse direction if it moves too far
                if (enemy.getXPos() > enemy.getMaxPX() || enemy.getXPos() < enemy.getMinPX()) {
                    enemy.setXVel(enemy.getXVel() * -1);
                }
            }
        }
        //global enemy animation updater
        enemyTick++;
        if (enemyTick >= animSpeed) {
            enemyTick = 0;
            enemyIndex = (enemyIndex + 1) % 2;
        }
    }

    private void checkCollisions() {
        //Player updater handles X direction already
        //Handle all X collisions
        for (Rectangle platform : platforms) { //solid platforms
            if (player.intersects(platform)) {
                if (velocityX > 0) {
                    playerX = platform.x - player.width;
                } else if (velocityX < 0) {
                    playerX = platform.x + platform.width;
                }
                velocityX = 0;
                player.x = (int) playerX;
            }
        }
        for (Rectangle platform : bricks) { //breakable platforms
            if (player.intersects(platform)) {
                if (velocityX > 0) {
                    playerX = platform.x - player.width;
                } else if (velocityX < 0) {
                    playerX = platform.x + platform.width;
                }
                velocityX = 0;
                player.x = (int) playerX;
            }
        }
        // Handle Y movement
        playerY += velocityY;
        player.y = (int) playerY; 
        //Solid platforms
        for (Rectangle platform : platforms) {
            if (player.intersects(platform)) {
                if (velocityY > 0) { // Falling onto a floor
                    playerY = platform.y - player.height;
                    velocityY = 0;   // STOP gravity from building terminal velocity while standing
                    isJumping = false;
                } else if (velocityY < 0) { // Hitting a ceiling
                    playerY = platform.y + platform.height;
                    velocityY = 0;   // Stop upward momentum instantly
                }
                player.y = (int) playerY; // Sync back immediately
            }
        }
        // Platform collisions (Semisolid)
        for (Rectangle platform : semisolid) {
            if (player.intersects(platform)) {
                // If falling and hitting the top of a platform
                if (velocityY > 0 && playerY + player.height - velocityY <= platform.y+16) { 
                    playerY = platform.y - player.height;
                    velocityY = 0;
                    isJumping = false;
                }
            }
        }
        for (Block block : blocks) { //?-block
            if (player.intersects(block)) {
                // If falling and hitting the top of a platform
                if (velocityY > 0 && playerY + player.height - velocityY <= block.y+16) {
                    playerY = block.y - player.height;
                    velocityY = 0;
                    isJumping = false;
                }
                if (velocityY <=0 && playerY + player.height - velocityY >= block.y+32) {
                    velocityY = 0;
                    //cant jump through that
                    if(block.isAlive()) {
                        int pwrtype = block.hit(); //makes block inactive
                        if(pwrtype==0) {
                            coins.add(new Rectangle(block.getXPos(),block.getYPos()-32,32,32)); //summon coin if block returns special hit state
                        }else{
                            powerups.add(new Powerup(block.getXPos(),block.getYPos()-32,pwrtype)); //summon powerup otherwise
                        }
                    }
                }
            }
        }
        //breakable platforms
        Iterator<Rectangle> it = bricks.iterator();
        while (it.hasNext()) {
            Rectangle brick = it.next();
            if (player.intersects(brick)) {
                // If falling and hitting the top of a platform
                if (velocityY > 0 && playerY + player.height - velocityY <= brick.y+16) { 
                    playerY = brick.y - player.height;
                    velocityY = 0;
                    isJumping = false;
                }
                //if hit from below
                if (velocityY <=0 && playerY + player.height - velocityY >= brick.y+32) {
                    velocityY = 0;
                    //break bricks sound/animation tba
                    if(mushroom) {
                        it.remove();
                        score += 5;
                    }
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
        for(Enemy enemy : enemies) {
            if (enemy.isAlive() && player.intersects(enemy)) {
                // Stop enemy
                if(star>0) {
                    enemy.kill();
                    score+=50;
                }else if (enemy.getType()!=4 && velocityY > 0 && playerY + player.height - velocityY <= enemy.getYPos()) {
                    velocityY = JUMP_STRENGTH / 2; // Bounce off enemy
                    score += 50;
                    enemy.kill(); //You can kill any enemy except the Spiny Shell
                }else {
                    // Player got hit
                    if(mushroom) {
                        velocityY = JUMP_STRENGTH / 2;
                        mushroom = false;
                    }
                    else gameOver = true;
                }
            }
        }
        //Powerups
        for(Powerup pwr : powerups) {
            if (pwr.isAlive() && player.intersects(pwr)) {
                switch(pwr.get()) { //powerup logic
                    case 0:
                        score+=10;
                        break;
                    case 1:
                        lives++; //1-Up
                        break;
                    case 2: //Super Mushroom
                        score+=100;
                        mushroom = true;
                        break;
                    case 3: //Super Star
                        star = 256; //start timer
                        break;
                }
            }
        }

        // Death by falling
        if (playerY > HEIGHT) {
            gameOver = true;
        }
        
        if(player.intersects(flagpole)) { //VICTORY!
            score += (HEIGHT - player.height); //the higher the player is on the pole, the more bonus points they earn
            victory = true;
        }
    }

    private void updateCamera() {
        // Keep the player roughly in the center of the screen
        cameraX = (int) playerX - (WIDTH / 2) + (player.width / 2);
        
        // Prevent camera from scrolling past the starting line
        if (cameraX < 0) cameraX = 0;
    }

    private void drawTile(Graphics2D g2d, BufferedImage img, Rectangle rect) {
        int tw = img.getWidth();
        int th = img.getHeight();

        for (int x = rect.x; x < rect.x + rect.width; x += tw) {
            for (int y = rect.y; y < rect.y + rect.height; y += th) {
                // Only draw the parts of the image that fit inside the platform's boundaries
                int dw = Math.min(tw, (rect.x + rect.width) - x);
                int dh = Math.min(th, (rect.y + rect.height) - y);
                
                g2d.drawImage(img, x, y, x + dw, y + dh, 
                              0, 0, dw, dh, null);
            }
        }
    }

    private BufferedImage flip(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, src.getType());
        Graphics2D g = flipped.createGraphics();
        g.drawImage(src, w, 0, 0, h, 0, 0, w, h, null);
        g.dispose();
        return flipped;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Apply camera offset
        g2d.translate(-cameraX, 0);

        // Draw Platforms
        //g2d.setColor(new Color(34, 139, 34)); // Forest Green, solid platforms
        for (Rectangle platform : platforms) {
            drawTile(g2d, grassTexture, platform);
            // g2d.fillRect(platform.x, platform.y, platform.width, platform.height); LEGACY
        }
        
        //g2d.setColor(new Color(230, 139, 34)); // Orange, semisolid platforms
        for (Rectangle platform : semisolid) {
            drawTile(g2d, tileTexture, platform);
            //g2d.fillRect(platform.x, platform.y, platform.width, platform.height);
        }
        
        //g2d.setColor(new Color(139, 34, 34)); // Brick Red
        for (Rectangle platform : bricks) {
            drawTile(g2d, brickTexture, platform);
            //g2d.fillRect(platform.x, platform.y, platform.width, platform.height);
        }
        
        //g2d.setColor(new Color(230, 180, 0)); // Bright Yellow
        for (Rectangle block : blocks) {
            drawTile(g2d, blockTexture, block);
            //g2d.fillRect(block.x, block.y, block.width, block.height);
        }

        // Draw Coins
        //g2d.setColor(Color.YELLOW);
        for (Rectangle coin : coins) {
            g2d.drawImage(coinTexture, coin.x, coin.y, coin.width, coin.height, null);
            //g2d.fillOval(coin.x, coin.y, coin.width, coin.height);
        }

        // Draw Enemies
        for(Enemy enemy : enemies) {
            if(!enemy.isAlive())continue; //optimized
            boolean right = enemy.getXVel() > 0;
            BufferedImage frame = empty;
            switch(enemy.getType()) {
                case 0: //goomba
                    frame = goomba[enemyIndex]; //does not flip
                    break;
                case 1: //bullet bill
                    frame = right ? billRight[0] : billLeft[0];
                    break;
                case 2: //fast bullet bill
                    frame = right ? billRight[1] : billLeft[1];
                    break;
                case 3: //turtle
                    frame = right ? koopaRight[enemyIndex] : koopaLeft[enemyIndex];
                    break;
                case 4: //spiny shell
                    frame = right ? spinyRight[enemyIndex] : spinyLeft[enemyIndex];
                    break;
            }
            g2d.drawImage(frame, enemy.x, enemy.y, enemy.width, enemy.height, null);
        }
        //Draw Powerups
        for(Powerup pwr : powerups) {
            if(pwr.isAlive()) {
                switch(pwr.getType()) {
                    case 1:
                        g2d.drawImage(mushroom1, pwr.x, pwr.y, pwr.width, pwr.height, null);
                        break;
                    case 2:
                        g2d.drawImage(mushroom2, pwr.x, pwr.y, pwr.width, pwr.height, null);
                        break;
                    case 3:
                        g2d.drawImage(starTexture, pwr.x, pwr.y, pwr.width, pwr.height, null);
                        break;
                }
            }
        }
        
        //Flagpole
        g2d.setColor(Color.WHITE);
        if(victory) g2d.drawImage(marioTexture, flagpole.x, flagpole.y, flagpole.width, flagpole.height, null);
        else g2d.drawImage(flagTexture, flagpole.x, flagpole.y, flagpole.width, flagpole.height, null);
        //g2d.fillRect(flagpole.x, flagpole.y, flagpole.width, flagpole.height);

        // Draw Player (Animated)
        BufferedImage currentFrame = idleRight;
        if(star>0 && animIndex>0) {
            currentFrame = empty;
        }else{
           if (isJumping) currentFrame = left ? jumpLeft : jumpRight;
           else if (velocityX != 0) currentFrame = left ? walkLeft[animIndex] : walkRight[animIndex];
           else currentFrame = left ? idleLeft : idleRight;
        }
        // Draw the active frame perfectly stretched into the player hitbox
        g2d.drawImage(currentFrame, (int)playerX, (int)playerY, player.width, player.height, null);

        // Remove camera offset to draw UI elements (Score)
        g2d.translate(cameraX, 0);
        g2d.setColor(Color.WHITE);
        g2d.setFont(hud);
        g2d.drawString(currentPlayer+" x " + lives +"  "+String.format("%07d", score), 20, 30);
        if(pause && playerX!=100) g2d.drawString("PAUSED. Press DOWN ARROW to resume.", 20, 110);
         g2d.drawString("LEVEL " + world +"-" + level, 20, 70);
         if(pause  && playerX==100) { //title Screen
            g2d.setColor(new Color(240,180,0));
            g2d.fillRect(0,0,WIDTH, HEIGHT);
            g2d.setFont(heading);
            g2d.setColor(Color.RED);
            g2d.drawString("SUPER MARIO BROS.", WIDTH / 8, HEIGHT / 4);
            g2d.setColor(new Color(240, 240, 200));
            g2d.drawString("SUPER MARIO BROS.", WIDTH / 8 - 5, HEIGHT / 4 -5);
            g2d.setFont(hud);
            g2d.setColor(Color.BLACK);
            g2d.drawString("1-PLAYER GAME", WIDTH / 4 , HEIGHT / 3);
            g2d.drawString("Use ARROWS to move. SPACE to run.", WIDTH / 8 , HEIGHT / 2);
            g2d.drawString("Press DOWN ARROW to pause game.", WIDTH / 8 , HEIGHT / 2+40);
            g2d.drawString("Press S to LOAD SAVED GAME now", WIDTH / 8 , HEIGHT / 2+80);
            g2d.drawString("or when the game is paused.", WIDTH / 8 , HEIGHT / 2+120);
            g2d.drawString("Press S to SAVE when unpaused.", WIDTH / 8 , HEIGHT / 2+160);
            g2d.drawString("Press DOWN ARROW to start", WIDTH / 4 ,(int) (HEIGHT / 1.125));
            g2d.setColor(Color.WHITE); //3D Text effect
            g2d.drawString("1-PLAYER GAME", WIDTH / 4 - 5, HEIGHT / 3 - 5);
            g2d.drawString("Press DOWN ARROW to start", WIDTH / 4 - 5,(int) (HEIGHT / 1.125)- 5);
         }
         
        if (gameOver) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0,0,WIDTH, HEIGHT);
            g2d.setFont(heading);
            g2d.setColor(Color.RED);
            if(lives<=1) {
                lives = 0;
                g2d.drawString("GAME OVER", WIDTH / 4, HEIGHT / 3);
                //actual gameover
                g2d.setColor(Color.WHITE);
                g2d.setFont(hud);
                g2d.drawString("Press DOWN ARROW to try again", WIDTH / 6 ,(int) (HEIGHT / 1.5));
                star = 0;
                level = 1; //reset player
                world = 1;
                score = 0;
            }else{
                //allow respawn
                lives--;
                star = 0; //clear invincibility timer
                initLevel();
                gameOver = false;
            }
        }
        
        if (victory) {
            if(level==5) {
                g2d.setColor(new Color(64,128,0));
                g2d.fillRect(0,0,WIDTH, HEIGHT);
                g2d.setFont(heading);
                g2d.setColor(Color.RED);
                g2d.drawString("SUPER MARIO BROS.", WIDTH / 8, HEIGHT / 4);
                g2d.setColor(new Color(240, 240, 200));
                g2d.drawString("SUPER MARIO BROS.", WIDTH / 8 - 5, HEIGHT / 4 -5);
                g2d.setFont(hud);
                g2d.setColor(Color.BLACK);
                g2d.drawString("THANKS FOR PLAYING", WIDTH / 4 , HEIGHT / 3);
                g2d.drawString("Made by ALEXANDER MAJI", WIDTH / 8 , HEIGHT / 2);
                g2d.drawString("Developed for ICS4UR", WIDTH / 8 , HEIGHT / 2+40);
                g2d.drawString("Grade 12 Computer Science", WIDTH / 8 , HEIGHT / 2+80);
                g2d.drawString("You will return to LEVEL 1-1", WIDTH / 8 , HEIGHT / 2+160);
                g2d.drawString("Press DOWN ARROW to restart", WIDTH / 4 ,(int) (HEIGHT / 1.125));
                g2d.setColor(Color.WHITE); //3D Text effect
                g2d.drawString("THANKS FOR PLAYING", WIDTH / 4 - 5, HEIGHT / 3 - 5);
                g2d.drawString("Press DOWN ARROW to restart", WIDTH / 4 - 5,(int) (HEIGHT / 1.125)- 5);
            }else{
                g2d.setFont(heading);
                g2d.setColor(new Color(50,150,0));
                g2d.drawString("LEVEL CLEAR!", WIDTH / 6, HEIGHT / 2);
            }
            //delay
            winTick++;
            
        }
        if(winTick>100) {
            if(level<5) { //removed code for handling other Levels and Worlds for now
                level++;
                victory = false;
                winTick = 0;
                initLevel();
            } else if (winTick>320){ //too high
                level = 1;
                victory = false;
                winTick = 0;
                initLevel();
            }
            
        }
    }

    // Input Handling
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_SPACE) SPEED = 8; //run if Space pressed
        if ((key == KeyEvent.VK_UP) && !isJumping) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_SPACE) SPEED = 3; //walk if Space released
        if (key == KeyEvent.VK_DOWN) {
            if(gameOver && lives == 0) lives = 4;
            else pause = !pause;
        }
        if(key == KeyEvent.VK_S) {
            if(pause) loadSave(); //load save when paused, save game otherwise.
            else {
                sm.saveScore(currentPlayer, score, level, world);
                JOptionPane.showMessageDialog(this, "Score and level saved.");
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Main Method to run the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Super Mario Bros. 5");
        MiniMario game = new MiniMario();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
