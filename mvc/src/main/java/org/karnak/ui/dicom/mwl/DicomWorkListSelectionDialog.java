package org.karnak.ui.dicom.mwl;

import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.karnak.dicom.model.*;
import org.karnak.dicom.service.DicomEchoService;
import org.karnak.dicom.service.DicomNodeManager;
import org.karnak.ui.component.AbstractDialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

public class DicomWorkListSelectionDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;

    // UI COMPONENTS
    private Dialog dialog;

    private Div titleBar;
    private FormLayout formLayout;
    private Select<ConfigNode> worklistNodeSelector;
    private HorizontalLayout buttonBar;
    private Button cancelBtn;
    private Button selectBtn;

    // DATA
    private DicomNodeList workListNodes;
    private ListDataProvider<ConfigNode> dataProviderForWorkListNodes;


    public DicomWorkListSelectionDialog() {
        init();
        selectDicomNodeList(DicomNodeManager.getAllWorkListNodesDefinedLocally());
        createMainLayout();
        dataProviderForWorkListNodes.refreshAll();
        selectFirstItemInWorkListNodes();
        dialog.add(mainLayout);
    }

    @Override
    protected void createMainLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        buildTitleBar();
        buildFormLayout();
        buildButtonBar();

        mainLayout.add(titleBar, formLayout, buttonBar);
    }

    public void open() {
        dialog.open();
    }

    public void loadWorkListNodes(DicomNodeList workListNodes) {
        this.workListNodes.clear();

        if (workListNodes != null && !workListNodes.isEmpty()) {
            this.workListNodes.addAll(workListNodes);
            this.workListNodes.sort((wl1, wl2) -> wl1.getName().compareTo(wl2.getName()));
        }

    }
    
    // LISTENERS
    public Registration addWorkListSelectionListener(ComponentEventListener<WorkListSelectionEvent> listener) {
        return addListener(WorkListSelectionEvent.class, listener);
    }


    private void init() {
        dialog = this.getContent();

        workListNodes = new DicomNodeList("Worklists");
        buildDataProvider();
    }

    @SuppressWarnings("serial")
    private void buildDataProvider() {
        dataProviderForWorkListNodes = new ListDataProvider<>(workListNodes);

        dataProviderForWorkListNodes.addDataProviderListener(new DataProviderListener<ConfigNode>() {

            @Override
            public void onDataChange(DataChangeEvent<ConfigNode> event) {
                selectFirstItemInWorkListNodes();
            }
        });
    }

    private void buildTitleBar() {
        titleBar = new Div();
        titleBar.setText("Worklist Selection");
        titleBar.getStyle().set("font-weight", "500");
    }
    
    private void buildFormLayout() {
        formLayout = new FormLayout();
        formLayout.setSizeFull();

        buildWorklistNodeSelector();
        
        formLayout.add(worklistNodeSelector);
    }

    private void buildWorklistNodeSelector() {
        worklistNodeSelector = new Select<>();
        worklistNodeSelector.setLabel("Worklist Node");
        worklistNodeSelector.setDataProvider(dataProviderForWorkListNodes);
        worklistNodeSelector.setItemLabelGenerator(item -> item.getName() + " [" + item.getAet() + " | " + item.getHostname() + " | " + item.getPort() + "]");
        worklistNodeSelector.setRenderer(buildDicomNodeRenderer());
    }

    private ComponentRenderer<Div, ConfigNode> buildDicomNodeRenderer() {
        return new ComponentRenderer<Div, ConfigNode>(item -> {
            Div div = new Div();
            div.getStyle().set("line-height", "92%");

            Span spanDescription = new Span(item.getName());
            spanDescription.getStyle().set("font-weight", "500");

            HtmlComponent htmlLineBreak = new HtmlComponent("BR");

            Span spanOtherAttributes = new Span(item.getAet() + " | " + item.getHostname() + " | " + item.getPort());
            spanOtherAttributes.getStyle().set("font-size", "75%");

            div.add(spanDescription, htmlLineBreak, spanOtherAttributes);

            return div;
        });
    }

    public void selectDicomNodeList(DicomNodeList originSelected) {
        try {
            this.removeMessage();
            this.loadWorkListNodes(originSelected);
        } catch (Exception e) {
            Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, "Cannot read the set of worklists");
            this.displayMessage(message);
        }
    }

    private void selectFirstItemInWorkListNodes() {
        if (workListNodes != null && !workListNodes.isEmpty()) {
            worklistNodeSelector.setValue(workListNodes.get(0));
        }
    }
    
    private void buildButtonBar() {
            buttonBar = new HorizontalLayout();
            buttonBar.setWidthFull();
            buttonBar.setPadding(false);
            buttonBar.setSpacing(true);
            
            buildCancelBtn();
            buildSelectBtn();
            
            buttonBar.add(cancelBtn, selectBtn);
    }
    
    @SuppressWarnings("serial")
    private void buildCancelBtn() {
        cancelBtn = new Button("Cancel");
        
        cancelBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                dialog.close();
            }
        });
    }

    @SuppressWarnings("serial")
    private void buildSelectBtn() {
        selectBtn = new Button("Select");
        selectBtn.addClassName("stroked-button");
        
        selectBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                fireWorkListSelectionEvent();
                dialog.close();
            }
        });
    }

    private void fireWorkListSelectionEvent() {
        ConfigNode selectedWorkList = worklistNodeSelector.getValue();
        fireEvent(new WorkListSelectionEvent(this, false, selectedWorkList));
    }

    
    public class WorkListSelectionEvent extends ComponentEvent<DicomWorkListSelectionDialog> {

        private static final long serialVersionUID = 1L;
        
        private ConfigNode selectedWorkList;

        public WorkListSelectionEvent(DicomWorkListSelectionDialog source, boolean fromClient, ConfigNode selectedWorkList) {
            super(source, fromClient);
            
            this.selectedWorkList = selectedWorkList;
        }

        public ConfigNode getSelectedWorkList() {
            return selectedWorkList;
        }
    }
    
}