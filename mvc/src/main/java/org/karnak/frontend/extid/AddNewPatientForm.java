package org.karnak.frontend.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.configuration.AppConfig;
import org.karnak.backend.util.PatientClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddNewPatientForm extends VerticalLayout {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AddNewPatientForm.class);
    private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";

    private final Binder<CachedPatient> binder;
    private final ListDataProvider<CachedPatient> dataProvider;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientNameField;
    private TextField issuerOfPatientIdField;
    private Button addNewPatientButton;
    private Button clearFieldsButton;

    private final transient PatientClient externalIDCache;

    public AddNewPatientForm(ListDataProvider<CachedPatient> dataProvider){
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

        addNewPatientButton.addClickListener(click ->
            addPatientFieldsInGrid()
        );

        // enable/disable update button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            addNewPatientButton.setEnabled(hasChanges && isValid);
        });
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
        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        HorizontalLayout horizontalLayout2 = new HorizontalLayout();

        externalIdField = new TextField("External Pseudonym");
        externalIdField.setWidth("25%");
        patientIdField = new TextField("Patient ID");
        patientIdField.setWidth("25%");
        patientNameField = new TextField("Patient name");
        patientNameField.setWidth("25%");
        issuerOfPatientIdField = new TextField("Issuer of patient ID");
        issuerOfPatientIdField.setWidth("25%");

        clearFieldsButton = new Button("Clear");
        clearFieldsButton.getStyle().set("margin-left", "auto");

        addNewPatientButton = new Button("Save patient temporary");
        addNewPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewPatientButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());

        horizontalLayout1.setSizeFull();
        horizontalLayout2.setSizeFull();

        horizontalLayout1.add(externalIdField, patientIdField, patientNameField, issuerOfPatientIdField);
        horizontalLayout2.add(clearFieldsButton, addNewPatientButton);
        add(horizontalLayout1, horizontalLayout2);
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

        binder.forField(patientNameField)
                .withValidator(StringUtils::isNotBlank, "Patient name is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .bind("patientName");

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

    public void addPatientFieldsInGrid(){
        final CachedPatient newPatient = new CachedPatient(externalIdField.getValue(),
                patientIdField.getValue(),
                patientNameField.getValue(),
                issuerOfPatientIdField.getValue());
        binder.validate();
        if (binder.isValid()){
            if (patientExist(newPatient, dataProvider)){
                WarningDialog warningDialog = new WarningDialog("Duplicate data", "You are trying to insert two equivalent pseudonyms or two potentially identical patients.", "ok");
                warningDialog.open();
            } else {
                dataProvider.getItems().add(newPatient);
                dataProvider.refreshAll();
                externalIDCache.put(PatientClientUtil.generateKey(newPatient), newPatient);
                binder.readBean(null);
            }
        }
    }

    public void clearPatientFields(){
        externalIdField.clear();
        patientIdField.clear();
        patientNameField.clear();
        issuerOfPatientIdField.clear();
        binder.readBean(null);
    }

    public Button getAddNewPatientButton() {
        return addNewPatientButton;
    }
}
