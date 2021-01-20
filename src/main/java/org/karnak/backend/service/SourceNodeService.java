package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.DicomSourceNodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class SourceNodeService extends ListDataProvider<DicomSourceNodeEntity> {

    // Repositories
    private final DicomSourceNodeRepo dicomSourceNodeRepo;

    // Services
    private final ForwardNodeService forwardNodeService;

    private ForwardNodeEntity forwardNodeEntity; // Current forward node
    private boolean hasChanges;

    /**
     * Text filter that can be changed separately.
     */
    private String filterText = "";

    @Autowired
    public SourceNodeService(final DicomSourceNodeRepo dicomSourceNodeRepo,
        final ForwardNodeService forwardNodeService) {
        super(new HashSet<>());
        this.dicomSourceNodeRepo = dicomSourceNodeRepo;
        this.forwardNodeService = forwardNodeService;
    }

    @Override
    public Object getId(DicomSourceNodeEntity data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");
        return data.hashCode();
    }

    @Override
    public void refreshAll() {
        getItems().clear();
        if (forwardNodeEntity != null) {
            getItems().addAll(forwardNodeEntity.getSourceNodes());
        }
        super.refreshAll();
    }


    public void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
        this.forwardNodeEntity = forwardNodeEntity;
        Collection<DicomSourceNodeEntity> sourceNodes = this.forwardNodeService.getAllSourceNodes(
            forwardNodeEntity);

        this.getItems().clear();
        this.getItems().addAll(sourceNodes);

        hasChanges = false;
    }

    /**
     * Store given DicomSourceNodeEntity.
     *
     * @param dicomSourceNodeEntity the updated or new dicomSourceNodeEntity
     */
    public void save(DicomSourceNodeEntity dicomSourceNodeEntity) {
        DicomSourceNodeEntity dataUpdated = this.forwardNodeService
            .updateSourceNode(forwardNodeEntity, dicomSourceNodeEntity);
        if (dicomSourceNodeEntity.getId() == null) {
            refreshAll();
        } else {
            refreshItem(dataUpdated);
        }
        hasChanges = true;
        dicomSourceNodeRepo.saveAndFlush(dataUpdated);
    }

    /**
     * Delete given data from the backing data service.
     *
     * @param data the data to be deleted
     */
    public void delete(DicomSourceNodeEntity data) {
        this.forwardNodeService.deleteSourceNode(forwardNodeEntity, data);
        refreshAll();
        hasChanges = true;
        dicomSourceNodeRepo.deleteById(data.getId());
        dicomSourceNodeRepo.saveAndFlush(data);
    }

    /**
     * Sets the filter to use for this data provider and refreshes data.
     * <p>
     * Filter is compared for allowed properties.
     * 
     * @param filterTextInput the text to filter by, never null.
     */
    public void setFilter(String filterTextInput) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");

        final String filterText = filterTextInput.trim();

        if (Objects.equals(this.filterText, filterText)) {
            return;
        }
        this.filterText = filterText;

        setFilter(data -> matchesFilter(data, filterText));
    }

    private boolean matchesFilter(DicomSourceNodeEntity data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
