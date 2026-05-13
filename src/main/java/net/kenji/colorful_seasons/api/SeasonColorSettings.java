package net.kenji.colorful_seasons.api;

// r, g, b:   0 = no change, 100 = push fully to 255
// lightness: 0.0 = black, 0.5 = no change, 1.0 = white
public record SeasonColorSettings(int r, int g, int b, double lightness) {
    public static final SeasonColorSettings NONE = new SeasonColorSettings(0, 0, 0, 0.5);
}