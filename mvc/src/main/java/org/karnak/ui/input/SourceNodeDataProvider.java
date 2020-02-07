package org.karnak.ui.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.karnak.data.input.SourceNode;

import com.vaadin.flow.data.provider.ListDataProvider;

@SuppressWarnings("serial")
public class SourceNodeDataProvider extends ListDataProvider<SourceNode> {
    private final DataService dataService;

    private final Collection<SourceNode> backend;

    /** Text filter that can be changed separately. */
    private String filterText = "";

    public SourceNodeDataProvider() {
        this(new DataServiceImpl(), new ArrayList<>());
    }

    protected SourceNodeDataProvider(DataService dataService, Collection<SourceNode> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
        backend.addAll(dataService.getAllSourceNodes());
    }

    public DataService getDataService() {
        return dataService;
    }

    /**
     * Retrieves the SourceNode according to its ID.
     * 
     * @param dataId the data ID.
     * @return the SourceNode according to its ID; null if not found.
     */
    public SourceNode get(Long dataId) {
        return dataService.getSourceNodeById(dataId);
    }

    /**
     * Store given SourceNode to the backing data service.
     * 
     * @param data the updated or new data
     */
    public void save(SourceNode data) {
        boolean newData = data.isNewData();

        SourceNode dataUpdated = this.dataService.updateSourceNode(data);
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
    public void delete(SourceNode data) {
        this.dataService.deleteSourceNode(data.getId());
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
    public Long getId(SourceNode data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.getId();
    }

    private boolean matchesFilter(SourceNode data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        backend.addAll(dataService.getAllSourceNodes());
        super.refreshAll();
    }
}
