import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

//PlayAudio handles audio playing

public class PlayAudio {
    public PlayAudio() {
        String filePath = "kalinka.wav"; // Must be WAV, AIFF, or AU format
        //This plays the tetris game audio as a loop
        try {//play audio from a file
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            Thread.sleep(clip.getMicrosecondLength() / 1000); // Wait until done
            clip.close();

        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio format.");
        } catch (IOException e) {
            System.out.println("Error reading the audio file.");
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
