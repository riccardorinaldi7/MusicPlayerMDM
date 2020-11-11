package com.ktplayer;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Song {
    private StringProperty id;
    private StringProperty artistName;
    private StringProperty songName;
    private StringProperty length;
    private StringProperty duration;
    private StringProperty album;
    private StringProperty url;
    private BufferedImage image;

    public Song() {}

    public Song(String url) {
        this.url = new SimpleStringProperty(url);
    }

    public Song(String id, String artistName, String songName, String length, String duration, String album, String url, byte[] imageData) throws IOException {
        this.id = new SimpleStringProperty(id);
        this.artistName = new SimpleStringProperty(artistName);
        this.songName = new SimpleStringProperty(songName);
        this.length = new SimpleStringProperty(length);
        this.duration = new SimpleStringProperty(duration);
        this.album = new SimpleStringProperty(album);
        this.url = new SimpleStringProperty(url);
        if(imageData != null) this.image = ImageIO.read(new ByteArrayInputStream(imageData));
    }

    public Song(File file) {

    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getArtistName() {
        return artistName.get();
    }

    public void setArtistName(String artistName) {
        this.artistName.set(artistName);
    }

    public StringProperty artistNameProperty() {
        return artistName;
    }

    public String getSongName() {
        return songName.get();
    }

    public void setSongName(String songName) {
        this.songName.set(songName);
    }

    public StringProperty songNameProperty() {
        return songName;
    }

    public String getDuration() {
        return length.get();
    }

    public void setDuration(String duration) {
        this.length.set(duration);
    }

    public StringProperty durationProperty() {
        return length;
    }

    public String getRate() {
        return duration.get();
    }

    public StringProperty rateProperty() {
        return duration;
    }

    public void setRate(String rate) {
        this.duration.set(rate);
    }

    public String getFormat() {
        return album.get();
    }

    public StringProperty formatProperty() {
        return album;
    }

    public void setFormat(String format) {
        this.album.set(format);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}

