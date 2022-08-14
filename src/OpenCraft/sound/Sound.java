package OpenCraft.sound;

import OpenCraft.Logger.Logger;
import OpenCraft.Resources.Resources;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

public class Sound
{

    public static void loadAndPlay(String path)
    {
        new Thread(() -> {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Resources.load(path)));
                Clip clip = null;
                clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                Logger.exception("Error while playing sound", e);
            }
        }).start();
    }

}
