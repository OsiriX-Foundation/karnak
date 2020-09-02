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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.apache.commons.lang3.StringUtils;



public class AddNewPatient extends VerticalLayout {

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
    private HorizontalLayout horizontalLayout1;
    private HorizontalLayout horizontalLayout2;

    public AddNewPatient(ListDataProvider<Patient> dataProvider, Grid<Patient> grid){
        setSizeFull();
        this.dataProvider = dataProvider;
        this.grid = grid;
        binder = new BeanValidationBinder<>(Patient.class);

        externalIdField = new TextField("External ID");
        externalIdField.setWidth("33%");
        patientIdField = new TextField("Patient ID");
        patientIdField.setWidth("33%");
        patientNameField = new TextField("Patient Name");
        patientNameField.setWidth("33%");
        issuerOfPatientIdField = new TextField("Issuer of patient ID");
        issuerOfPatientIdField.setWidth("33%");
        patientBirthDateField = new TextField("Patient Birth Date");
        patientBirthDateField.setWidth("33%");
        patientSexField = new TextField("Patient Sex");
        patientSexField.setWidth("33%");

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

        horizontalLayout1 = new HorizontalLayout();
        horizontalLayout2 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.add(externalIdField, patientIdField, patientNameField);

        horizontalLayout2.add(issuerOfPatientIdField, patientBirthDateField, patientSexField);
        horizontalLayout2.setSizeFull();
        add(horizontalLayout1, horizontalLayout2,addNewPatientButton);
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
