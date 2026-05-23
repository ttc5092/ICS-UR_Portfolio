import java.io.*;
import java.util.*;

/**
 * ScoreManager handles saving scores to a local text file
 * using standard Java File I/O.
 */
public class ScoreManager {
    private String filename;

    public ScoreManager(String filename) {
        this.filename = filename;
    }

    public int loadScore(String username, int type) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            int retval = type-1;
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                if (line[0].equals(username)) {
                    int temp =  Integer.parseInt(line[type]);
                    if (temp>retval) retval = temp;
                }
            }
            scanner.close();
            return retval;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void saveScore(String username, int score, int level) {
        try (FileWriter fw = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter sv = new PrintWriter(bw)) {
             
            sv.println(username + "," + score + "," + level + "\n");
            
        } catch (IOException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }
}
