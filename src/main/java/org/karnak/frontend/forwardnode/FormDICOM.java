package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.frontend.component.converter.HStringToIntegerConverter;
import org.karnak.frontend.util.UIS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormDICOM extends VerticalLayout {

    private Binder<DestinationEntity> binder;

    private TextField aeTitle;
    private TextField description;
    private TextField hostname;
    private TextField port;
    private Checkbox useaetdest;

    private TextField notify;
    private TextField notifyObjectErrorPrefix;
    private TextField notifyObjectPattern;
    private TextField notifyObjectValues;
    private TextField notifyInterval;
    private final LayoutDesidentification layoutDesidentification;
    private FilterBySOPClassesForm filterBySOPClassesForm;

    @Autowired
    public FormDICOM(final LayoutDesidentification layoutDesidentification) {
        this.layoutDesidentification = layoutDesidentification;
    }

    public void init(Binder<DestinationEntity> binder,
        ButtonSaveDeleteCancel buttonSaveDeleteCancel) {

        this.layoutDesidentification.init(binder);
        this.binder = binder;

        setSizeFull();

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
        filterBySOPClassesForm = new FilterBySOPClassesForm(binder);

        add(UIS.setWidthFull(new HorizontalLayout(aeTitle, description)),
            UIS.setWidthFull(new HorizontalLayout(hostname, port)),
            UIS.setWidthFull(new HorizontalLayout(useaetdest)),
            UIS.setWidthFull(new HorizontalLayout(notify)),
            UIS.setWidthFull(new HorizontalLayout(notifyObjectErrorPrefix, notifyObjectPattern,
                notifyObjectValues, notifyInterval)),
            UIS.setWidthFull(layoutDesidentification),
            UIS.setWidthFull(filterBySOPClassesForm),
            UIS.setWidthFull(buttonSaveDeleteCancel));

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
        binder.forField(aeTitle).withValidator(StringUtils::isNotBlank, "AETitle is mandatory")
            .withValidator(value -> value.length() <= 16, "AETitle has more than 16 characters")
            .withValidator(UIS::containsNoWhitespace, "AETitle contains white spaces")
            .bind(DestinationEntity::getAeTitle, DestinationEntity::setAeTitle);

        binder.forField(hostname).withValidator(StringUtils::isNotBlank, "Hostname is mandatory")
            .bind(DestinationEntity::getHostname, DestinationEntity::setHostname);
        binder.forField(port).withConverter(new HStringToIntegerConverter())
            .withValidator(Objects::nonNull, "Port is mandatory")
            .withValidator(value -> 1 <= value && value <= 65535,
                "Port should be between 1 and 65535")
            .bind(DestinationEntity::getPort, DestinationEntity::setPort);
        binder.forField(notifyInterval) //
            .withConverter(new HStringToIntegerConverter()) //
            .bind(DestinationEntity::getNotifyInterval, DestinationEntity::setNotifyInterval);

        binder.bindInstanceFields(this);

    }
}
