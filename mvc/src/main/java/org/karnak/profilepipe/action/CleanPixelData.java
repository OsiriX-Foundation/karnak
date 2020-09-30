package org.karnak.profilepipe.action;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.img.op.MaskArea;
import org.weasis.dicom.param.AttributeEditorContext;

public class CleanPixelData extends AbstractAction {

    public CleanPixelData(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID,
        AttributeEditorContext context) {
        if(tag == Tag.PixelData && dcm.get(tag).isPresent()) {
            List<Shape> shapeList = new ArrayList<>();
            shapeList.add(new Rectangle(25, 15 , 150, 70));
            shapeList.add(new Rectangle(340, 15 , 150, 50));
            MaskArea mask = new MaskArea(shapeList, Color.RED);
            context.setMaskArea(mask);
        }
    }
}
