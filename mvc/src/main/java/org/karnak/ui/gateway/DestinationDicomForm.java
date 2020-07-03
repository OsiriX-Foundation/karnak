package org.karnak.ui.gateway;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.component.converter.HStringToIntegerConverter;
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
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A form for editing a single destination.
 */
@SuppressWarnings("serial")
public class DestinationDicomForm extends Div {
    private DestinationLogic viewLogic;

    private VerticalLayout content;

    private final TextField description;
    private final TextField aeTitle;
    private final TextField hostname;
    private final TextField port;
    private final Checkbox useaetdest;

    private final TextField notify;
    private final TextField notifyObjectErrorPrefix;
    private final TextField notifyObjectPattern;
    private final TextField notifyObjectValues;
    private final TextField notifyInterval;

    private final Checkbox desidentification;

    private Button update;
    private Button discard;
    private Button cancel;
    private Button remove;

    private Binder<Destination> binder;
    private Destination currentDestination;

    public DestinationDicomForm(DestinationLogic viewLogic) {
        this.viewLogic = viewLogic;

        setClassName("destination-form");

        content = new VerticalLayout();
        content.setSizeFull();
        add(content);

        aeTitle = new TextField("AETitle");
        aeTitle.setWidth("30%");
        aeTitle.setValueChangeMode(ValueChangeMode.EAGER);
        aeTitle.addBlurListener(e -> {
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
            UIS.setTooltip(tf, tf.getValue());
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

        port = new TextField("Port");
        port.setWidth("30%");
        port.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        port.setValueChangeMode(ValueChangeMode.EAGER);

        useaetdest = new Checkbox("Use AETitle destination");
        UIS.setTooltip(useaetdest,
                "if \"true\" then use the destination AETitle as the calling  AETitle at the gateway side");

        notify = new TextField("Notif.: list of emails");
        notify.setWidth("100%");
        notify.setValueChangeMode(ValueChangeMode.EAGER);
        notify.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        notify.addValueChangeListener(e -> {
            TextField tf = e.getSource();
            UIS.setTooltip(tf, tf.getValue());
        });

        notifyObjectErrorPrefix = new TextField("Notif.: error subject prefix");
        notifyObjectErrorPrefix.setWidth("24%");
        notifyObjectErrorPrefix.setValueChangeMode(ValueChangeMode.EAGER);
        notifyObjectErrorPrefix.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(notifyObjectErrorPrefix,
                "Prefix of the email object when containing an issue. Default value: **ERROR**");

        notifyObjectPattern = new TextField("Notif.: subject pattern");
        notifyObjectPattern.setWidth("24%");
        notifyObjectPattern.setValueChangeMode(ValueChangeMode.EAGER);
        notifyObjectPattern.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(notifyObjectPattern,
                "Pattern of the email object, see https://dzone.com/articles/java-string-format-examples. Default value: [Karnak Notification] %s %.30s");

        notifyObjectValues = new TextField("Notif.: subject values");
        notifyObjectValues.setWidth("24%");
        notifyObjectValues.setValueChangeMode(ValueChangeMode.EAGER);
        notifyObjectValues.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(notifyObjectValues,
                "Values injected in the pattern [PatientID StudyDescription StudyDate StudyInstanceUID]. Default value: PatientID,StudyDescription");

        notifyInterval = new TextField("Notif.: interval");
        notifyInterval.setWidth("18%");
        notifyInterval.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        notifyInterval.setValueChangeMode(ValueChangeMode.EAGER);
        UIS.setTooltip(notifyInterval,
                "Interval in seconds for sending a notification (when no new image is arrived in the archive folder). Default value: 45");

        desidentification = new Checkbox();
        desidentification.setLabel("Activate de-identification");
        desidentification.setValue(true);

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(aeTitle, description)));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(hostname, port)));
        content.add(UIS.setWidthFull( //
                useaetdest));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(notify)));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(notifyObjectErrorPrefix, notifyObjectPattern, notifyObjectValues,
                        notifyInterval)));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(desidentification)));

        binder = new BeanValidationBinder<>(Destination.class);
        // Define the same validators as the Destination class, because the validation
        // bean doesn't work in Vaadin
        binder.forField(aeTitle) //
                .withValidator( //
                        StringUtils::isNotBlank, //
                        "AETitle is mandatory") //
                .withValidator( //
                        value -> value.length() <= 16, //
                        "AETitle has more than 16 characters") //
                .withValidator( //
                        UIS::containsNoWhitespace, //
                        "AETitle contains white spaces") //
                .bind(Destination::getAeTitle, Destination::setAeTitle);
        binder.forField(hostname) //
                .withValidator( //
                        StringUtils::isNotBlank, //
                        "Hostname is mandatory") //
                .bind(Destination::getHostname, Destination::setHostname);
        binder.forField(port) //
                .withConverter(new HStringToIntegerConverter()) //
                .withValidator( //
                        Objects::nonNull, //
                        "Port is mandatory") //
                .withValidator( //
                        value -> 1 <= value && value <= 65535, //
                        "Port should be between 1 and 65535") //
                .bind(Destination::getPort, Destination::setPort);
        binder.forField(notifyInterval) //
                .withConverter(new HStringToIntegerConverter()) //
                .bind(Destination::getNotifyInterval, Destination::setNotifyInterval);
        binder.forField(desidentification) //
                .bind(Destination::getDesidentification, Destination::setDesidentification);
        binder.bindInstanceFields(this);

        // enable/disable update button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            update.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
            remove.setEnabled(!hasChanges);
        });

        update = new Button("Update");
        update.setWidthFull();
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(event -> {
            if (currentDestination != null && binder.writeBeanIfValid(currentDestination)) {
                this.viewLogic.saveDestination(currentDestination);
            }
        });
        update.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(event -> this.viewLogic.editDestination(currentDestination));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> this.viewLogic.cancelDestination());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement().addEventListener("keydown", event -> this.viewLogic.cancelDestination())
                .setFilter("event.key == 'Escape'");

        remove = new Button("Remove");
        remove.setWidth("100%");
        remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        remove.addClickListener(event -> {
            if (currentDestination != null) {
                this.viewLogic.deleteDestination(currentDestination);
            }
        });

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(update, discard, remove, cancel)));
    }

    public void editDestination(Destination data) {
        remove.setVisible(data != null);
        cancel.setVisible(data == null);
        if (data == null) {
            data = Destination.ofDicomEmpty();
        }
        currentDestination = data;
        binder.readBean(data);
    }
}
