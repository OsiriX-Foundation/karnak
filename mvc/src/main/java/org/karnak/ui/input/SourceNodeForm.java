package org.karnak.ui.input;

import org.karnak.data.input.SourceNode;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.util.UIS;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A form for editing a single source node.
 */
@SuppressWarnings("serial")
public class SourceNodeForm extends Div {
    private final InputLogic viewLogic;

    private final DestinationView destinationView;
    private final DestinationLogic destinationLogic;

    private VerticalLayout content;

    private final TextField srcAeTitle;
    private final TextField dstAeTitle;
    private final TextField description;
    private final TextField hostname;
    private final Checkbox checkHostname;

    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private Binder<SourceNode> binder;
    private SourceNode currentSourceNode;

    public SourceNodeForm(DataService dataService, InputLogic viewLogic) {
        this.viewLogic = viewLogic;

        this.destinationView = new DestinationView(dataService, viewLogic);
        this.destinationLogic = destinationView.getViewLogic();

        setClassName("inputsourcenode-form");

        content = new VerticalLayout();
        content.setSizeFull();
        add(content);

        srcAeTitle = new TextField("Source AETitle");
        srcAeTitle.setWidth("20%");
        srcAeTitle.setRequired(true);
        srcAeTitle.setValueChangeMode(ValueChangeMode.EAGER);
        srcAeTitle.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        dstAeTitle = new TextField("Destination AETitle");
        dstAeTitle.setWidth("20%");
        dstAeTitle.setRequired(true);
        dstAeTitle.setValueChangeMode(ValueChangeMode.EAGER);
        dstAeTitle.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        description = new TextField("Description");
        description.setWidth("60%");
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        description.addValueChangeListener(e -> {
            TextField tf = e.getSource();
            tf.getElement().setAttribute("title", tf.getValue());
        });

        hostname = new TextField("Hostname");
        hostname.setWidth("70%");
        hostname.setRequired(true);
        hostname.setValueChangeMode(ValueChangeMode.EAGER);
        hostname.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        checkHostname = new Checkbox("Check the hostname");
        UIS.setTooltip(checkHostname,
                "if \"true\" check the hostname during the DICOM association and if not match the connection is abort");

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(srcAeTitle, dstAeTitle, description)));
        content.add(UIS.setWidthFull( //
                hostname));
        content.add(UIS.setWidthFull( //
                checkHostname));

        content.add(UIS.setWidthFull( //
                destinationView));

        binder = new BeanValidationBinder<>(SourceNode.class);
        binder.bindInstanceFields(this);

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = hasChanges();
            save.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
            delete.setEnabled(!hasChanges);
        });

        save = new Button("Save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentSourceNode != null && binder.writeBeanIfValid(currentSourceNode)) {
                this.viewLogic.saveSourceNode(currentSourceNode);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(event -> this.viewLogic.discardSourceNode(currentSourceNode));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> this.viewLogic.cancelSourceNode());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement().addEventListener("keydown", event -> this.viewLogic.cancelSourceNode())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentSourceNode != null) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Are you sure to delete the source node " + currentSourceNode.getStringReference() + " ?");
                dialog.addConfirmationListener(componentEvent -> this.viewLogic.deleteSourceNode(currentSourceNode));
                dialog.open();
            }
        });

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(save, discard, delete, cancel)));
    }

    public void editSourceNode(SourceNode data) {
        if (data == null) {
            data = SourceNode.ofEmpty();
        }
        delete.setVisible(!data.isNewData());
        cancel.setVisible(data.isNewData());
        currentSourceNode = data;
        destinationLogic.init(data);
        binder.readBean(data);
    }

    public void validateView() {
        binder.validate();
    }

    public boolean hasChanges() {
        return binder.hasChanges() || destinationView.hasChanges();
    }
}
