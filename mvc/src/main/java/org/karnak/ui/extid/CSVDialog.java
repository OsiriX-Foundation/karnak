package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;

import liquibase.util.csv.opencsv.CSVReader;
import org.karnak.cache.CachedPatient;
import org.karnak.cache.PatientClient;
import org.karnak.cache.PatientClientUtil;
import org.karnak.data.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CSVDialog extends Dialog {

    private NumberField fromLine;

    private Button readCSVButton;
    private Button cancelButton;
    private Div divContent;
    private Div divTitle;

    private Grid<String[]> grid;

    private List<Select<String>> listOfSelect;

    private transient PatientClient externalIDCache;

    private List<String[]> allRows;
    private final String[] selectValues = {"", "External Pseudonym", "Patient ID", "Patient name", "Issuer of patient ID"};
    private HashMap<String, Integer> hashMap;

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


        readCSVButton = new Button("Read CSV", event -> {
            try {
                //Read CSV line by line and use the string array as you want
                for (String[] row : allRows.subList(fromLine.getValue().intValue() - 1, allRows.size())) {
                    final CachedPatient newPatient = new CachedPatient(row[hashMap.get("External Pseudonym")],
                            row[hashMap.get("Patient ID")],
                            row[hashMap.get("Patient name")],
                            row[hashMap.get("Issuer of patient ID")]);
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
        listOfSelect = new ArrayList<>();

        hashMap = new HashMap<>();

        for(String val: selectValues) {
            hashMap.put(val, -1);
        }

        for (int i = 0; i < headers.length; i++) {
            int idx = i;
            Select<String> currentSelect = new Select<>();
            currentSelect.setId(String.format("%d",i));
            currentSelect.setItems(selectValues);
            currentSelect.addValueChangeListener(value -> {
                int currentPosition = Integer.parseInt(currentSelect.getId().orElse("-1"));
                //reset value of old key
                if (hashMap.containsValue(currentPosition)) {
                    String valueInHashMap = getKey(hashMap, currentPosition);
                    if (valueInHashMap != null) {
                        hashMap.replace(valueInHashMap, -1);
                    }
                }
                //update new key
                hashMap.replace(value.getValue(), currentPosition);
                updateSelectGrid();
            });
            listOfSelect.add(currentSelect);
            grid.addColumn(lineArray -> lineArray[idx]).setHeader(currentSelect);
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

    private void updateSelectGrid() {
        for(Select<String> currentSelect : listOfSelect) {
            int currentPosition = Integer.parseInt(currentSelect.getId().orElse("-1"));

            currentSelect.setItemEnabledProvider(item -> hashMap.get(item).equals(-1) ||
                    hashMap.get(item).equals(currentPosition) || item.equals("")

            );
        }

    }

    public <K, V> String getKey(Map<K, V> map, V value) {
        Stream<K> keyStream1 = map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);

        return (String) keyStream1.findFirst().orElse(null);
    }

}
