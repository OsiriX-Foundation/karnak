package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.data.AppConfig;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.profilepipe.utils.MetadataDICOMObject;
import org.karnak.standard.Attribute;
import org.karnak.standard.Module;
import org.karnak.standard.StandardDICOM;
import org.karnak.standard.exceptions.StandardDICOMException;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleActions extends AbstractAction {
    final StandardDICOM standardDICOM;
    final ActionItem defaultDummyValue;
    final ActionItem actionUID;
    final ActionItem actionReplaceNull;
    final ActionItem actionRemove;
    final ActionItem actionKeep;

    public MultipleActions(String symbol) {
        super(symbol);
        standardDICOM = AppConfig.getInstance().getStandardDICOM();
        defaultDummyValue = new DefaultDummy(symbol);
        actionUID = new UID("U");
        actionReplaceNull = new ReplaceNull("Z");
        actionRemove = new Remove("X");
        actionKeep = new Keep("K");
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        final String sopUID = MetadataDICOMObject.getValue(dcm, Tag.SOPClassUID);
        final String tagPath = MetadataDICOMObject.getTagPath(dcm, tag);
        try {
            List<Attribute> attributes = standardDICOM.getAttributesBySOP(sopUID, tagPath);
            if (attributes.size() == 1) {
                String currentType = attributes.get(0).getType();
                ActionItem actionItem = chooseAction(sopUID, currentType);
                actionItem.execute(dcm, tag, iterator, hmac);
            } else if (attributes.size() > 1) {
                ActionItem action = multipleAttributes(sopUID, attributes);
                action.execute(dcm, tag, iterator, hmac);
            }
        } catch (StandardDICOMException standardDICOMException) {
            LOGGER.error("Could not execute an action with a unknown SOP", standardDICOMException);
        }
        // TODO: throw exception
        // Throw exception Tag NOT FOUND IN THE DICOM Standard ?
    }

    private ActionItem multipleAttributes(String sopUID, List<Attribute> attributes) {
        List<Module> modules = attributes.stream()
                .map(attribute -> standardDICOM.getModuleByModuleID(sopUID, attribute.getModuleId()).orElse(null))
                .collect(Collectors.toList());
        List<Module> modulesMandatory = modules.stream()
                .filter(module -> Module.moduleIsMandatory(module))
                .collect(Collectors.toList());

        if (modulesMandatory.size() == 0) {
            String currentType = Attribute.getStrictedType(attributes);
            return chooseAction(sopUID, currentType);
        }

        if (modulesMandatory.size() == 1) {
            String currentType = attributes.stream()
                    .filter(attr -> attr.getModuleId().equals(modulesMandatory.get(0).getId()))
                    .findFirst().orElse(null).getType();
            return chooseAction(sopUID, currentType);
        }

        // TODO: use mandatory attributes !!
        String currentType = Attribute.getStrictedType(attributes);
        return chooseAction(sopUID, currentType);
    }

    private ActionItem chooseAction(String sopUID, String currentType) {
        return switch (symbol) {
            case "Z/D" -> DummyOrReplaceNull(currentType);
            case "X/D" -> DummyOrRemove(currentType);
            case "X/Z/D" -> DummyOrReplaceNullOrRemove(currentType);
            case "X/Z" -> ReplaceNullOrRemove(currentType);
            case "X/Z/U", "X/Z/U*" -> UIDorReplaceNullOrRemove(currentType);
            default -> new DefaultDummy(symbol);
        };
    }

    private ActionItem DummyOrReplaceNull(String currentType) {
        if (currentType.equals("1") || currentType.equals("1C")) {
            return defaultDummyValue;
        }
        return actionReplaceNull;
    }

    private ActionItem DummyOrRemove(String currentType) {
        if (currentType.equals("3")) {
            return actionRemove;
        }
        return defaultDummyValue;
    }

    private ActionItem DummyOrReplaceNullOrRemove(String currentType) {
        if (currentType.equals("1") || currentType.equals("1C")) {
            return defaultDummyValue;
        }
        if (currentType.equals("2") || currentType.equals("2C")) {
            return actionReplaceNull;
        }
        return actionRemove;
    }

    private ActionItem ReplaceNullOrRemove(String currentType) {
        if (currentType.equals("1") || currentType.equals("1C")) {
            // TODO: throw exception ?
            // throw new Exception(For the current SOP, the tag must type 1. Impossible to execute and respect the standard);
        }
        if (currentType.equals("2") || currentType.equals("2C")) {
            return actionReplaceNull;
        }
        return actionRemove;
    }

    private ActionItem UIDorReplaceNullOrRemove(String currentType) {
        if (currentType.equals("1") || currentType.equals("1C")) {
            return actionUID;
        }
        if (currentType.equals("2") || currentType.equals("2C")) {
            return actionReplaceNull;
        }
        return actionRemove;
    }
}
