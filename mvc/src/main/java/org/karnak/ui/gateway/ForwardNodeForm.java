package org.karnak.ui.gateway;

import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.util.UIS;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A form for editing a single forward node.
 */
@SuppressWarnings("serial")
public class ForwardNodeForm extends Div {
    private final GatewayViewLogic gatewayViewLogic;

    private final DestinationView destinationView;
    private final DestinationLogic destinationLogic;

    private final SourceNodeView sourceNodeView;
    private final SourceNodeLogic sourceNodeLogic;

    private VerticalLayout content;

    private final TextField fwdAeTitle;
    private final TextField description;

    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;
    private Tabs tabs;

    private Binder<ForwardNode> binder;
    private ForwardNode currentForwardNode;

    public ForwardNodeForm(DataService dataService, GatewayViewLogic gatewayViewLogic) {
        this.gatewayViewLogic = gatewayViewLogic;

        this.destinationView = new DestinationView(dataService, gatewayViewLogic);
        this.destinationLogic = destinationView.getDestinationLogic();

        this.sourceNodeView = new SourceNodeView(dataService, gatewayViewLogic);
        this.sourceNodeLogic = sourceNodeView.getSourceNodeLogic();

        setClassName("forwardnode-form");

        content = new VerticalLayout();
        content.setSizeFull();
        add(content);

        fwdAeTitle = new TextField("Forward AETitle");
        fwdAeTitle.setWidth("30%");
        fwdAeTitle.setRequired(true);
        fwdAeTitle.setValueChangeMode(ValueChangeMode.EAGER);
        fwdAeTitle.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        description = new TextField("Description");
        description.setWidth("70%");
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

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(fwdAeTitle, description)));

        Tab sourcesTab = new Tab("Sources");
        Tab destinationsTab = new Tab("Destinations");
        tabs = new Tabs(sourcesTab, destinationsTab);
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSource().getSelectedTab();
            if (selectedTab == sourcesTab) {
                sourceNodeView.setVisible(true);
                destinationView.setVisible(false);
            } else {
                sourceNodeView.setVisible(false);
                destinationView.setVisible(true);
            }
        });
        tabs.setSelectedTab(destinationsTab);

        content.add(UIS.setWidthFull( //
                tabs));
        content.add(UIS.setWidthFull( //
                sourceNodeView));
        content.add(UIS.setWidthFull( //
                destinationView));

        binder = new BeanValidationBinder<>(ForwardNode.class);
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
            if (currentForwardNode != null && binder.writeBeanIfValid(currentForwardNode)) {
                this.gatewayViewLogic.saveForwardNode(currentForwardNode);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(event -> this.gatewayViewLogic.discardForwardNode(currentForwardNode));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> this.gatewayViewLogic.cancelForwardNode());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement().addEventListener("keydown", event -> this.gatewayViewLogic.cancelForwardNode())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentForwardNode != null) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Are you sure to delete the forward node " + currentForwardNode.getFwdAeTitle() + " ?");
                dialog.addConfirmationListener(componentEvent -> this.gatewayViewLogic.deleteForwardNode(currentForwardNode));
                dialog.open();
            }
        });

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(save, discard, delete, cancel)));
    }

    public void editForwardNode(ForwardNode data) {
        if (data == null) {
            data = ForwardNode.ofEmpty();
        }
        delete.setVisible(!data.isNewData());
        currentForwardNode = data;
        destinationLogic.init(data);
        sourceNodeLogic.init(data);
        binder.readBean(data);
    }

    public void validateView() {
        binder.validate();
    }

    public boolean hasChanges() {
        return binder.hasChanges() || destinationView.hasChanges() || sourceNodeView.hasChanges();
    }

    public void showForm(boolean show){
        fwdAeTitle.setVisible(show);
        description.setVisible(show);
        tabs.setVisible(show);
        save.setVisible(show);
        discard.setVisible(show);
        delete.setVisible(show);
        cancel.setVisible(show);
    }
}
