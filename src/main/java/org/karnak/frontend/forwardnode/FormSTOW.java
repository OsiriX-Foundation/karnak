package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.frontend.component.converter.HStringToIntegerConverter;
import org.karnak.frontend.kheops.SwitchingAlbumsView;
import org.karnak.frontend.util.UIS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormSTOW extends VerticalLayout {

  private final LayoutDesidentification layoutDesidentification;
  private Binder<DestinationEntity> binder;
  private TextField description;
  private TextField url;
  private TextField urlCredentials;
  private TextArea headers;
  private TextField notify;
  private TextField notifyObjectErrorPrefix;
  private TextField notifyObjectPattern;
  private TextField notifyObjectValues;
  private TextField notifyInterval;
  private FilterBySOPClassesForm filterBySOPClassesForm;
  private SwitchingAlbumsView switchingAlbumsView;


  @Autowired
  public FormSTOW(final LayoutDesidentification layoutDesidentification) {
    this.layoutDesidentification = layoutDesidentification;
  }

  public void init(Binder<DestinationEntity> binder,
      ButtonSaveDeleteCancel buttonSaveDeleteCancel) {
    setSizeFull();
    this.binder = binder;
    this.layoutDesidentification.init(binder);

    this.description = new TextField("Description");
    this.url = new TextField("URL");
    this.urlCredentials = new TextField("URL credentials");
    this.headers = new TextArea("Headers");
    this.notify = new TextField("Notif.: list of emails");
    this.notifyObjectErrorPrefix = new TextField("Notif.: error subject prefix");
    this.notifyObjectPattern = new TextField("Notif.: subject pattern");
    this.notifyObjectValues = new TextField("Notif.: subject values");
    this.notifyInterval = new TextField("Notif.: interval");
    this.filterBySOPClassesForm = new FilterBySOPClassesForm(binder);
    this.switchingAlbumsView = new SwitchingAlbumsView();

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
    add(UIS.setWidthFull(layoutDesidentification));
    add(UIS.setWidthFull(filterBySOPClassesForm));
    add(UIS.setWidthFull(switchingAlbumsView));
    add(UIS.setWidthFull(buttonSaveDeleteCancel));

    setElements();
    setBinder();
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
        .bind(DestinationEntity::getUrl, DestinationEntity::setUrl);
    binder.forField(notifyInterval)
        .withConverter(new HStringToIntegerConverter())
        .bind(DestinationEntity::getNotifyInterval, DestinationEntity::setNotifyInterval);
    binder.forField(switchingAlbumsView)
        .bind(DestinationEntity::getKheopsAlbumEntities,
            DestinationEntity::setKheopsAlbumEntities);
    binder.bindInstanceFields(this);
  }
}
