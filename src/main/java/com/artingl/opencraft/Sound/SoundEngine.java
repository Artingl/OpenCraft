package com.artingl.opencraft.Sound;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Resources.Resources;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundEngine
{

    public SoundEngine() {

    }

    public void loadAndPlay(String id, String sound)
    {
        Thread soundThread = new Thread(() -> {
            try {
                InputStream in = Resources.load(Opencraft.class, id + ":sounds/" + sound + ".wav");

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(in));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                FloatControl gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(limit(gainControl, OptionsRegistry.Values.getIntOption("soundVolume")*0.8f));
                clip.start();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                Logger.exception("Error while playing sound", e);
            }

            Thread.currentThread().interrupt();
        });

        soundThread.setDaemon(true);
        soundThread.start();
    }

    private static float limit(FloatControl control, float level)
    {
        level += control.getMinimum();
        if (level > control.getMaximum()) level = control.getMaximum();
        return level;
    }


}
