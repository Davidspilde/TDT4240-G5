package io.github.Spyfall.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

import io.github.Spyfall.controller.StageManager;

// AudioService.java
public class AudioService {
    private final Map<String, Sound> sounds;
    private final Map<String, Music> musicTracks;
    private float musicVolume = 1.0f;
    private float soundVolume = 1.0f;
    private Preferences prefs;
    private Music currentMusic;
    private static AudioService instance;

    private AudioService() {
        prefs = Gdx.app.getPreferences("GameSettings");
        loadSettings();
        sounds = new HashMap<>();
        musicTracks = new HashMap<>();
    }
    public static AudioService getInstance(){
        return (instance == null) ? (instance = new AudioService()) : instance;
    }

    public void loadSettings() {
        musicVolume = prefs.getFloat("musicVolume", 1.0f);
        soundVolume = prefs.getFloat("soundVolume", 1.0f);
    }

    public void saveSettings() {
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putFloat("soundVolume", soundVolume);
        prefs.flush();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        saveSettings();
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
        saveSettings();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void loadSound(String name, String filePath) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
        sounds.put(name, sound);
    }

    public void loadMusic(String name, String filePath) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        musicTracks.put(name, music);
    }

    public void playSound(String name) {
        Sound sound = sounds.get(name);
        if (sound != null) {
            sound.play(soundVolume);
        }
    }

    public void playMusic(String name, boolean looping) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = musicTracks.get(name);
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
            currentMusic.setLooping(looping);
            currentMusic.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    //Burde ha en dispose sounds?? tror ikke det er lurt å ha all musikk og lyd loadet hele tiden, men vi har jo ikke så stort prosjekt med mye lyder da
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        for (Music music : musicTracks.values()) {
            music.dispose();
        }
    }
}
