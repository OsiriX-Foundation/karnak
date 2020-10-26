package org.karnak.ui.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.karnak.data.gateway.ForwardNode;

import com.vaadin.flow.data.provider.ListDataProvider;

@SuppressWarnings("serial")
public class ForwardNodeDataProvider extends ListDataProvider<ForwardNode> {
    private final DataService dataService;

    private final Collection<ForwardNode> backend;

    /** Text filter that can be changed separately. */
    private String filterText = "";

    public ForwardNodeDataProvider() {
        this(new DataServiceImpl(), new ArrayList<>());
    }

    protected ForwardNodeDataProvider(DataService dataService, Collection<ForwardNode> backend) {
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
     * @return the ForwardNode according to its ID; null if not found.
     */
    public ForwardNode get(Long dataId) {
        return dataService.getForwardNodeById(dataId);
    }

    /**
     * Store given ForwardNode to the backing data service.
     * 
     * @param data the updated or new data
     */
    public void save(ForwardNode data) {
        boolean newData = data.isNewData();

        ForwardNode dataUpdated = this.dataService.updateForwardNode(data);
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
    public void delete(ForwardNode data) {
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
    public Long getId(ForwardNode data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.getId();
    }

    private boolean matchesFilter(ForwardNode data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        backend.addAll(dataService.getAllForwardNodes());
        super.refreshAll();
    }
}
