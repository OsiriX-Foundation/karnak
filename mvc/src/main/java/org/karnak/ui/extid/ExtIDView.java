package org.karnak.ui.extid;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.karnak.ui.MainLayout;

import java.util.*;

@Route(value = "extid", layout = MainLayout.class)
@PageTitle("KARNAK - External ID")
@Tag("extid-view")
@SuppressWarnings("serial")
public class ExtIDView extends VerticalLayout {
    public static final String VIEW_NAME = "External pseudonym";

    private Div validationStatus;
    private Binder<Patient> binder;
    private Grid<Patient> grid;
    private List<Patient> patientList;
    private Button addNewPatientButton;
    private ListDataProvider<Patient> dataProvider;
    private Button deletePatientButton;
    private Button saveEditPatientButton;
    private Button cancelEditPatientButton;

    private Grid.Column<Patient> extidColumn;
    private Grid.Column<Patient> patientIdColumn;
    private Grid.Column<Patient> patientNameColumn;
    private Grid.Column<Patient> issuerOfPatientIDColumn;
    private Grid.Column<Patient> patientBirthDateColumn;
    private Grid.Column<Patient> patientSexColumn;
    private Grid.Column<Patient> editorColumn;

    private Editor<Patient> editor;
    private Collection<Button> editButtons;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientNameField;
    private TextField issuerOfPatientIdField;
    private TextField patientBirthDateField;
    private Select<String> patientSexField;

    private Grid.Column<Patient> deleteColumn;

    private AddNewPatient addNewPatient;


    //https://vaadin.com/components/vaadin-grid/java-examples/assigning-data
    public ExtIDView() {
        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new H2("External ID"));

        binder = new Binder<>(Patient.class);
        patientList = new ArrayList<>();
        grid = new Grid<>();
        grid.setHeightByRows(true);
        grid.setItems(patientList);
        dataProvider = (ListDataProvider<Patient>) grid.getDataProvider();

        addNewPatient = new AddNewPatient(dataProvider, grid);

        extidColumn = grid.addColumn(Patient::getExtid).setHeader("External ID");
        patientIdColumn = grid.addColumn(Patient::getPatientId).setHeader("Patient ID");
        patientNameColumn = grid.addColumn(Patient::getPatientName).setHeader("Patient Name");
        issuerOfPatientIDColumn = grid.addColumn(Patient::getIssuerOfPatientId).setHeader("Issuer of patient ID");
        patientBirthDateColumn = grid.addColumn(Patient::getPatientBirthDate).setHeader("Patient Birth Date");
        patientSexColumn = grid.addColumn(Patient::getPatientSex).setHeader("Patient Sex");


        editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        editorColumn = grid.addComponentColumn(patient -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(patient);
                externalIdField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        externalIdField = new TextField();
        patientIdField = new TextField();
        patientNameField = new TextField();
        issuerOfPatientIdField = new TextField();
        patientBirthDateField = new TextField();
        patientSexField = new Select<>();
        patientSexField.setItems("M", "F", "O");

        validationStatus = new Div();
        validationStatus.setId("validation");

        fieldValidator();

        extidColumn.setEditorComponent(externalIdField);
        patientIdColumn.setEditorComponent(patientIdField);
        patientNameColumn.setEditorComponent(patientNameField);
        issuerOfPatientIDColumn.setEditorComponent(issuerOfPatientIdField);
        patientBirthDateColumn.setEditorComponent(patientBirthDateField);
        patientSexColumn.setEditorComponent(patientSexField);

        editor.addOpenListener(e -> {
            editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen()));
            showEditor(true);
        });

        editor.addCloseListener(e -> {
            editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen()));
            showEditor(false);
        });



        saveEditPatientButton = new Button("Save", e -> {
            editor.save();
        });

        cancelEditPatientButton = new Button("Cancel", e -> {
            editor.cancel();
        });



        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(saveEditPatientButton, cancelEditPatientButton);
        editorColumn.setEditorComponent(buttons);


        deleteColumn = grid.addComponentColumn(patient -> {
            deletePatientButton = new Button("Delete");
            deletePatientButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            deletePatientButton.addClickListener( e -> {
                patientList.remove(patient);
                grid.getDataProvider().refreshAll();
            });
            return deletePatientButton;
        });


        verticalLayout.add(addNewPatient);
        verticalLayout.add(grid);

        add(verticalLayout);
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

    public void showEditor(boolean show){
        deleteColumn.setVisible(!show);
        addNewPatient.setEnabled(!show);
    }

}
