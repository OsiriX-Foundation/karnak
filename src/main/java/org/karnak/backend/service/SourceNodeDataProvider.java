package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.karnak.backend.config.GatewayConfig;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.DicomSourceNodeRepo;

@SuppressWarnings("serial")
public class SourceNodeDataProvider extends ListDataProvider<DicomSourceNodeEntity> {

    private final DicomSourceNodeRepo dicomSourceNodeRepo;
    private final Set<DicomSourceNodeEntity> backend;

    private final DataService dataService;
    private ForwardNodeEntity forwardNodeEntity; // Current forward node
    private boolean hasChanges;

    {
        dicomSourceNodeRepo = GatewayConfig.getInstance().getDicomSourceNodePersistence();
    }

    /**
     * Text filter that can be changed separately.
     */
    private String filterText = "";

    public SourceNodeDataProvider(DataService dataService) {
        this(dataService, new HashSet<>());
    }

    private SourceNodeDataProvider(DataService dataService, Set<DicomSourceNodeEntity> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
    }

    public void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
        this.forwardNodeEntity = forwardNodeEntity;
        Collection<DicomSourceNodeEntity> sourceNodes = this.dataService.getAllSourceNodes(
            forwardNodeEntity);

        this.backend.clear();
        this.backend.addAll(sourceNodes);

        hasChanges = false;
    }

    /**
     * Store given DicomSourceNodeEntity to the backing data service.
     *
     * @param data the updated or new data
     */
    public void save(DicomSourceNodeEntity data) {
        boolean newData = data.isNewData();

        DicomSourceNodeEntity dataUpdated = this.dataService
            .updateSourceNode(forwardNodeEntity, data);
        if (newData) {
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
        this.dataService.deleteSourceNode(forwardNodeEntity, data);
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

    @Override
    public Object getId(DicomSourceNodeEntity data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.hashCode();
    }

    private boolean matchesFilter(DicomSourceNodeEntity data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        if (forwardNodeEntity != null) {
            backend.addAll(forwardNodeEntity.getSourceNodes());
        }
        super.refreshAll();
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
