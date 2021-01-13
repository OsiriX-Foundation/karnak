package org.karnak.frontend.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.util.PatientClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExternalIDForm extends VerticalLayout {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExternalIDForm.class);
    private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";

    private final Binder<CachedPatient> binder;
    private final ListDataProvider<CachedPatient> dataProvider;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientFirstNameField;
    private TextField patientLastNameField;
    private TextField issuerOfPatientIdField;
    private Button addPatientButton;
    private Button clearFieldsButton;
    private transient InputStream inputStream;

    private final transient PatientClient externalIDCache;

    private Upload uploadCsvButton;
    private Div addedPatientLabelDiv;
    private Div uploadCsvLabelDiv;

    public ExternalIDForm(ListDataProvider<CachedPatient> dataProvider){
        setSizeFull();
        this.dataProvider = dataProvider;
        externalIDCache = AppConfig.getInstance().getExternalIDCache();
        binder = new BeanValidationBinder<>(CachedPatient.class);

        setElements();
        setBinder();

        readAllCacheValue();

        clearFieldsButton.addClickListener( click ->
            clearPatientFields()
        );

        addPatientButton.addClickListener(click -> {
            CachedPatient newPatient = new CachedPatient(externalIdField.getValue(),
                    patientIdField.getValue(),
                    patientFirstNameField.getValue(),
                    patientLastNameField.getValue(),
                    issuerOfPatientIdField.getValue());
            binder.validate();
            if (binder.isValid()){
                addPatientInGrid(newPatient);
            }
        });

        // enable/disable update button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            addPatientButton.setEnabled(hasChanges && isValid);
        });

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        HorizontalLayout horizontalLayout3 = new HorizontalLayout();
        HorizontalLayout horizontalLayout4 = new HorizontalLayout();
        HorizontalLayout horizontalLayout5 = new HorizontalLayout();
        Div addPatientDiv = new Div();

        horizontalLayout1.setSizeFull();
        horizontalLayout2.setSizeFull();
        horizontalLayout3.setSizeFull();
        horizontalLayout4.setSizeFull();

        horizontalLayout1.add(uploadCsvLabelDiv);
        horizontalLayout2.add(uploadCsvButton);
        horizontalLayout3.add(addedPatientLabelDiv);

        horizontalLayout4.add(externalIdField, patientIdField, patientFirstNameField, patientLastNameField, issuerOfPatientIdField);
        horizontalLayout5.add(clearFieldsButton, addPatientButton);

        addPatientDiv.add(horizontalLayout4, horizontalLayout5);
        add(horizontalLayout1, horizontalLayout2, horizontalLayout3, addPatientDiv);
    }

    private void readAllCacheValue(){
        if (externalIDCache != null) {
            Collection<PseudonymPatient> patients = externalIDCache.getAll();
            for (Iterator<PseudonymPatient> iterator = patients.iterator(); iterator.hasNext();) {
                final PseudonymPatient patient = iterator.next();
                dataProvider.getItems().add((CachedPatient) patient);
            }
        }
    }

    private void setElements() {
        setElementUploadCSV();

        uploadCsvLabelDiv = new Div();
        uploadCsvLabelDiv.setText("Upload the CSV file containing the external ID associated with patient(s): ");
        uploadCsvLabelDiv.getStyle().set("font-size", "large").set("font-weight", "bolder");

        addedPatientLabelDiv = new Div();
        addedPatientLabelDiv = new Div();
        addedPatientLabelDiv.setText("Add a new patient: ");
        addedPatientLabelDiv.getStyle().set("font-size", "large").set("font-weight", "bolder");

        externalIdField = new TextField("External Pseudonym");
        externalIdField.setWidth("20");
        patientIdField = new TextField("Patient ID");
        patientIdField.setWidth("20%");
        patientFirstNameField = new TextField("Patient first name");
        patientFirstNameField.setWidth("20%");
        patientLastNameField = new TextField("Patient last name");
        patientLastNameField.setWidth("20%");
        issuerOfPatientIdField = new TextField("Issuer of patient ID");
        issuerOfPatientIdField.setWidth("20%");

        clearFieldsButton = new Button("Clear");

        addPatientButton = new Button("Add patient");
        addPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addPatientButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
    }

    public void setElementUploadCSV() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        uploadCsvButton = new Upload(memoryBuffer);
        uploadCsvButton.setDropLabel(new Span("Drag and drop your CSV file here"));
        uploadCsvButton.addSucceededListener(event -> {
            inputStream = memoryBuffer.getInputStream();

            Dialog chooseSeparatorDialog = new Dialog();
            TextField separatorCSVField = new TextField("Choose the separator for reading the CSV file");
            separatorCSVField.setWidthFull();
            separatorCSVField.setMaxLength(1);
            separatorCSVField.setValue(",");
            Button openCSVButton = new Button("Open CSV");

            openCSVButton.addClickListener(buttonClickEvent -> {
                chooseSeparatorDialog.close();
                char separator = ',';
                if(!separatorCSVField.getValue().equals("")){
                    separator = separatorCSVField.getValue().charAt(0);
                }
                CSVDialog csvDialog = new CSVDialog(inputStream, separator);
                csvDialog.open();

                csvDialog.getReadCSVButton().addClickListener(buttonClickEvent1 -> {
                    final List<CachedPatient> patientListInCSV = csvDialog.getPatientsList();
                    patientListInCSV.forEach(this::addPatientInGrid);
                    csvDialog.resetPatientsList();
                });
            });

            chooseSeparatorDialog.add(separatorCSVField, openCSVButton);
            chooseSeparatorDialog.open();
            separatorCSVField.focus();
        });
    }

    public void setBinder(){
        binder.forField(externalIdField)
                .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .bind("pseudonym");

        binder.forField(patientIdField)
                .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .bind("patientId");

        binder.forField(patientFirstNameField)
                .withValidator(StringUtils::isNotBlank, "Patient first name is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .bind("patientFirstName");

        binder.forField(patientLastNameField)
                .withValidator(StringUtils::isNotBlank, "Patient last name is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .bind("patientLastName");

        binder.forField(issuerOfPatientIdField)
                .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
                .bind("issuerOfPatientId");
    }

    public boolean patientExist(PseudonymPatient patient, ListDataProvider<CachedPatient> dataProvider) {
        for (PseudonymPatient patientElem : dataProvider.getItems()) {
            if (patientElem.getPseudonym().equals(patient.getPseudonym()) ||
                    (patientElem.getPatientId().equals(patient.getPatientId()) &&
                            patientElem.getIssuerOfPatientId().equals(patient.getIssuerOfPatientId()))) {
                return true;
            }
        }
        return false;
    }

    public void addPatientInGrid(CachedPatient newPatient){
        if (patientExist(newPatient, dataProvider)){
            WarningDialog warningDialog = new WarningDialog("Duplicate data",
                    String.format("You are trying to insert two equivalent pseudonyms or identical patients: {%s}",
                            newPatient.toString()),
                    "ok");
            warningDialog.open();
        } else {
            dataProvider.getItems().add(newPatient);
            dataProvider.refreshAll();
            externalIDCache.put(PatientClientUtil.generateKey(newPatient), newPatient);
            binder.readBean(null);
        }
    }

    public void clearPatientFields(){
        externalIdField.clear();
        patientIdField.clear();
        patientFirstNameField.clear();
        patientLastNameField.clear();
        issuerOfPatientIdField.clear();
        binder.readBean(null);
    }

    public Button getAddPatientButton() {
        return addPatientButton;
    }
}
