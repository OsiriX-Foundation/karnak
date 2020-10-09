package org.karnak.ui.research;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.data.gateway.Research;
import org.karnak.ui.MainLayout;

@Route(value = "research", layout = MainLayout.class)
@PageTitle("KARNAK - Research")
public class ResearchView extends VerticalLayout {
    public static final String VIEW_NAME = "Research";
    private NewResearchForm newResearchForm;
    private Binder<Research> newResearchBinder;

    public ResearchView() {
        newResearchForm = new NewResearchForm();
        newResearchBinder = newResearchForm.getBinder();
        add(newResearchForm);
        setEventButtonAdd();
    }

    private void setEventButtonAdd() {
        newResearchForm.getButtonAdd().addClickListener(event -> {
            Research newResearch = new Research();
            if (newResearchBinder.writeBeanIfValid(newResearch)) {
                /*
                dataProviderSwitchingAlbums.getItems().add(newKheopsAlbums);
                dataProviderSwitchingAlbums.refreshAll();
                */
                newResearchForm.clear();
            }
        });
    }
}
