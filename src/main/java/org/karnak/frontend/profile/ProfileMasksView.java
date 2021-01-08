package org.karnak.frontend.profileEntity;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.awt.Rectangle;
import java.util.List;
import java.util.Set;
import org.karnak.backend.data.entity.MaskEntity;

public class ProfileMasksView extends VerticalLayout {

    private final Set<MaskEntity> maskEntities;


    public ProfileMasksView(Set<MaskEntity> maskEntities) {
        this.maskEntities = maskEntities;
        getStyle().set("margin-top", "0px");
        setView();
    }

    public void setView() {
        removeAll();
        if (!maskEntities.isEmpty()) {
            add(setTitleValue("Masks"));
            maskEntities.forEach(maskEntity -> {
                if (maskEntity.getStationName() != null) {
                    add(setMasksValue("Station name : " + maskEntity.getStationName()));
                }
                if (maskEntity.getColor() != null) {
                    add(setMasksValue("Color : " + maskEntity.getColor()));
                }

                if (maskEntity.getRectangles().size() > 0) {
                    add(setMasksValue("Rectangles"));
                    add(setMasksRectangles(maskEntity.getRectangles()));
                }
            });
        }

    }

    private Div setTitleValue(String title) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(title));
        profileNameDiv.getStyle().set("font-weight", "bold").set("margin-top", "0px").set("padding-left", "5px");
        return profileNameDiv;
    }

    private Div setMasksValue(String value) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(value));
        profileNameDiv.getStyle().set("color", "grey").set("padding-left", "10px").set("margin-top", "5px");
        return profileNameDiv;
    }

    private VerticalLayout setMasksRectangles(List<Rectangle> rectangles) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("color", "grey").set("padding-left", "10px").set("margin-top", "5px");
        for (Rectangle rectangle : rectangles) {
            Div rectDiv = new Div();
            rectDiv.add(new Text(
                    String.format("%d %d %d %d", (int) rectangle.getX(), (int) rectangle.getY(),
                            (int) rectangle.getWidth(), (int) rectangle.getHeight())
            ));
            rectDiv.getStyle().set("color", "grey").set("padding-left", "15px").set("margin-top", "2px");
            verticalLayout.add(rectDiv);
        }
        return verticalLayout;
    }
}
