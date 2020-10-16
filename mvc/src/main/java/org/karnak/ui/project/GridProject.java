package org.karnak.ui.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.codec.binary.Hex;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.Project;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.data.ProjectDataProvider;
import org.karnak.ui.forwardnode.ProfileDropDown;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

public class GridProject extends Grid<Project> {
    private ProjectDataProvider projectDataProvider;
    private Binder<Project> binder;

    private Editor<Project> editor;
    private Collection<Button> editButtons;
    private TextField textProjectName;
    private TextField textProjectSecret;
    private ProfileDropDown profileDropDown;
    private WarningRemoveProjectUsed dialogWarning;

    public GridProject(ProjectDataProvider projectDataProvider) {
        this.projectDataProvider = projectDataProvider;
        setDataProvider(this.projectDataProvider);
        setWidthFull();
        setHeightByRows(true);

        dialogWarning = new WarningRemoveProjectUsed();
        TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
        binder = textFieldsBindProject.getBinder();
        textProjectName = textFieldsBindProject.getTextResearchName();
        textProjectSecret = textFieldsBindProject.getTextSecret();
        profileDropDown = textFieldsBindProject.getProfileDropDown();
        editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        addColumn(Project::getName).setHeader("Project Name").setFlexGrow(15)
                .setSortable(true).setEditorComponent(textProjectName);
        addColumn(project -> Hex.encodeHexString(project.getSecret())).setHeader("Project Secret").setFlexGrow(15)
                .setSortable(true).setEditorComponent(textProjectSecret);
        addColumn(project -> project.getProfile().getName()).setHeader("Desidenfication profile").setFlexGrow(15)
                .setSortable(true).setEditorComponent(profileDropDown);

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
                List<Destination> destinations = project.getDestinations();
                if (destinations != null && destinations.size() > 0) {
                    dialogWarning.setText(project);
                    dialogWarning.open();

                } else {
                    projectDataProvider.remove(project);
                }
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

        Button save = new Button("Save", e -> {
            Project project = editor.getItem();
            if (project.getDestinations() != null && project.getDestinations().size() > 0) {
                ConfirmDialog dialog = new ConfirmDialog(
                        String.format("The project %s is used, are you sure you want to updated ?", project.getName()));
                dialog.addConfirmationListener(componentEvent -> {
                    editor.save();
                });
                dialog.open();
            } else {
                editor.save();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        editor.addSaveListener(
                event -> projectDataProvider.update(event.getItem())
        );

        Button cancel = new Button("Cancel", e -> editor.cancel());

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);
    }

    public void clear() {
        editButtons.clear();
        editor.cancel();
    }
}
