package org.karnak.backend.data.converter;


import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
  public class RectangleListConverter implements AttributeConverter<List<Rectangle>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Rectangle> stringList) {
      return stringList.stream().map(RectangleListConverter::rectangleToString)
          .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Rectangle> convertToEntityAttribute(String string) {
      return Arrays.asList(string.split(SPLIT_CHAR)).stream().map(RectangleListConverter::stringToRectangle).collect(Collectors.toList());
    }

  public static String rectangleToString(Rectangle rect) {
    return String.join(" ", String.valueOf(rect.x), String.valueOf(rect.y), String.valueOf(rect.width),
        String.valueOf(rect.height));
  }

  public static Rectangle stringToRectangle(String rectangle) {
    String[] vals = rectangle.trim().split("\\s+");
    if (vals.length == 4) {
      return
          new Rectangle(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Integer.parseInt(vals[2]),
              Integer.parseInt(vals[3])
          );
    }
    return null;
  }
}

