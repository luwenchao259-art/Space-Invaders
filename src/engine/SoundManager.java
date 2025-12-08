package engine;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Manages sound effects for the game.
 */
public class SoundManager {

    /**
     * Plays a sound effect from the specified file path.
     *
     * @param soundFilePath Path to the sound file.
     */
    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}