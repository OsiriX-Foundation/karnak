package org.karnak.ui.kheops;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.*;

public class GridSwitchingAlbums extends Grid<KheopsAlbums> {
    private Binder<KheopsAlbums> binder;

    private Editor<KheopsAlbums> editor;
    private Collection<Button> editButtons;
    private TextField textUrlAPI;
    private TextField textAuthorizationDestination;
    private TextField textAuthorizationSource;
    private TextField textCondition;

    public GridSwitchingAlbums() {
        setWidthFull();
        setHeightByRows(true);
        setItems(new ArrayList<>());

        TextFieldsBindSwitchingAlbum textFieldsBindSwitchingAlbum = new TextFieldsBindSwitchingAlbum();
        binder = textFieldsBindSwitchingAlbum.getBinder();
        textUrlAPI = textFieldsBindSwitchingAlbum.getTextUrlAPI();
        textAuthorizationDestination = textFieldsBindSwitchingAlbum.getTextAuthorizationDestination();
        textAuthorizationSource = textFieldsBindSwitchingAlbum.getTextAuthorizationSource();
        textCondition = textFieldsBindSwitchingAlbum.getTextCondition();
        editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        addColumn(KheopsAlbums::getUrlAPI).setHeader("URL API").setFlexGrow(20)
                .setSortable(true).setEditorComponent(textUrlAPI);

        addColumn(KheopsAlbums::getAuthorizationDestination).setHeader("Token destination").setFlexGrow(20)
                .setSortable(true).setEditorComponent(textAuthorizationDestination);

        addColumn(KheopsAlbums::getAuthorizationSource).setHeader("Token source").setFlexGrow(20)
                .setSortable(true).setEditorComponent(textAuthorizationSource);

        addColumn(KheopsAlbums::getCondition).setHeader("Condition").setFlexGrow(20)
                .setSortable(true).setEditorComponent(textCondition);

        setEditorColumn();
    }

    private void setEditorColumn() {
        editor = getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Column<KheopsAlbums> editorColumn = addComponentColumn(kheopsAlbums -> {
            Button edit = new Button("Edit");
            edit.addClickListener(e -> {
                editor.editItem(kheopsAlbums);
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });
        editor.addOpenListener(e -> editButtons.stream()
            .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
            .forEach(button -> button.setEnabled(!editor.isOpen())));
        Button save = new Button("Save", e -> editor.save());
        Button cancel = new Button("Cancel", e -> editor.cancel());

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);
    }

    public void clearEditorEditButtons() {
        editButtons.clear();
        editor.cancel();
    }

    public KheopsAlbums getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(KheopsAlbums data) {
        getDataCommunicator().refresh(data);
    }
}
