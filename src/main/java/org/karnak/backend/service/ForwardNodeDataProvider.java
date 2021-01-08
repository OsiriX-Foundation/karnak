package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.karnak.backend.data.entity.ForwardNodeEntity;

@SuppressWarnings("serial")
public class ForwardNodeDataProvider extends ListDataProvider<ForwardNodeEntity> {

    private final DataService dataService;

    private final Collection<ForwardNodeEntity> backend;

    /**
     * Text filter that can be changed separately.
     */
    private String filterText = "";

    public ForwardNodeDataProvider() {
        this(new DataServiceImpl(), new ArrayList<>());
    }

    protected ForwardNodeDataProvider(DataService dataService,
        Collection<ForwardNodeEntity> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
        backend.addAll(dataService.getAllForwardNodes());
    }

    public DataService getDataService() {
        return dataService;
    }

    /**
     * Retrieves the ForwardNode according to its ID.
     *
     * @param dataId the data ID.
     * @return the ForwardNodeEntity according to its ID; null if not found.
     */
    public ForwardNodeEntity get(Long dataId) {
        return dataService.getForwardNodeById(dataId);
    }

    /**
     * Store given ForwardNode to the backing data service.
     *
     * @param data the updated or new data
     */
    public void save(ForwardNodeEntity data) {
        boolean newData = data.isNewData();

        ForwardNodeEntity dataUpdated = this.dataService.updateForwardNode(data);
        if (newData) {
            refreshAll();
        } else {
            refreshItem(dataUpdated);
        }
    }

    /**
     * Delete given data from the backing data service.
     *
     * @param data the data to be deleted
     */
    public void delete(ForwardNodeEntity data) {
        this.dataService.deleteForwardNode(data.getId());
        refreshAll();
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
    public Long getId(ForwardNodeEntity data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.getId();
    }

    private boolean matchesFilter(ForwardNodeEntity data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        backend.addAll(dataService.getAllForwardNodes());
        super.refreshAll();
    }
}
