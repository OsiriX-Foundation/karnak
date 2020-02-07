package org.karnak.ui.input;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.karnak.data.input.Destination;
import org.karnak.data.input.SourceNode;

import com.vaadin.flow.data.provider.ListDataProvider;

@SuppressWarnings("serial")
public class DestinationDataProvider extends ListDataProvider<Destination> {
    private final DataService dataService;
    private Set<Destination> backend;
    private boolean hasChanges;

    private SourceNode sourceNode; // Current source node

    /** Text filter that can be changed separately. */
    private String filterText = "";

    public DestinationDataProvider(DataService dataService) {
        this(dataService, new HashSet<>());
    }

    private DestinationDataProvider(DataService dataService, Set<Destination> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
    }

    public void setSourceNode(SourceNode sourceNode) {
        this.sourceNode = sourceNode;
        Collection<Destination> destinations = this.dataService.getAllDestinations(sourceNode);

        this.backend.clear();
        this.backend.addAll(destinations);

        hasChanges = false;
    }

    /**
     * Store given Destination to the backing data service.
     * 
     * @param data the updated or new data
     */
    public void save(Destination data) {
        boolean newData = data.isNewData();

        Destination dataUpdated = this.dataService.updateDestination(sourceNode, data);
        if (newData) {
            refreshAll();
        } else {
            refreshItem(dataUpdated);
        }
        hasChanges = true;
    }

    /**
     * Delete given data from the backing data service.
     * 
     * @param data the data to be deleted
     */
    public void delete(Destination data) {
        this.dataService.deleteDestination(sourceNode, data);
        refreshAll();
        hasChanges = true;
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
    public Object getId(Destination data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.hashCode();
    }

    private boolean matchesFilter(Destination data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        if (sourceNode != null) {
            backend.addAll(sourceNode.getDestinations());
        }
        super.refreshAll();
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
