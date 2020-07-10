package org.karnak.ui.dicom.echo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.router.*;
import org.karnak.ui.dicom.DicomMainView;
import org.karnak.dicom.model.ConfigNode;
import org.karnak.dicom.model.DicomEchoQueryData;
import org.karnak.dicom.model.Message;
import org.karnak.dicom.model.MessageFormat;
import org.karnak.dicom.model.MessageLevel;
import org.karnak.ui.dicom.PortField;
import org.karnak.ui.dicom.AbstractView;
import org.karnak.ui.dicom.echo.DicomEchoSelectionDialog.DicomNodeSelectionEvent;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.data.binder.StatusChangeListener;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * Calling Order 
 * 1) constructor 
 * 2) setParameter 
 * 3) beforeEnter
 * 
 */
@PageTitle("Dicom Echo")
@Route(value = "echo", layout= DicomMainView.class)
@SuppressWarnings("serial")
public class DicomEchoView extends AbstractView implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Dicom Echo";
    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_CALLING_AET = "callingAET";
    private static final String PARAMETER_CALLED_AET = "calledAET";
    private static final String PARAMETER_CALLED_HOSTNAME = "calledHostname";
    private static final String PARAMETER_CALLED_PORT = "calledPort";
    private static final String PARAMETER_ACTION = "action";
    
    private static final String ACTION_ECHO = "echo";

    // CONTROLLER
    private DicomEchoLogic logic = new DicomEchoLogic(this);
    
    // DIALOGS
    private DicomEchoSelectionDialog dicomEchoSelectionDialog;

    // UI COMPONENTS
    // Dicom Echo Query
    private VerticalLayout dicomEchoQueryLayout;
    private FormLayout formLayout;
    private H6 formLayoutTitle;
    private TextField callingAetFld;
    private TextField calledAetFld;
    private TextField calledHostnameFld;
    private PortField calledPortFld;
    private HorizontalLayout buttonBar;
    private Button clearBtn;
    private Button selectDicomNodeBtn;
    private Button dicomEchoBtn;
    // Dicom Echo Status
    private Div dicomEchoStatusLayout;

    // DATA
    private DicomEchoQueryData dicomEchoQueryData;
    private Binder<DicomEchoQueryData> binder;

    // PARAMETERS
    private String callingAetParam;
    private String dicomNodeAetParam;
    private String dicomNodeHostnameParam;
    private String dicomNodePortParam;
    private String actionParam;

    
    public DicomEchoView() {
        init();
        createView();
        createMainLayout();

        add(mainLayout);

        bindFields();
    }
    

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        readParameters(parameter);
        
        fillFieldsFromParameters();
        
        executeAction();
    }

    /*
     * https://vaadin.com/forum/thread/17072019/inject-an-html-into-a-flow-compoment
     */
    public void displayStatus(String status) {
        dicomEchoStatusLayout.removeAll();
        dicomEchoStatusLayout.add(new Html("<span>" + status + "</span>"));
        dicomEchoStatusLayout.setVisible(true);
    }

    private void init() {
        dicomEchoQueryData = new DicomEchoQueryData();
        binder = new Binder<DicomEchoQueryData>();
    }
    
    private void createView() {
        getStyle().set("background-color", "#fafafa");
    	setSizeFull();
    }

    private void createMainLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);
        mainLayout.setWidthFull();

        buildDicomEchoQueryLayout();
        buildDicomEchoStatusLayout();

        mainLayout.add(dicomEchoQueryLayout, dicomEchoStatusLayout);
    }
    
    private void buildDicomEchoQueryLayout() {
        dicomEchoQueryLayout = new VerticalLayout();
        dicomEchoQueryLayout.setWidthFull();
        dicomEchoQueryLayout.setPadding(true);
        dicomEchoQueryLayout.setSpacing(false);
        dicomEchoQueryLayout.getStyle().set("background-color", "#ffffff");
        dicomEchoQueryLayout.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0,0,0,.2), 0 1px 1px 0 rgba(0,0,0,.14), 0 1px 3px 0 rgba(0,0,0,.12)");
        dicomEchoQueryLayout.getStyle().set("border-radius", "4px");
        
        buildFormLayoutTitle();
        buildFormLayout();
        buildButtonBar();

        dicomEchoQueryLayout.add(formLayoutTitle, formLayout, buttonBar);
    }

    private void buildFormLayoutTitle() {
        formLayoutTitle = new H6("Dicom Echo");
        formLayoutTitle.getStyle().set("margin-top", "0px");
    }

    private void buildFormLayout() {
        formLayout = new FormLayout();

        buildCallingAetFld();
        buildCalledAetFld();
        buildCalledHostnameFld();
        buildCalledPortFld();

        formLayout.add(callingAetFld, calledAetFld, calledHostnameFld, calledPortFld);

        setFormResponsive(formLayout);
    }

    private void buildCallingAetFld() {
        callingAetFld = new TextField();
        callingAetFld.setLabel("Calling AETitle");
        callingAetFld.setRequired(true);
        callingAetFld.setRequiredIndicatorVisible(true);
        callingAetFld.setValueChangeMode(ValueChangeMode.EAGER);
    }
    
    private void buildCalledAetFld() {
        calledAetFld = new TextField("Called AET");
        calledAetFld.setValueChangeMode(ValueChangeMode.EAGER);
    }
    
    private void buildCalledHostnameFld() {
        calledHostnameFld = new TextField("Called Hostname");
        calledHostnameFld.setValueChangeMode(ValueChangeMode.EAGER);
    }
    
    private void buildCalledPortFld() {
        calledPortFld = new PortField();
        calledPortFld.setLabel("Called Port");
        calledPortFld.setValueChangeMode(ValueChangeMode.EAGER);
    }

    private void buildButtonBar() {
        buttonBar = new HorizontalLayout();
        buttonBar.setWidthFull();
        buttonBar.setPadding(false);
        buttonBar.setMargin(false);
        buttonBar.getStyle().set("margin-top", "1em");

        buildClearBtn();
        buildSelectDicomNodeBtn();
        buildDicomEchoBtn();

        buttonBar.add(clearBtn, selectDicomNodeBtn, dicomEchoBtn);
    }

    @SuppressWarnings("serial")
    private void buildClearBtn() {
        clearBtn = new Button("Clear");
        clearBtn.getStyle().set("cursor", "pointer");
        
        clearBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                binder.readBean(dicomEchoQueryData);
            }
        });
    }
    
    @SuppressWarnings("serial")
    private void buildSelectDicomNodeBtn() {
        selectDicomNodeBtn = new Button("Select Node");
        selectDicomNodeBtn.getStyle().set("cursor", "pointer");
        
        selectDicomNodeBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                openDicomEchoSelectionDialog();
            }
        });
    }
    
    @SuppressWarnings("serial")
    private void openDicomEchoSelectionDialog() {
        dicomEchoSelectionDialog = new DicomEchoSelectionDialog();
        
        dicomEchoSelectionDialog.addDicomNodeSelectionListener(new ComponentEventListener<DicomEchoSelectionDialog.DicomNodeSelectionEvent>() {
            
            @Override
            public void onComponentEvent(DicomNodeSelectionEvent event) {
                ConfigNode selectedDicomNode = event.getSelectedDicomNode();
                
                calledAetFld.setValue(selectedDicomNode.getAet());
                calledHostnameFld.setValue(selectedDicomNode.getHostname());
                calledPortFld.setValue(selectedDicomNode.getPort());
            }
        });
        
        dicomEchoSelectionDialog.open();
    }
    
    @SuppressWarnings("serial")
    private void buildDicomEchoBtn() {
        dicomEchoBtn = new Button("Echo");
        dicomEchoBtn.getStyle().set("cursor", "pointer");
        dicomEchoBtn.addClassName("stroked-button");
        dicomEchoBtn.setEnabled(false);

        dicomEchoBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                executeEcho();
            }
        });
    }

    private void buildDicomEchoStatusLayout() {
        dicomEchoStatusLayout = new Div();
        dicomEchoStatusLayout.setWidth("-webkit-fill-available");
        dicomEchoStatusLayout.getStyle().set("padding", "1em");
        dicomEchoStatusLayout.getStyle().set("background-color", "#ffffff");
        dicomEchoStatusLayout.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0,0,0,.2), 0 1px 1px 0 rgba(0,0,0,.14), 0 1px 3px 0 rgba(0,0,0,.12)");
        dicomEchoStatusLayout.getStyle().set("border-radius", "4px");
        
        dicomEchoStatusLayout.setVisible(false);
    }

    @SuppressWarnings("serial")
    private void bindFields() {
        binder.forField(callingAetFld)
        .asRequired("This filed is mandatory")
        .bind(DicomEchoQueryData::getCallingAet, DicomEchoQueryData::setCallingAet);
        
        binder.forField(calledAetFld)
        .asRequired("This filed is mandatory")
        .bind(DicomEchoQueryData::getCalledAet, DicomEchoQueryData::setCalledAet);
        
        binder.forField(calledHostnameFld)
        .asRequired("This filed is mandatory")
        .bind(DicomEchoQueryData::getCalledHostname, DicomEchoQueryData::setCalledHostname);
        
        binder.forField(calledPortFld)
        .asRequired("This filed is mandatory")
        .withValidator(new IntegerRangeValidator("Invalid port number", 1, 65535))
        .bind(DicomEchoQueryData::getCalledPort, DicomEchoQueryData::setCalledPort);
        
        binder.readBean(dicomEchoQueryData);

        binder.addStatusChangeListener(new StatusChangeListener() {

            @Override
            public void statusChange(StatusChangeEvent event) {
                if (callingAetFld.isEmpty() || calledAetFld.isEmpty() || calledHostnameFld.isEmpty() || calledPortFld.isEmpty()) {
                    dicomEchoBtn.setEnabled(false);
                } else {
                    dicomEchoBtn.setEnabled(!event.hasValidationErrors());
                }

                dicomEchoStatusLayout.removeAll();
                dicomEchoStatusLayout.setVisible(false);
            }
        });
    }

    private void readParameters(String queryParameter) {
        if (queryParameter!=null && !queryParameter.trim().isEmpty()) {
            
            try {
                String queryParameterDecoded = URLDecoder.decode(queryParameter, "UTF-8");
                
                String[] parametersArray = queryParameterDecoded.split("&");

                List<String> parametersList = Arrays.asList(parametersArray);
                
                for (String parameter : parametersList) {
                    String[] parameterArray = parameter.split("=");
                    String parameterName = parameterArray[0];
                    String parameterValue = parameterArray[1];
                    
                    switch (parameterName) {
                    case PARAMETER_CALLING_AET:
                        callingAetParam = parameterValue;
                        break;
                    case PARAMETER_CALLED_AET:
                        dicomNodeAetParam = parameterValue;
                        break;
                    case PARAMETER_CALLED_HOSTNAME:
                        dicomNodeHostnameParam = parameterValue;
                        break;
                    case PARAMETER_CALLED_PORT:
                        dicomNodePortParam = parameterValue;
                        break;
                    case PARAMETER_ACTION:
                        actionParam = parameterValue;
                        break;
                    default:
                        break;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                // LOG
            }
        }
    }
    
    private void fillFieldsFromParameters() {
        if (callingAetParam != null && !callingAetParam.isEmpty()) {
            callingAetFld.setValue(callingAetParam);
        }
        
        if (dicomNodeAetParam != null && !dicomNodeAetParam.isEmpty()) {
            calledAetFld.setValue(dicomNodeAetParam);
        }
        
        if (dicomNodeHostnameParam != null && !dicomNodeHostnameParam.isEmpty()) {
            calledHostnameFld.setValue(dicomNodeHostnameParam);
        }
        
        if (dicomNodePortParam != null && !dicomNodePortParam.isEmpty()) {
            calledPortFld.setValue(Integer.valueOf(dicomNodePortParam));
        }
    }
    
    private void executeAction() {
        if (actionParam != null) {
            if (actionParam.equals(ACTION_ECHO)) {
                executeEcho();
            }
        }
    }
    
    private void executeEcho() {
        DicomEchoQueryData data = new DicomEchoQueryData();
        try {
            binder.writeBean(data);
            logic.dicomEcho(data);
        } catch (ValidationException e) {
            Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, e.getMessage());
            displayMessage(message);
        }
    }
    
}
