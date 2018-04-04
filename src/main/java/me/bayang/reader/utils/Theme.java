package me.bayang.reader.utils;

public enum Theme {
    
    LIGHT("/css/application.css", "Light"),
    DARK_ORANGE("/css/application-dark-orange.css", "Dark orange");
    
    private String path;
    
    private String displayName;
    
    private Theme(final String path, final String displayName) {
        this.path = path;
        this.displayName = displayName;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }

    public static Theme forDisplayName(String value) {
        for (Theme t : values()) {
            if (t.getDisplayName().equals(value)) {
                return t;
            }
        }
        return null;
    }

}
