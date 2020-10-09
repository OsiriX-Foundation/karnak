package org.karnak.ui.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Project;
import org.karnak.ui.data.ProjectDataProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

public class GridProject extends Grid<Project> {
    private ProjectDataProvider projectDataProvider;
    private Binder<Project> binder;

    private Editor<Project> editor;
    private Collection<Button> editButtons;
    private TextField textProjectName;
    private TextField textProjectSecret;

    public GridProject(ProjectDataProvider projectDataProvider) {
        this.projectDataProvider = projectDataProvider;
        setDataProvider(this.projectDataProvider);
        setWidthFull();
        setHeightByRows(true);

        TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
        binder = textFieldsBindProject.getBinder();
        textProjectName = textFieldsBindProject.getTextResearchName();
        textProjectSecret = textFieldsBindProject.getTextSecret();
        editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        addColumn(Project::getName).setHeader("Project Name").setFlexGrow(15)
                .setSortable(true).setEditorComponent(textProjectName);
        addColumn(Project::getSecret).setHeader("Project Secret").setFlexGrow(15)
                .setSortable(true).setEditorComponent(textProjectSecret);

        setEditorColumn();
    }

    private void setEditorColumn() {
        editor = getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Column<Project> editorColumn = addComponentColumn(project -> {
            Button edit = new Button("Edit");
            edit.addClickListener(e -> {
                editor.editItem(project);
            });
            edit.setEnabled(!editor.isOpen());

            Button remove = new Button("Remove");
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            remove.addClickListener(e -> {

            });
            remove.setEnabled(!editor.isOpen());

            editButtons.add(edit);
            editButtons.add(remove);
            return new Div(edit, remove);
        }).setFlexGrow(15);

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Save", e -> editor.save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        editor.addSaveListener(
                event -> projectDataProvider.update(event.getItem())
        );

        Button cancel = new Button("Cancel", e -> editor.cancel());

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);
    }
}
