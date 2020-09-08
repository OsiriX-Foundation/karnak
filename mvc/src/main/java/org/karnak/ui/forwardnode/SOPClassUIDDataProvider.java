package org.karnak.ui.forwardnode;

import com.vaadin.flow.data.provider.ListDataProvider;
import org.karnak.data.gateway.SOPClassUID;
import org.karnak.data.gateway.SOPClassUIDPersistence;
import org.karnak.ui.gateway.DataService;
import org.karnak.ui.gateway.DataServiceImpl;
import org.karnak.ui.gateway.GatewayConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class SOPClassUIDDataProvider extends ListDataProvider<SOPClassUID> {
    private SOPClassUIDPersistence sopClassUIDPersistence;
    {
        sopClassUIDPersistence = GatewayConfiguration.getInstance().getSopClassUIDPersistence();
    }

    DataService dataService;
    private final Collection<SOPClassUID> backend;

    public SOPClassUIDDataProvider() {
        this(new DataServiceImpl(), new ArrayList<>());
    }

    public SOPClassUIDDataProvider(DataService dataService, Collection<SOPClassUID> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
        backend.addAll(dataService.getAllSOPClassUIDs());
    }

    public DataService getDataService() {
        return dataService;
    }


    public SOPClassUID get(Long dataId) {
        return dataService.getSOPClassUIDById(dataId);
    }

    public SOPClassUID getByName(String dataName) {
        return dataService.getSOPClassUIDByName(dataName);
    }


    public List<SOPClassUID> getAllSOPClassUIDs() {
        List<SOPClassUID> list = new ArrayList<>();
        sopClassUIDPersistence.findAll() //
                .forEach(list::add);
        return list;
    }

    public List<String> getAllSOPClassUIDsName() {
        return sopClassUIDPersistence.findAll().stream().map(SOPClassUID::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void refreshAll() {
        backend.clear();
        backend.addAll(dataService.getAllSOPClassUIDs());
        super.refreshAll();
    }
}
