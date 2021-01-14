package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.karnak.backend.config.GatewayConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.DestinationRepo;

@SuppressWarnings("serial")
public class DestinationDataProvider extends ListDataProvider<DestinationEntity> {

    private final DestinationRepo destinationRepo;
    private final Set<DestinationEntity> backend;

    private final DataService dataService;
    private ForwardNodeEntity forwardNodeEntity; // Current forward node
    private boolean hasChanges;

    {
        destinationRepo = GatewayConfig.getInstance().getDestinationPersistence();
    }

    /**
     * Text filter that can be changed separately.
     */
    private String filterText = "";

    public DestinationDataProvider(DataService dataService) {
        this(dataService, new HashSet<>());
    }

    private DestinationDataProvider(DataService dataService, Set<DestinationEntity> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
    }

    public void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
        this.forwardNodeEntity = forwardNodeEntity;
        Collection<DestinationEntity> destinationEntities = this.dataService.getAllDestinations(
            forwardNodeEntity);

        this.backend.clear();
        this.backend.addAll(destinationEntities);

        hasChanges = false;
    }

    /**
     * Store given Destination to the backing data service.
     *
     * @param data the updated or new data
     */
    public void save(DestinationEntity data) {
        KheopsAlbumsDataProvider kheopsAlbumsDataProvider = new KheopsAlbumsDataProvider();
        boolean newData = data.isNewData();

        DestinationEntity dataUpdated = dataService.updateDestination(forwardNodeEntity, data);
        if (newData) {
            refreshAll();
        } else {
            dataUpdated = removeValuesOnDisabledDesidentification(data);
            refreshItem(dataUpdated);
        }
        hasChanges = true;
        destinationRepo.saveAndFlush(dataUpdated);
        kheopsAlbumsDataProvider.updateSwitchingAlbumsFromDestination(data);
    }

    private DestinationEntity removeValuesOnDisabledDesidentification(DestinationEntity data) {
        if (data.getDesidentification() == false) {
            data.setProjectEntity(null);
        }
        return data;
    }

    /**
     * Delete given data from the backing data service.
     *
     * @param data the data to be deleted
     */
    public void delete(DestinationEntity data) {
        dataService.deleteDestination(forwardNodeEntity, data);
        refreshAll();
        destinationRepo.deleteById(data.getId());
        // TODO: Le jours où la suprresion d'une destination se passera correctement SUPPRIMER cette ligne
        data.setKheopsAlbumEntities(null);
        data.setProjectEntity(null);
        destinationRepo.saveAndFlush(data);
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
    public Object getId(DestinationEntity data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.hashCode();
    }

    private boolean matchesFilter(DestinationEntity data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        if (forwardNodeEntity != null) {
            backend.addAll(forwardNodeEntity.getDestinationEntities());
        }
        super.refreshAll();
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
