package OpenCraft.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound
{

    public static void loadAndPlay(String path)
    {
        new Thread(() -> {
            try {
                File f = new File(path);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
                Clip clip = null;
                clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

}
