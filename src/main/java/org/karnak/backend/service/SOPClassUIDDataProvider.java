package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.karnak.backend.config.GatewayConfig;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.data.repo.SOPClassUIDRepo;

@SuppressWarnings("serial")
public class SOPClassUIDDataProvider extends ListDataProvider<SOPClassUIDEntity> {

  private final SOPClassUIDRepo sopClassUIDRepo;
  private final Collection<SOPClassUIDEntity> backend;

  DataService dataService;

  {
    sopClassUIDRepo = GatewayConfig.getInstance().getSopClassUIDPersistence();
  }

  public SOPClassUIDDataProvider() {
    this(new DataServiceImpl(), new ArrayList<>());
  }

  public SOPClassUIDDataProvider(DataService dataService, Collection<SOPClassUIDEntity> backend) {
    super(backend);
    this.dataService = dataService;
    this.backend = backend;
    backend.addAll(dataService.getAllSOPClassUIDs());
  }

  public DataService getDataService() {
    return dataService;
  }

  public SOPClassUIDEntity get(Long dataId) {
    return dataService.getSOPClassUIDById(dataId);
  }

  public SOPClassUIDEntity getByName(String dataName) {
    return dataService.getSOPClassUIDByName(dataName);
  }

  public List<SOPClassUIDEntity> getAllSOPClassUIDs() {
    List<SOPClassUIDEntity> list = new ArrayList<>();
    sopClassUIDRepo
        .findAll() //
        .forEach(list::add);
    return list;
  }

  public List<String> getAllSOPClassUIDsName() {
    return sopClassUIDRepo.findAll().stream()
        .map(SOPClassUIDEntity::getName)
        .collect(Collectors.toList());
  }

  @Override
  public void refreshAll() {
    backend.clear();
    backend.addAll(dataService.getAllSOPClassUIDs());
    super.refreshAll();
  }
}
