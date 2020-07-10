package org.karnak.ui.dicom.mwl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.io.DicomEncoding;
import org.dcm4che6.io.DicomOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DicomPaneLogic {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DicomPaneLogic.class);

	// PANE
	private DicomPane pane;
	
	
	public DicomPaneLogic(DicomPane pane) {
		this.pane = pane;
	}

	public InputStream getWorklistItemInputStreamInDicom(DicomObject attributes) {
		InputStream inputStream = null;
		
        if (attributes != null) {
            try (ByteArrayOutputStream tmp = new ByteArrayOutputStream(); DicomOutputStream out = new DicomOutputStream(tmp).withEncoding(DicomEncoding.EVR_LE)) {
                out.writeDataSet(attributes);
                inputStream = new ByteArrayInputStream(tmp.toByteArray());
            } catch (IOException e) {
                LOGGER.error("Cannot write dicom file: {}", e.getMessage()); //$NON-NLS-1$
            }
        }
		
		return inputStream;
	}
	
	public InputStream getWorklistItemInputStreamText(DicomObject attributes) {
		InputStream inputStream = null;
		
        if (attributes != null) {
        	inputStream = new ByteArrayInputStream(attributes.toString(1500, 300).getBytes());
        }

        return inputStream;
	}

}
