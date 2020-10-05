package org.karnak.profilepipe.profilebody;

import java.util.List;

public class MaskBody {
    private String stationName;
    private String color;
    private List<String> rectangles;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getRectangles() {
        return rectangles;
    }

    public void setRectangles(List<String> rectangles) {
        this.rectangles = rectangles;
    }
}
