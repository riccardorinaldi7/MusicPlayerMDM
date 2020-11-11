package com.ktplayer;

public class Shortcut {
    private String shortcut;
    private String description;

    public Shortcut(String shortcut, String description) {
        this.shortcut = shortcut;
        this.description = description;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
