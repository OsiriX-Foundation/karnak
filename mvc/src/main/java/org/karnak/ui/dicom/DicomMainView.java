package org.karnak.ui.dicom;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.ui.MainLayout;
import org.karnak.ui.dicom.echo.DicomEchoView;
import org.karnak.ui.dicom.monitor.MonitorView;
import org.karnak.ui.dicom.mwl.DicomWorkListView;
import org.springframework.security.access.annotation.Secured;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "dicom", layout = MainLayout.class)
@PageTitle("KARNAK - DICOM Tools")
@Secured({"ADMIN"})
public class DicomMainView extends VerticalLayout {
  public static final String VIEW_NAME = "DICOM Tools";
  private static final long serialVersionUID = 1L;

  // UI COMPONENTS
  private DicomWebToolsBrand dicomWebToolsBrand;
  private Tabs menu;
  private Tab tabDicomEchoView;
  private Tab tabDicomWorkListView;
  private Tab tabMonitorView;
  private Map<Tab, Component> tabsToPages;
  private AbstractView pageDicomEchoView;
  private AbstractView pageDicomWorkListView;
  private AbstractView pageMonitorView;
  private Set<Component> pagesShown;

  // DATA
  private Map<Class<? extends Component>, Tab> navigationTargetToTab;

  public DicomMainView() {
    init();

    createDicomWebToolsBrand();
    createMenu();
  }

  private void init() {
    navigationTargetToTab = new HashMap<>();
  }

  private void createDicomWebToolsBrand() {
    dicomWebToolsBrand = new DicomWebToolsBrand();
  }

  private void createMenu() {
    tabDicomEchoView = new Tab("DICOM Echo");
    pageDicomEchoView = new DicomEchoView();

    tabDicomWorkListView = new Tab("DICOM Worklist");
    pageDicomWorkListView = new DicomWorkListView();

    tabMonitorView = new Tab("Monitor");
    pageMonitorView = new MonitorView();

    tabsToPages = new HashMap<>();
    tabsToPages.put(tabDicomEchoView, pageDicomEchoView);
    tabsToPages.put(tabDicomWorkListView, pageDicomWorkListView);
    tabsToPages.put(tabMonitorView, pageMonitorView);

    menu = new Tabs(tabDicomEchoView, tabDicomWorkListView, tabMonitorView);
    add(menu);
    pagesShown = Stream.of(pageDicomEchoView).collect(Collectors.toSet());

    add(pageDicomEchoView);
    menu.addSelectedChangeListener(event -> {
      pagesShown.forEach(page -> page.setVisible(false));
      pagesShown.clear();
      Component selectedPage = tabsToPages.get(menu.getSelectedTab());
      selectedPage.setVisible(true);
      pagesShown.add(selectedPage);
      add(selectedPage);
    });
  }
}
