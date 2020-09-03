package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.component.converter.HStringToIntegerConverter;
import org.karnak.ui.util.UIS;

import java.util.Objects;

public class FormDICOM extends VerticalLayout {
    private Binder<Destination> binder;
    private Destination currentDestination;

    private final TextField aeTitle;
    private final TextField description;
    private final TextField hostname;
    private final TextField port;
    private final Checkbox useaetdest;

    private final TextField notify;
    private final TextField notifyObjectErrorPrefix;
    private final TextField notifyObjectPattern;
    private final TextField notifyObjectValues;
    private final TextField notifyInterval;

    public FormDICOM(Binder<Destination> binder) {
        setSizeFull();
        this.binder = binder;

        aeTitle = new TextField("AETitle");
        description = new TextField("Description");
        hostname = new TextField("Hostname");
        port = new TextField("Port");
        useaetdest = new Checkbox("Use AETitle destination");
        notify = new TextField("Notif.: list of emails");
        notifyObjectErrorPrefix = new TextField("Notif.: error subject prefix");
        notifyObjectPattern = new TextField("Notif.: subject pattern");
        notifyObjectValues = new TextField("Notif.: subject values");
        notifyInterval = new TextField("Notif.: interval");

        add(UIS.setWidthFull(new HorizontalLayout(aeTitle, description)),
                UIS.setWidthFull(new HorizontalLayout(hostname, port)),
                UIS.setWidthFull(new HorizontalLayout(useaetdest)),
                UIS.setWidthFull(new HorizontalLayout(notify)),
                UIS.setWidthFull(new HorizontalLayout(notifyObjectErrorPrefix, notifyObjectPattern, notifyObjectValues, notifyInterval)));


        setElements();
        setBinder();
    }

    private void setElements() {
        aeTitle.setWidth("30%");

        description.setWidth("70%");

        hostname.setWidth("70%");
        hostname.setRequired(true);

        port.setWidth("30%");
        port.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        UIS.setTooltip(useaetdest,
                "if \"true\" then use the destination AETitle as the calling  AETitle at the gateway side");

        notify.setWidth("100%");

        notifyObjectErrorPrefix.setWidth("24%");
        UIS.setTooltip(notifyObjectErrorPrefix,
                "Prefix of the email object when containing an issue. Default value: **ERROR**");

        notifyObjectPattern.setWidth("24%");
        UIS.setTooltip(notifyObjectPattern,
                "Pattern of the email object, see https://dzone.com/articles/java-string-format-examples. Default value: [Karnak Notification] %s %.30s");

        notifyObjectValues.setWidth("24%");
        UIS.setTooltip(notifyObjectValues,
                "Values injected in the pattern [PatientID StudyDescription StudyDate StudyInstanceUID]. Default value: PatientID,StudyDescription");

        notifyInterval.setWidth("18%");
        notifyInterval.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        UIS.setTooltip(notifyInterval,
                "Interval in seconds for sending a notification (when no new image is arrived in the archive folder). Default value: 45");
    }

    private void setBinder() {
        binder.forField(aeTitle).withValidator( StringUtils::isNotBlank, "AETitle is mandatory")
            .withValidator(value -> value.length() <= 16, "AETitle has more than 16 characters")
            .withValidator(UIS::containsNoWhitespace, "AETitle contains white spaces")
            .bind(Destination::getAeTitle, Destination::setAeTitle);

        binder.forField(hostname).withValidator( StringUtils::isNotBlank, "Hostname is mandatory")
                .bind(Destination::getHostname, Destination::setHostname);
        binder.forField(port).withConverter(new HStringToIntegerConverter())
            .withValidator(Objects::nonNull,"Port is mandatory")
                .withValidator(value -> 1 <= value && value <= 65535, "Port should be between 1 and 65535")
            .bind(Destination::getPort, Destination::setPort);
        binder.forField(notifyInterval) //
            .withConverter(new HStringToIntegerConverter()) //
            .bind(Destination::getNotifyInterval, Destination::setNotifyInterval);

        binder.bindInstanceFields(this);

    }
}
