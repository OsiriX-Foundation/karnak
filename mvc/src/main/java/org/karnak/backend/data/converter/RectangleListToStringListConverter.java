package org.karnak.backend.data.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class RectangleListToStringListConverter extends StdConverter<List<Rectangle>, List<String>> {

    @Override
    public List<String> convert(List<Rectangle> rectangles) {
        List strArray = new ArrayList();
        rectangles.forEach(rectangle -> {
            strArray.add(String.format("%d %d %d %d", (int) rectangle.getX(), (int) rectangle.getY(),
                    (int) rectangle.getWidth(), (int) rectangle.getHeight()));
        });
        return strArray;
    }
}
