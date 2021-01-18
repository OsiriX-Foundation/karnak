package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.data.repo.SOPClassUIDRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class SOPClassUIDService extends ListDataProvider<SOPClassUIDEntity> {

  // Repositories
  private final SOPClassUIDRepo sopClassUIDRepo;

  @Autowired
  public SOPClassUIDService(final SOPClassUIDRepo sopClassUIDRepo) {
    super(new ArrayList<>());
    this.sopClassUIDRepo = sopClassUIDRepo;
    getItems().addAll(getAllSOPClassUIDs());
  }

  public SOPClassUIDEntity get(Long dataId) {
    return sopClassUIDRepo.getSOPClassUIDById(dataId);
  }

  public SOPClassUIDEntity getByName(String name) {
    return sopClassUIDRepo.getSOPClassUIDByName(name);
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
    getItems().clear();
    getItems().addAll(getAllSOPClassUIDs());
    super.refreshAll();
  }
}
