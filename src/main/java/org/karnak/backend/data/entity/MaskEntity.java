package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.karnak.backend.data.converter.RectangleListConverter;
import org.karnak.backend.data.converter.RectangleListToStringListConverter;

@Entity(name = "Masks")
@Table(name = "masks")
public class MaskEntity implements Serializable {

  private static final long serialVersionUID = 1833858684629178458L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  @ManyToOne()
  @JoinColumn(name = "profile_id", nullable = false)
  @JsonIgnore
  private ProfileEntity profileEntity;

    private String stationName;
    private String color;

    @Convert(converter = RectangleListConverter.class)
    @JsonSerialize(converter = RectangleListToStringListConverter.class)
    private List<Rectangle> rectangles = new ArrayList<>();

  public MaskEntity() {
  }

  public MaskEntity(String stationName, String color, ProfileEntity profileEntity) {
    this.stationName = stationName;
    this.color = color;
    this.profileEntity = profileEntity;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaskEntity that = (MaskEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(profileEntity, that.profileEntity) &&
        Objects.equals(stationName, that.stationName) &&
        Objects.equals(color, that.color) &&
        Objects.equals(rectangles, that.rectangles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, profileEntity, stationName, color, rectangles);
  }
}
