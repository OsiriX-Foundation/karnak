package org.karnak.ui.dicom.monitor;

import java.util.concurrent.ExecutionException;

import org.karnak.dicom.model.DicomNodeList;
import org.karnak.dicom.model.Message;
import org.karnak.dicom.model.MessageFormat;
import org.karnak.dicom.model.MessageLevel;
import org.karnak.dicom.model.WadoNodeList;
import org.karnak.dicom.service.DicomEchoService;
import org.karnak.dicom.service.WadoService;

public class MonitorLogic {
    
    // PAGE
    private MonitorView view;

    // SERVICES
    private DicomEchoService dicomEchoService;
    private WadoService wadoService;
    
    // DATA
    private DicomNodeList dicomNodeListSelected;
    private WadoNodeList wadoNodeListSelected;
    
    
    public MonitorLogic(MonitorView view) {
        this.view = view;
        
        dicomEchoService = new DicomEchoService();
        wadoService = new WadoService();
    }
    
    
    public void dicomNodeListSelected(DicomNodeList dicomNodeList) {
        this.dicomNodeListSelected = dicomNodeList;
    }
    
    public void wadoNodeListSelected(WadoNodeList wadoNodeList) {
        this.wadoNodeListSelected = wadoNodeList;
    }
    
    public void dicomEcho() {
    	try {
    		String result = dicomEchoService.dicomEcho(dicomNodeListSelected);
            view.displayStatus(result);
    	} catch (InterruptedException e) {
    		Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, "Execution was interrupted");
    		view.displayMessage(message);
        Thread.currentThread().interrupt();
    	} catch (ExecutionException e) {
    		Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, "Execution failed");
    		view.displayMessage(message);
    	}
    }
    
    public void wado() {
    	try {
	        String result = wadoService.checkWado(wadoNodeListSelected);
	        view.displayStatus(result);
    	} catch (InterruptedException e) {
    		Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, "Execution was interrupted");
    		view.displayMessage(message);
        Thread.currentThread().interrupt();
    	} catch (ExecutionException e) {
    		Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, "Execution failed");
    		view.displayMessage(message);
    	}
    }

}
