package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.apache.commons.lang3.StringUtils;
import org.karnak.ui.component.ConfirmDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AddNewPatientForm extends VerticalLayout {

    private Binder<Patient> binder;
    private ListDataProvider<Patient> dataProvider;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientNameField;
    private TextField issuerOfPatientIdField;
    private DatePicker patientBirthDateField;
    private Select<String> patientSexField;
    private Button addNewPatientButton;
    private Button sendInMainzellisteButton;
    private Button clearFieldsButton;

    private Div validationStatus;
    private HorizontalLayout horizontalLayoutAddClear;
    private HorizontalLayout horizontalLayout1;
    private HorizontalLayout horizontalLayout2;
    private HorizontalLayout horizontalLayout3;

    public AddNewPatientForm(ListDataProvider<Patient> dataProvider){
        setSizeFull();
        getElement().addEventListener("keydown", event -> {
            addPatientFieldsInGrid();
        }).setFilter("event.key == 'Enter'");

        this.dataProvider = dataProvider;

        binder = new BeanValidationBinder<>(Patient.class);

        externalIdField = new TextField("External ID");
        externalIdField.setWidth("33%");
        patientIdField = new TextField("Patient ID");
        patientIdField.setWidth("33%");
        patientNameField = new TextField("Patient Name");
        patientNameField.setWidth("33%");
        issuerOfPatientIdField = new TextField("Issuer of patient ID");
        issuerOfPatientIdField.setWidth("33%");
        patientBirthDateField = new DatePicker("Patient Birth Date");
        patientBirthDateField.setWidth("33%");
        patientSexField = new Select<>();
        patientSexField.setLabel("Patient Sex");
        patientSexField.setItems("M", "F", "O");
        patientSexField.setWidth("33%");


        sendInMainzellisteButton = new Button("Send patients in database");
        sendInMainzellisteButton.setEnabled(false);
        sendInMainzellisteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendInMainzellisteButton.setIcon(new IronIcon("icons", "icons:send"));
        sendInMainzellisteButton.addClickListener( click -> {
            ConfirmDialog dialog = new ConfirmDialog("Are you sure to send in database all patients in grid?");
            dialog.addConfirmationListener(componentEvent -> {
                sendInMainzellisteButton.setEnabled(false);
                sendInMainzelliste();
                dataProvider.getItems().clear();
                dataProvider.refreshAll();
            });
            dialog.open();
        });


        horizontalLayoutAddClear = new HorizontalLayout();
        horizontalLayoutAddClear.getStyle().set("position", "absolute");
        horizontalLayoutAddClear.getStyle().set("right", "33px");

        clearFieldsButton = new Button("Clear");
        clearFieldsButton.addClickListener( click -> {
            clearPatientFields();
        });

        addNewPatientButton = new Button("Add");
        addNewPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewPatientButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        addNewPatientButton.addClickListener(click -> {
            addPatientFieldsInGrid();
        });
        horizontalLayoutAddClear.add(clearFieldsButton, addNewPatientButton);


        // enable/disable update button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            addNewPatientButton.setEnabled(hasChanges && isValid);
        });

        dataProvider.addDataProviderListener( dataChangeEvent -> {
            if(dataProvider.getItems().isEmpty()){
                sendInMainzellisteButton.setEnabled(false);
            }else{
                sendInMainzellisteButton.setEnabled(true);
            }
        });

        validationStatus = new Div();
        validationStatus.setId("validation");
        fieldValidator();

        horizontalLayout1 = new HorizontalLayout();
        horizontalLayout2 = new HorizontalLayout();
        horizontalLayout3 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout2.setSizeFull();
        horizontalLayout3.setSizeFull();
        horizontalLayout1.add(externalIdField, patientIdField, patientNameField);
        horizontalLayout2.add(issuerOfPatientIdField, patientBirthDateField, patientSexField);
        horizontalLayout3.add(sendInMainzellisteButton, horizontalLayoutAddClear);
        add(horizontalLayout1, horizontalLayout2, horizontalLayout3);
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
                .asRequired("Please choose a date")
                .bind("patientBirthDate");

        binder.forField(patientSexField)
                .withValidator(StringUtils::isNotBlank, "Patient Sex is empty")
                .withValidator(new StringLengthValidator("Length must be 1.", 1, 1))
                .bind("patientSex");
    }

    public void addPatientFieldsInGrid(){
        final Patient newPatient = new Patient(externalIdField.getValue(),
                patientIdField.getValue(),
                patientNameField.getValue(),
                patientBirthDateField.getValue(),
                patientSexField.getValue(),
                issuerOfPatientIdField.getValue());
        binder.validate();
        if(binder.isValid()){
            dataProvider.getItems().add(newPatient);
            dataProvider.refreshAll();
            binder.readBean(null);
        }
    }

    public void clearPatientFields(){
        externalIdField.clear();
        patientIdField.clear();
        patientNameField.clear();
        issuerOfPatientIdField.clear();
        patientBirthDateField.clear();
        patientSexField.clear();
        binder.readBean(null);
    }

    public void sendInMainzelliste(){
        dataProvider.getItems().forEach( patient -> {
            System.out.println(
                "ExternalID:" + patient.getExtid() + " "
                + "PatientID:" + patient.getPatientId() + " "
                + "PatientName:" + patient.getPatientName() + " "
                + "IssuerOfPatientID:" + patient.getIssuerOfPatientId() + " "
                + "PatientSex:" + patient.getPatientSex() + " "
                + "PatientBirthDate:" + patient.getPatientBirthDate().format(DateTimeFormatter.ofPattern("YYYYMMdd")));
        });
    }


    public Button getAddNewPatientButton() {
        return addNewPatientButton;
    }


}
