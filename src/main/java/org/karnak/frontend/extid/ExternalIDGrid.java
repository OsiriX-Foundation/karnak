package org.karnak.frontend.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.util.PatientClientUtil;

public class ExternalIDGrid extends Grid<CachedPatient> {

    private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";
    private final Binder<CachedPatient> binder;
    private final List<CachedPatient> patientList;
    private Button addPatientButton;
    private Button deletePatientButton;
    private Button saveEditPatientButton;
    private Button cancelEditPatientButton;

    private Editor<CachedPatient> editor;
    private Collection<Button> editButtons;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientFirstNameField;
    private TextField patientLastNameField;
    private TextField issuerOfPatientIdField;

    private Grid.Column<CachedPatient> deleteColumn;

    private static final String LABEL_SAVE = "Save";
    private static final String LABEL_CANCEL = "Cancel";
    private final transient PatientClient externalIDCache;

    public ExternalIDGrid(){
        binder = new Binder<>(CachedPatient.class);
        patientList = new ArrayList<>();
        externalIDCache = AppConfig.getInstance().getExternalIDCache();

        setSizeFull();
        getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
        setHeightByRows(true);
        setItems(patientList);
        setElements();
        setBinder();

        editor.addOpenListener(e -> {
            editButtons.stream()
                    .forEach(button -> button.setEnabled(!editor.isOpen()));
            deleteColumn.setVisible(false);
            addPatientButton.setVisible(false);
        });

        editor.addCloseListener(e -> {
            editButtons.stream()
                    .forEach(button -> button.setEnabled(!editor.isOpen()));
            deleteColumn.setVisible(true);
            addPatientButton.setVisible(true);
        });

        saveEditPatientButton.addClickListener(e -> {
            final CachedPatient patientEdit = new CachedPatient(
                    externalIdField.getValue(),
                    patientIdField.getValue(),
                    patientFirstNameField.getValue(),
                    patientLastNameField.getValue(),
                    issuerOfPatientIdField.getValue()
            );
            externalIDCache.remove(PatientClientUtil.generateKey(editor.getItem())); //old extid
            externalIDCache.put(PatientClientUtil.generateKey(patientEdit), patientEdit); //new extid
            editor.save();
        });
        saveEditPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelEditPatientButton.addClickListener(e -> editor.cancel());
    }

    private void setElements() {
        Grid.Column<CachedPatient> extidColumn = addColumn(CachedPatient::getPseudonym).setHeader("External Pseudonym");
        Grid.Column<CachedPatient> patientIdColumn = addColumn(CachedPatient::getPatientId).setHeader("Patient ID");
        Grid.Column<CachedPatient> patientFirstNameColumn = addColumn(CachedPatient::getPatientFirstName).setHeader("Patient first name");
        Grid.Column<CachedPatient> patientLastNameColumn = addColumn(CachedPatient::getPatientLastName).setHeader("Patient last name");
        Grid.Column<CachedPatient> issuerOfPatientIDColumn = addColumn(CachedPatient::getIssuerOfPatientId).setHeader("Issuer of patient ID");
        Grid.Column<CachedPatient> editorColumn = addComponentColumn(patient -> {
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

        editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        editor = getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        externalIdField = new TextField();
        patientIdField = new TextField();
        patientFirstNameField = new TextField();
        patientLastNameField = new TextField();
        issuerOfPatientIdField = new TextField();

        extidColumn.setEditorComponent(externalIdField);
        patientIdColumn.setEditorComponent(patientIdField);
        patientFirstNameColumn.setEditorComponent(patientFirstNameField);
        patientLastNameColumn.setEditorComponent(patientLastNameField);
        issuerOfPatientIDColumn.setEditorComponent(issuerOfPatientIdField);

        deleteColumn = addComponentColumn(patient -> {
            deletePatientButton = new Button("Delete");
            deletePatientButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            deletePatientButton.addClickListener( e -> {
                patientList.remove(patient);
                getDataProvider().refreshAll();
                externalIDCache.remove(PatientClientUtil.generateKey(patient));
            });
            return deletePatientButton;
        });

        saveEditPatientButton = new Button(LABEL_SAVE);
        cancelEditPatientButton = new Button(LABEL_CANCEL);

        Div buttons = new Div(saveEditPatientButton, cancelEditPatientButton);
        editorColumn.setEditorComponent(buttons);
    }

    public Div setBinder(){
        Div validationStatus = new Div();
        validationStatus.setId("validation");
        validationStatus.getStyle().set("color", "var(--theme-color, red)");
        binder.forField(externalIdField)
                .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .withStatusLabel(validationStatus).bind("pseudonym");

        binder.forField(patientIdField)
                .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .withStatusLabel(validationStatus).bind("patientId");

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
                .withStatusLabel(validationStatus).bind("issuerOfPatientId");

        return validationStatus;
    }

    public void setAddPatientButton(Button addPatientButton) {
        this.addPatientButton = addPatientButton;
    }
}
