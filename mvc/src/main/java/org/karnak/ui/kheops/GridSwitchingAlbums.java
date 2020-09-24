package org.karnak.ui.kheops;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.*;

public class GridSwitchingAlbums extends Grid<KheopsAlbums> {
    private Binder<KheopsAlbums> binder;

    private Collection<Button> editButtons;
    private TextField textUrlAPI;
    private TextField textAuthorizationDestination;
    private TextField textAuthorizationSource;
    private TextField textCondition;

    public GridSwitchingAlbums() {
        setWidthFull();
        setHeightByRows(true);

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
        Editor<KheopsAlbums> editorColumn = getEditor();
        editorColumn.setBinder(binder);

        addComponentColumn(kheopsAlbums -> {
            Button edit = new Button("Edit");
            edit.addClickListener(e -> {
                editorColumn.editItem(kheopsAlbums);
            });
            edit.setEnabled(!editorColumn.isOpen());
            editButtons.add(edit);
            return edit;
        }).setFlexGrow(10);

        addComponentColumn(kheopsAlbums -> {
            Button edit = new Button("Delete");
            edit.addClickListener(e -> {
                editorColumn.editItem(kheopsAlbums);
            });
            edit.setEnabled(!editorColumn.isOpen());
            editButtons.add(edit);
            return edit;
        }).setFlexGrow(10);
    }

    public KheopsAlbums getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(KheopsAlbums data) {
        getDataCommunicator().refresh(data);
    }

    public void initialize(List<KheopsAlbums> data) {
        if (data != null) {
            setItems(data);
        }
    }
}
