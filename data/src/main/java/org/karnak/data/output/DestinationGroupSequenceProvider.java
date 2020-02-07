package org.karnak.data.output;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

/**
 * @see https://stackoverflow.com/questions/27173960/jsr303-apply-all-validation-groups-defined-in-sequence
 */
public class DestinationGroupSequenceProvider implements DefaultGroupSequenceProvider<Destination> {
    public interface DestinationDicomGroup {
    }

    public interface DestinationStowGroup {
    }

    private static final List<Class<?>> DEFAULT_GROUPS = //
            Collections.singletonList(Destination.class);
    private static final List<Class<?>> TYPE_DICOM_GROUPS = //
            Arrays.asList(Destination.class, DestinationDicomGroup.class);
    private static final List<Class<?>> TYPE_STOW_GROUPS = //
            Arrays.asList(Destination.class, DestinationStowGroup.class);

    @Override
    public List<Class<?>> getValidationGroups(Destination destination) {
        if (destination != null) {
            DestinationType type = destination.getType();
            if (type != null) {
                switch (type) {
                case dicom:
                    return TYPE_DICOM_GROUPS;
                case stow:
                    return TYPE_STOW_GROUPS;
                }
            }
        }

        return DEFAULT_GROUPS;
    }
}
