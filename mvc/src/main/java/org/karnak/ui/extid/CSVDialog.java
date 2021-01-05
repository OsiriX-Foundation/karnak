package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;

import liquibase.util.csv.opencsv.CSVReader;
import org.karnak.cache.CachedPatient;
import org.karnak.cache.PatientClient;
import org.karnak.cache.PatientClientUtil;
import org.karnak.data.AppConfig;

import java.io.IOException;
import java.util.List;

public class CSVDialog extends Dialog {

    private NumberField externalPseudonymPos;
    private NumberField patientIDPos;
    private NumberField patientNamePos;
    private NumberField issuerOfPatientIDPos;
    private NumberField fromLine;

    private Button readCSVButton;
    private Button cancelButton;
    private Div divContent;
    private Div divTitle;

    private transient PatientClient externalIDCache;

    private List<String[]> allRows;

    private Grid<String[]> grid;

    public CSVDialog(CSVReader csvReader) {
        removeAll();
        externalIDCache = AppConfig.getInstance().getExternalIDCache();

        allRows = null;
        try {
            allRows = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setElement();
        buildGrid();

        divContent.add(externalPseudonymPos);
        divContent.add(patientIDPos);
        divContent.add(patientNamePos);
        divContent.add(issuerOfPatientIDPos);
        divContent.add(grid);
        add(divTitle, fromLine, divContent, readCSVButton, cancelButton);
    }

    private void setElement(){
        divTitle = new Div();
        divTitle.setText("Upload CSV for the table that contain external pseudonym");
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder").set("padding-bottom", "10px");

        divContent = new Div();

        fromLine = new NumberField("From line ");
        fromLine.setValue(1d);
        fromLine.setHasControls(true);
        fromLine.setMin(1);
        fromLine.setMax((double) allRows.size() + 1);

        externalPseudonymPos = new NumberField("Select the column number of the external pseudonym");
        externalPseudonymPos.setValue(1d);
        externalPseudonymPos.setHasControls(true);
        externalPseudonymPos.setMin(1);
        externalPseudonymPos.setWidthFull();

        patientIDPos = new NumberField("Select the column number of the patient ID");
        patientIDPos.setValue(2d);
        patientIDPos.setHasControls(true);
        patientIDPos.setMin(1);
        patientIDPos.setWidthFull();

        patientNamePos = new NumberField("Select the column number of the patient name");
        patientNamePos.setValue(3d);
        patientNamePos.setHasControls(true);
        patientNamePos.setMin(1);
        patientNamePos.setWidthFull();

        issuerOfPatientIDPos = new NumberField("Select the column number of the issuer of patient ID");
        issuerOfPatientIDPos.setValue(4d);
        issuerOfPatientIDPos.setHasControls(true);
        issuerOfPatientIDPos.setMin(1);
        issuerOfPatientIDPos.setWidthFull();

        readCSVButton = new Button("Read CSV", event -> {
            try {
                //Read CSV line by line and use the string array as you want
                for (String[] row : allRows.subList(fromLine.getValue().intValue() - 1, allRows.size())) {
                    final CachedPatient newPatient = new CachedPatient(row[externalPseudonymPos.getValue().intValue() - 1],
                            row[patientIDPos.getValue().intValue() - 1],
                            row[patientNamePos.getValue().intValue() - 1],
                            row[issuerOfPatientIDPos.getValue().intValue() - 1]);
                    externalIDCache.put(PatientClientUtil.generateKey(newPatient), newPatient);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            close();
        });

        cancelButton = new Button("Cancel", event -> close());

        cancelButton.getStyle().set("margin-left", "75%");
    }

    public void buildGrid() {
        grid = new Grid<>();

        String[] headers = allRows.get(0);

        for (int i = 0; i < headers.length; i++) {
            int idx = i;
            grid.addColumn(lineArray -> lineArray[idx]).setHeader(String.format("Column %d", i + 1));
        }

        grid.setItems(allRows);
        grid.recalculateColumnWidths();
        fromLine.addValueChangeListener(numberValue -> {
            if (numberValue.getValue().intValue() > allRows.size()) {
                grid.setItems(allRows.subList(allRows.size(), allRows.size()));
            } else {
                grid.setItems(allRows.subList(numberValue.getValue().intValue()-1, allRows.size()));
            }
        });

    }

}
