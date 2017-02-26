package de.ax.powermode.power.sound;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundTest {
    public static void main(String[] args) {
        File f = new File("/home/nyxos/Music/Amazing_Horse.mp3");
        JFXPanel jfxPanel = new JFXPanel();
        Media hit = new Media(f.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setVolume(1.0);
        mediaPlayer.play();
        System.out.println("playing");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mediaPlayer.stop();
        System.out.println("stopping");
         mediaPlayer.dispose();
    }
}
