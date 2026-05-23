import javax.swing.*;
import java.awt.*;

/**
 * Main application class that sets up the JFrame, handles the login system,
 * and initializes the game board.
 */
public class Tetris extends JFrame {

    private JLabel statusbar;
    private ScoreManager scoreManager;
    private String currentPlayer;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        scoreManager = new ScoreManager("tetris_scores.txt");
        
        // Simple Login System
        currentPlayer = JOptionPane.showInputDialog(this, 
                "Enter your username to play:", 
                "Tetris Login", JOptionPane.QUESTION_MESSAGE);
        
        if (currentPlayer == null || currentPlayer.trim().isEmpty()) {
            currentPlayer = "Guest";
        }

        statusbar = new JLabel(" Player: " + currentPlayer + " | 0 | HIGH 0");
        add(statusbar, BorderLayout.SOUTH);
        //Display controls message to user
        JOptionPane.showMessageDialog(this, "TETRIS CONTROLS\n\nUse left/right arrow to move.\nUse up/down arrow to rotate.\nUse space to drop piece (D for soft drop)\nUse P to pause and use S to save score.\nThe game auto-saves on game over.");
        Board board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JLabel getStatusBar() {
        return statusbar;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public static void main(String[] args) {
        //initialize game
        EventQueue.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
