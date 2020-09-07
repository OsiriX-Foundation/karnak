package org.karnak.ui.gateway;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationPersistence;
import org.karnak.data.gateway.ForwardNode;

import com.vaadin.flow.data.provider.ListDataProvider;

@SuppressWarnings("serial")
public class DestinationDataProvider extends ListDataProvider<Destination> {

    private DestinationPersistence destinationPersistence;
    {
        destinationPersistence = GatewayConfiguration.getInstance().getDestinationPersistence();
    }

    private final DataService dataService;
    private Set<Destination> backend;
    private boolean hasChanges;

    private ForwardNode forwardNode; // Current forward node

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

    public void setForwardNode(ForwardNode forwardNode) {
        this.forwardNode = forwardNode;
        Collection<Destination> destinations = this.dataService.getAllDestinations(forwardNode);

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

        Destination dataUpdated = dataService.updateDestination(forwardNode, data);
        if (newData) {
            refreshAll();
        } else {
            refreshItem(dataUpdated);
        }
        hasChanges = true;
        destinationPersistence.saveAndFlush(dataUpdated);
    }

    /**
     * Delete given data from the backing data service.
     * 
     * @param data the data to be deleted
     */
    public void delete(Destination data) {
        dataService.deleteDestination(forwardNode, data);
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
        if (forwardNode != null) {
            backend.addAll(forwardNode.getDestinations());
        }
        super.refreshAll();
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
