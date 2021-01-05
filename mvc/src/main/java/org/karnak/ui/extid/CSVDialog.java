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
    private Div divIntro;

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
        add(divTitle, divIntro, fromLine, divContent, readCSVButton, cancelButton);
    }

    private void setElement(){
        divTitle = new Div();
        divTitle.setText("Indicate position of clolumn");
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder").set("padding-bottom", "10px");

        divContent = new Div();
        divIntro = new Div();
        divIntro.setText("Indicate the position of clumn for fields");
        divIntro.getStyle().set("padding-bottom", "10px");

        fromLine = new NumberField("From line ");
        fromLine.setValue(1d);
        fromLine.setHasControls(true);
        fromLine.setMin(1);
        fromLine.setMax((double) allRows.size() + 1);

        externalPseudonymPos = new NumberField("External Pseudonym column");
        externalPseudonymPos.setValue(1d);
        externalPseudonymPos.setHasControls(true);
        externalPseudonymPos.setMin(0);

        patientIDPos = new NumberField("Patient ID column");
        patientIDPos.setValue(1d);
        patientIDPos.setHasControls(true);
        patientIDPos.setMin(0);

        patientNamePos = new NumberField("Patient name column");
        patientNamePos.setValue(1d);
        patientNamePos.setHasControls(true);
        patientNamePos.setMin(0);

        issuerOfPatientIDPos = new NumberField("Issuer of patient ID column");
        issuerOfPatientIDPos.setValue(1d);
        issuerOfPatientIDPos.setHasControls(true);
        issuerOfPatientIDPos.setMin(0);

        readCSVButton = new Button("Read CSV", event -> {
            try {
                //Read CSV line by line and use the string array as you want
                for (String[] row : allRows) {
                    final CachedPatient newPatient = new CachedPatient(row[externalPseudonymPos.getValue().intValue()],
                            row[patientIDPos.getValue().intValue()],
                            row[patientNamePos.getValue().intValue()],
                            row[issuerOfPatientIDPos.getValue().intValue()]);
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
            grid.addColumn(lineArray -> lineArray[idx]).setHeader(headers[idx]);
        }
        grid.setItems(allRows);

        fromLine.addValueChangeListener(numberValue -> {
            if (numberValue.getValue().intValue() > allRows.size()) {
                grid.setItems(allRows.subList(allRows.size(), allRows.size()));
            } else {
                grid.setItems(allRows.subList(numberValue.getValue().intValue()-1, allRows.size()));
            }
        });

    }

}
