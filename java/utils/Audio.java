package utils;

import java.io.File;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class Audio {
    private final String audioFile;

    public Audio(String audioFile) {
        this.audioFile = audioFile;
    }

    public void play() {
        File file = new File(audioFile);

        Clip clip = null;
        try{
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
        } catch (Exception e){
            e.printStackTrace();
        }

        if (clip != null) {
            clip.start();
            while (!clip.isRunning()) {
                // Waiting for the clip to start
                Thread.yield();
            }
            while (clip.isRunning()) {
                // Waiting for the clip to stop
                Thread.yield();
            }
            clip.close();
        }
    }
}