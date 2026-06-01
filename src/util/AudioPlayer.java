package util;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public final class AudioPlayer {
    private static Thread backgroundThread;
    private static volatile boolean backgroundPlaying;
    private static volatile Player backgroundPlayer;

    private AudioPlayer() {
    }

    public static void startBackgroundMusic() {
        stopBackgroundMusic();
        File file = new File("resources/background.mp3");
        if (!file.exists()) {
            return;
        }

        backgroundPlaying = true;
        backgroundThread = new Thread(() -> {
            while (backgroundPlaying) {
                playFile(file, true);
            }
        }, "background-music");
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public static void stopBackgroundMusic() {
        backgroundPlaying = false;
        if (backgroundPlayer != null) {
            backgroundPlayer.close();
            backgroundPlayer = null;
        }
    }

    public static void playSuccess() {
        playEffect("resources/success.mp3");
    }

    public static void playError() {
        playEffect("resources/error.mp3");
    }

    private static void playEffect(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        Thread thread = new Thread(() -> playFile(file, false), "sound-effect");
        thread.setDaemon(true);
        thread.start();
    }

    private static void playFile(File file, boolean background) {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            Player player = new Player(input);
            if (background) {
                backgroundPlayer = player;
            }
            player.play();
        } catch (Exception ex) {
            System.err.println("音频播放失败：" + file.getPath() + "，" + ex.getMessage());
        } finally {
            if (background) {
                backgroundPlayer = null;
            }
        }
    }
}