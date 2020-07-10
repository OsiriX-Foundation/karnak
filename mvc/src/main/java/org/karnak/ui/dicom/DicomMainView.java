package org.karnak.ui.dicom;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.*;
import org.karnak.ui.MainLayout;
import org.karnak.ui.authentication.AccessControlFactory;
import org.karnak.ui.dicom.echo.DicomEchoView;
import org.karnak.ui.dicom.mwl.DicomWorkListView;
import org.karnak.ui.dicom.monitor.MonitorView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;

@Route(value = "dicom", layout = MainLayout.class)
@PageTitle("DICOM Tools")
public class DicomMainView extends AppLayout implements BeforeEnterObserver {
  public static final String VIEW_NAME = "DICOM Tools";
  private static final long serialVersionUID = 1L;

  // UI COMPONENTS
  private DicomWebToolsBrand dicomWebToolsBrand;
  private Tabs menu;
  private Tab dicomEchoTab;
  private Tab dicomWorklistTab;
  private Tab monitorTab;

  // DATA
  private Map<Class<? extends Component>, Tab> navigationTargetToTab;

  public DicomMainView() {
    init();

    createDicomWebToolsBrand();
    createMenu();

    Button logoutButton = new Button("Back", VaadinIcon.SIGN_OUT.create());
    logoutButton.addClickListener(
        e -> logoutButton.getUI().ifPresent(ui -> ui.navigate("gateway")));
    addToNavbar(dicomWebToolsBrand, menu, logoutButton);
  }

  private void init() {
    navigationTargetToTab = new HashMap<>();
  }

  private void createDicomWebToolsBrand() {
    dicomWebToolsBrand = new DicomWebToolsBrand();
  }

  private void createMenu() {
    menu = new Tabs();
    menu.setWidthFull();
    menu.setOrientation(Orientation.HORIZONTAL);

    createDicomEchoTab();
    createDicomWorklistTab();
    createMonitorTab();

    menu.add(dicomEchoTab, dicomWorklistTab, monitorTab);
  }

  private void createDicomEchoTab() {
    RouterLink dicomEchoLink = new RouterLink("DICOM Echo", DicomEchoView.class);
    dicomEchoTab = new Tab(dicomEchoLink);
    navigationTargetToTab.put(DicomEchoView.class, dicomEchoTab);
  }

  private void createDicomWorklistTab() {
    RouterLink dicomWorklistLink = new RouterLink("DICOM Worklist", DicomWorkListView.class);
    dicomWorklistTab = new Tab(dicomWorklistLink);
    navigationTargetToTab.put(DicomWorkListView.class, dicomWorklistTab);
  }

  private void createMonitorTab() {
    RouterLink monitorLink = new RouterLink("Monitor", MonitorView.class);
    monitorTab = new Tab(monitorLink);
    navigationTargetToTab.put(MonitorView.class, monitorTab);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (event.getNavigationTarget() == DicomMainView.class) {
      // Force navigation to the default view (DicomEchoView)
      event.rerouteTo(DicomEchoView.class);
    } else {
      menu.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
    }
  }
}
