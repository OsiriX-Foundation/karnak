package org.karnak.ui.help;


import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.ui.MainLayout;

@Route(value = "help", layout = MainLayout.class)
@PageTitle("KARNAK - Help")
@Tag("help-view")
public class HelpView extends VerticalLayout {
    public static final String VIEW_NAME = "Help";


    public HelpView() {
        setSizeFull();
        H1 heading = new H1( "Help" );

        Anchor generalDoc = new Anchor( "https://github.com/OsiriX-Foundation/karnak-docker/blob/master/README.md" , "General documentation");
        generalDoc.setTarget( "_blank" );

        Anchor installation = new Anchor( "https://github.com/OsiriX-Foundation/karnak-docker/blob/master/README.md" , "Installation and configuration with Docker");
        installation.setTarget( "_blank" );

        Anchor profile = new Anchor( "https://github.com/OsiriX-Foundation/karnak-docker/tree/master/profileExample" , "Build your own profile for de-identification or for tag morphing");
        profile.setTarget( "_blank" );
        VerticalLayout layout = new VerticalLayout();
        layout.add(heading, generalDoc,  installation, profile );
        this.add( layout);
    }
}
