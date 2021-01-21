/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
import org.karnak.backend.data.converter.RectangleListConverter;
import org.karnak.backend.data.converter.RectangleListToStringListConverter;

@Entity(name = "Masks")
@Table(name = "masks")
public class MaskEntity {

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

  public MaskEntity() {}

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
}
