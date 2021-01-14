package org.karnak.backend.data.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.DestinationType;

/**
 * @see https://stackoverflow.com/questions/27173960/jsr303-apply-all-validation-groups-defined-in-sequence
 */
public class DestinationGroupSequenceProvider implements
    DefaultGroupSequenceProvider<DestinationEntity> {

  public interface DestinationDicomGroup {

  }

  public interface DestinationStowGroup {

  }

  private static final List<Class<?>> DEFAULT_GROUPS = //
      Collections.singletonList(DestinationEntity.class);
  private static final List<Class<?>> TYPE_DICOM_GROUPS = //
      Arrays.asList(DestinationEntity.class, DestinationDicomGroup.class);
  private static final List<Class<?>> TYPE_STOW_GROUPS = //
      Arrays.asList(DestinationEntity.class, DestinationStowGroup.class);

  @Override
  public List<Class<?>> getValidationGroups(DestinationEntity destinationEntity) {
    if (destinationEntity != null) {
      DestinationType type = destinationEntity.getType();
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
