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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVDialog extends Dialog {

    private static final String EXTERNAL_PSEUDONYM = "External Pseudonym";
    private static final String PATIENT_ID = "Patient ID";
    private static final String PATIENT_NAME = "Patient name";
    private static final String ISSUER_OF_PATIENT_ID = "Issuer of patient ID";

    private NumberField fromLine;

    private Button readCSVButton;
    private Button cancelButton;
    private Div divContent;
    private Div errorMsg;
    private Div divTitle;

    private Grid<String[]> grid;

    private List<Select<String>> listOfSelect;

    private transient PatientClient externalIDCache;
    private transient CSVReader csvReader;

    private List<String[]> allRows;
    private final String[] selectValues = {"", EXTERNAL_PSEUDONYM, PATIENT_ID, PATIENT_NAME, ISSUER_OF_PATIENT_ID};
    private HashMap<String, Integer> hashMap;

    public CSVDialog(InputStream inputStream, char separator) {
        removeAll();
        externalIDCache = AppConfig.getInstance().getExternalIDCache();

        allRows = null;
        try {
            csvReader = new CSVReader(new InputStreamReader(inputStream), separator);
            allRows = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setElement();
        buildGrid();

        divContent.add(grid);
        add(divTitle, fromLine, divContent, errorMsg,readCSVButton, cancelButton);
    }

    private void setElement(){
        divTitle = new Div();
        divTitle.setText("Upload CSV for the table that contain external pseudonym");
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder").set("padding-bottom", "10px");

        divContent = new Div();
        errorMsg = new Div();
        errorMsg.getStyle().set("font-weight", "bolder").set("padding-bottom", "10px").set("color", "red");

        fromLine = new NumberField("From line ");
        fromLine.setValue(1d);
        fromLine.setHasControls(true);
        fromLine.setMin(1);
        fromLine.setMax((double) allRows.size() + 1);


        readCSVButton = new Button("Read CSV", event -> {
            if (hashMap.get(EXTERNAL_PSEUDONYM).equals(-1) || hashMap.get(PATIENT_ID).equals(-1) ||
                    hashMap.get(PATIENT_NAME).equals(-1)){
                generateErrorMsg();
            } else {
                readCSVAndPushInCache();
                close();
            }

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
                    String valueInHashMap = getValueWithKey(hashMap, currentPosition);
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

    public <K, V> String getValueWithKey(Map<K, V> map, V value) {
        Stream<K> keyStream1 = map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);

        return (String) keyStream1.findFirst().orElse(null);
    }

    private void generateErrorMsg() {
        final Stream<String> streamFieldNotSelected = hashMap.entrySet().stream().map(stringIntegerEntry -> {
            if(stringIntegerEntry.getValue().equals(-1) && !stringIntegerEntry.getKey().equals("") &&
                    !stringIntegerEntry.getKey().equals(ISSUER_OF_PATIENT_ID)) {
                return stringIntegerEntry.getKey();
            } else {
                return "";
            }
        }).filter(s -> !s.equals(""));
        final String concatFieldNotSelected = streamFieldNotSelected.collect(Collectors.joining(", "));
        errorMsg.setText(String.format("This fields are not selected: %s", concatFieldNotSelected));
    }

    private void readCSVAndPushInCache() {
        try {
            //Read CSV line by line and use the string array as you want
            for (String[] row : allRows.subList(fromLine.getValue().intValue() - 1, allRows.size())) {
                String issuerOfPatientID = hashMap.get(ISSUER_OF_PATIENT_ID).equals(-1) ? "" : row[hashMap.get(ISSUER_OF_PATIENT_ID)];
                final CachedPatient newPatient = new CachedPatient(row[hashMap.get(EXTERNAL_PSEUDONYM)],
                        row[hashMap.get(PATIENT_ID)],
                        row[hashMap.get(PATIENT_NAME)],
                        issuerOfPatientID);
                externalIDCache.put(PatientClientUtil.generateKey(newPatient), newPatient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
