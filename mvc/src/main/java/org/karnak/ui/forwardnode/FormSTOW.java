package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.component.converter.HStringToIntegerConverter;
import org.karnak.ui.util.UIS;

public class FormSTOW extends VerticalLayout  {
    private Binder<Destination> binder;
    private final TextField description;
    private final TextField url;
    private final TextField urlCredentials;
    private final TextArea headers;

    private final TextField notify;
    private final TextField notifyObjectErrorPrefix;
    private final TextField notifyObjectPattern;
    private final TextField notifyObjectValues;
    private final TextField notifyInterval;

    public FormSTOW(Binder<Destination> binder) {
        setSizeFull();
        this.binder = binder;

        description = new TextField("Description");
        url = new TextField("URL");
        urlCredentials = new TextField("URL credentials");
        headers = new TextArea("Headers");
        notify = new TextField("Notif.: list of emails");
        notifyObjectErrorPrefix = new TextField("Notif.: error subject prefix");
        notifyObjectPattern = new TextField("Notif.: subject pattern");
        notifyObjectValues = new TextField("Notif.: subject values");
        notifyInterval = new TextField("Notif.: interval");

        add(UIS.setWidthFull( //
                new HorizontalLayout(description)));
        add(UIS.setWidthFull( //
                new HorizontalLayout(url, urlCredentials)));
        add(UIS.setWidthFull( //
                headers));
        add(UIS.setWidthFull( //
                new HorizontalLayout(notify)));
        add(UIS.setWidthFull( //
                new HorizontalLayout(notifyObjectErrorPrefix, notifyObjectPattern, notifyObjectValues,
                        notifyInterval)));

        setElements();
    }

    private void setElements() {
        description.setWidth("100%");

        url.setWidth("50%");
        UIS.setTooltip(url, "The destination STOW-RS URL");

        urlCredentials.setWidth("50%");
        UIS.setTooltip(urlCredentials, "Credentials of the STOW-RS service (format is \"user:password\")");

        headers.setMinHeight("10em");
        headers.setWidth("100%");
        UIS.setTooltip(headers,
                "Headers for HTTP request. Example of format:\n<key>Authorization</key>\n<value>Bearer 1v1pwxT4Ww4DCFzyaMt0NP</value>");

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
        binder.forField(url)
                .withValidator(StringUtils::isNotBlank, "URL is mandatory")
                .bind(Destination::getUrl, Destination::setUrl);
        binder.forField(notifyInterval)
                .withConverter(new HStringToIntegerConverter())
                .bind(Destination::getNotifyInterval, Destination::setNotifyInterval);

        binder.bindInstanceFields(this);
    }
}
