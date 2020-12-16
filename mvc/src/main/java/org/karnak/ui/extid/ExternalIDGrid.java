package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.apache.commons.lang3.StringUtils;
import org.karnak.cache.CachedPatient;
import org.karnak.cache.PatientClient;
import org.karnak.data.AppConfig;
import org.karnak.cache.PatientClientUtil;

import java.util.*;

public class ExternalIDGrid extends Grid<CachedPatient> {
    private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";
    private Binder<CachedPatient> binder;
    private List<CachedPatient> patientList;
    private Button addNewPatientButton;
    private Button deletePatientButton;
    private Button saveEditPatientButton;
    private Button cancelEditPatientButton;

    private Editor<CachedPatient> editor;
    private Collection<Button> editButtons;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientNameField;
    private TextField issuerOfPatientIdField;

    private Grid.Column<CachedPatient> deleteColumn;

    private static final String LABEL_SAVE = "Save";
    private static final String LABEL_CANCEL = "Cancel";
    private transient PatientClient externalIDCache;

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
            addNewPatientButton.setVisible(false);
        });

        editor.addCloseListener(e -> {
            editButtons.stream()
                    .forEach(button -> button.setEnabled(!editor.isOpen()));
            deleteColumn.setVisible(true);
            addNewPatientButton.setVisible(true);
        });

        saveEditPatientButton.addClickListener(e -> {
            final CachedPatient patientEdit = new CachedPatient(
                    externalIdField.getValue(),
                    patientIdField.getValue(),
                    patientNameField.getValue(),
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
        Grid.Column<CachedPatient> patientNameColumn = addColumn(CachedPatient::getPatientName).setHeader("Patient name");
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
        patientNameField = new TextField();
        issuerOfPatientIdField = new TextField();

        extidColumn.setEditorComponent(externalIdField);
        patientIdColumn.setEditorComponent(patientIdField);
        patientNameColumn.setEditorComponent(patientNameField);
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

        binder.forField(patientNameField)
                .withValidator(StringUtils::isNotBlank, "Patient name is empty")
                .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
                .withStatusLabel(validationStatus).bind("patientName");

        binder.forField(issuerOfPatientIdField)
                .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
                .withStatusLabel(validationStatus).bind("issuerOfPatientId");

        return validationStatus;
    }

    public void setAddNewPatientButton(Button addNewPatientButton) {
        this.addNewPatientButton = addNewPatientButton;
    }
}
