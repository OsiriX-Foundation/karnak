package org.karnak.data.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.karnak.data.profile.converter.RectangleListToStringListConverter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "Masks")
@Table(name = "masks")
public class Mask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private Profile profile;

    private String stationName;
    private String color;

    @Convert(converter = RectangleListConverter.class)
    @JsonSerialize(converter = RectangleListToStringListConverter.class)
    private List<Rectangle> rectangles = new ArrayList<>();

    public Mask() {
    }

    public Mask(String stationName, String color, Profile profile) {
        this.stationName = stationName;
        this.color = color;
        this.profile = profile;
    }

    public void addRectangle(String rectangle) {
        Rectangle rect = RectangleListConverter.stringToRectangle(rectangle);
        if (rect != null) {
            rectangles.add(rect);
        }
    }

    public void addRectangle(Rectangle rect) {
        rectangles.add(rect);
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    public void setRectangles(List<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    public Long getId() {
        return id;
    }

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
}
