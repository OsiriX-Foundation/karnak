package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.karnak.data.gateway.Destination;


public class AddNewPatient extends HorizontalLayout {

    private Binder<Patient> binder;
    private Grid<Patient> grid;
    private ListDataProvider<Patient> dataProvider;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientNameField;
    private TextField issuerOfPatientIdField;
    private TextField patientBirthDateField;
    private TextField patientSexField;
    private Button addNewPatientButton;

    private Div validationStatus;

    public AddNewPatient(ListDataProvider<Patient> dataProvider, Grid<Patient> grid){


        this.dataProvider = dataProvider;
        this.grid = grid;
        binder = new BeanValidationBinder<>(Patient.class);

        externalIdField = new TextField("External ID");
        patientIdField = new TextField("Patient ID");
        patientNameField = new TextField("Patient Name");
        issuerOfPatientIdField = new TextField("Issuer of patient ID");
        patientBirthDateField = new TextField("Patient Birth Date");
        patientSexField = new TextField("Patient Sex");

        addNewPatientButton = new Button("Adding a new patient ");
        addNewPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewPatientButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        addNewPatientButton.addClickListener(click -> {
            Patient newPatient = new Patient(externalIdField.getValue(),
                    patientIdField.getValue(),
                    patientNameField.getValue(),
                    patientBirthDateField.getValue(),
                    patientSexField.getValue(),
                    issuerOfPatientIdField.getValue());
            binder.validate();
            if(binder.isValid()){
                dataProvider.getItems().add(newPatient);
                grid.getDataProvider().refreshAll();
                clearFields();
            }
        });

        // enable/disable update button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            addNewPatientButton.setEnabled(hasChanges && isValid);
        });

        validationStatus = new Div();
        validationStatus.setId("validation");
        fieldValidator();

        add(externalIdField, patientIdField, patientNameField, issuerOfPatientIdField, patientBirthDateField, patientSexField, addNewPatientButton);
    }

    public void fieldValidator(){
        binder.forField(externalIdField)
                .withValidator(StringUtils::isNotBlank, "External ID is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .bind("extid");

        binder.forField(patientIdField)
                .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .bind("patientId");

        binder.forField(patientNameField)
                .withValidator(StringUtils::isNotBlank, "Patient Name is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .bind("patientName");

        binder.forField(issuerOfPatientIdField)
                .withValidator(StringUtils::isNotBlank, "Issuer of patient ID is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .bind("issuerOfPatientId");

        binder.forField(patientBirthDateField)
                .withValidator(StringUtils::isNotBlank, "Patient Birth Date is empty")
                .withValidator(new StringLengthValidator("Length must be 8.", 8, 8))
                .bind("patientBirthDate");

        binder.forField(patientSexField)
                .withValidator(StringUtils::isNotBlank, "Patient Sex is empty")
                .withValidator(new StringLengthValidator("Length must be 1.", 1, 1))
                .bind("patientSex");
    }

    public void clearFields(){
        externalIdField.clear();
        patientIdField.clear();
        patientNameField.clear();
        issuerOfPatientIdField.clear();
        patientBirthDateField.clear();
        patientSexField.clear();
    }

}
