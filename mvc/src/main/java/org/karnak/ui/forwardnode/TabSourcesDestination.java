package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class TabSourcesDestination extends Tabs {
    public String LABEL_SOURCES = "Sources";
    public String LABEL_DESTINATIONS = "Destinations";

    public TabSourcesDestination() {
        Tab sourcesTab = new Tab(LABEL_SOURCES);
        Tab destinationsTab = new Tab(LABEL_DESTINATIONS);
        add(sourcesTab, destinationsTab);
        setSelectedTab(destinationsTab);
    }
}
