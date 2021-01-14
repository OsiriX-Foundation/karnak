package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.frontend.util.UIS;

public class FormSourceNode extends VerticalLayout {

  private final Binder<DicomSourceNodeEntity> binder;
  private final TextField aeTitle;
    private final TextField description;
    private final TextField hostname;
    private final Checkbox checkHostname;
    private final ButtonSaveDeleteCancel buttonSaveDeleteCancel;

  public FormSourceNode(Binder<DicomSourceNodeEntity> binder,
      ButtonSaveDeleteCancel buttonSaveDeleteCancel) {
    setSizeFull();
    this.binder = binder;
    this.buttonSaveDeleteCancel = buttonSaveDeleteCancel;
    aeTitle = new TextField("AETitle");
    description = new TextField("Description");
    hostname = new TextField("Hostname");
    checkHostname = new Checkbox("Check the hostname");

    setElements();
    setBinder();

        add(UIS.setWidthFull(new HorizontalLayout(aeTitle, description)),
                UIS.setWidthFull(new HorizontalLayout(hostname)),
                UIS.setWidthFull(checkHostname),
                UIS.setWidthFull(buttonSaveDeleteCancel));
    }

    private void setElements() {
        aeTitle.setWidth("30%");
        description.setWidth("70%");
        hostname.setWidth("70%");
        UIS.setTooltip(checkHostname,
                "if checked, check the hostname during the DICOM association and if not match the connection is abort");
    }

    private void setBinder() {
      binder.forField(aeTitle).withValidator(StringUtils::isNotBlank, "AETitle is mandatory")
          .bind(DicomSourceNodeEntity::getAeTitle, DicomSourceNodeEntity::setAeTitle);
      binder.bindInstanceFields(this);
    }
}
