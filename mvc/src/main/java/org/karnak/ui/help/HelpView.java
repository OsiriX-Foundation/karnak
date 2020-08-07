package org.karnak.ui.help;


import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.karnak.ui.MainLayout;

@Route(value = "help", layout = MainLayout.class)
@PageTitle("KARNAK - Help")
@Tag("help-view")
@NpmPackage(value = "@polymer/paper-input", version = "3.0.2")
@JsModule("./js/help.js")
public class HelpView extends PolymerTemplate<TemplateModel> {
    public static final String VIEW_NAME = "Help";


    public HelpView() {
        setId("template");
        getElement().getStyle().set("overflow-y", "auto").set("padding-left", "15px");

    }
}
