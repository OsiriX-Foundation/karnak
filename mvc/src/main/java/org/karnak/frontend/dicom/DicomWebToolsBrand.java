package org.karnak.frontend.dicom;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class DicomWebToolsBrand extends Composite<Div> {

	private static final long serialVersionUID = 5833252271846713700L;
	
	private static final String TEXT = "Dicom Web Tools";

  private final Div div;
  private Span text;

    public DicomWebToolsBrand() {
        div = getContent();
        div.getStyle().set("display", "contents");
        
        createText();
        
        div.add(text);
    }
    
    private void createText() {
    	text = new Span(TEXT);
    	text.getStyle().set("padding-left", "1em");
    	text.getStyle().set("padding-right", "1em");
    	text.getStyle().set("white-space", "nowrap");
    }
	
}
