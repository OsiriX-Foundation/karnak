package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.action.Replace;
import org.karnak.profilepipe.option.datemanager.ShiftDate;
import org.karnak.profilepipe.option.datemanager.ShiftRangeDate;
import org.karnak.profilepipe.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionDates extends AbstractProfileItem {
    private final Logger LOGGER = LoggerFactory.getLogger(ActionDates.class);
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private ActionItem actionByDefault;
    private ShiftRangeDate shiftRangeDate;

    public ActionDates(ProfileElement profileElement) throws Exception {
        super(profileElement);
        shiftRangeDate = new ShiftRangeDate();
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = new Replace("D");
        profileValidation();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        if (tags != null && tags.size() > 0) {
            for (IncludedTag tag : tags) {
                tagsAction.put(tag.getTagValue(), actionByDefault);
            }
        }
        if (excludedTags != null && excludedTags.size() > 0) {
            for (ExcludedTag tag : excludedTags) {
                exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
            }
        }
    }

    @Override
    public void profileValidation() throws Exception {}

    @Override
    public ActionItem getAction(DicomObject dcmCopy, DicomElement dcmElem, String PatientID) {
        final int tag = dcmElem.tag();
        final VR vr = dcmElem.vr();

        if (vr == VR.AS || vr == VR.DA || vr == VR.DT || vr == VR.TM) {
            if (exceptedTagsAction.get(tag) != null) {
                return null;
            }

            if (tagsAction.isEmpty() == false && tagsAction.get(tag) == null) {
                return null;
            }
            String dummyValue = applyOption(dcmCopy, dcmElem, PatientID);
            if (dummyValue != null) {
                actionByDefault.setDummyValue(dummyValue);
                return actionByDefault;
            }
        }
        return null;
    }

    private String applyOption(DicomObject dcmCopy, DicomElement dcmElem, String PatientID) {
        return switch (option) {
            case "shift" -> ShiftDate.shift(dcmCopy, dcmElem, arguments);
            case "shift_range" -> shiftRangeDate.shift(dcmCopy, dcmElem, arguments, PatientID);
            default -> null;
        };
    }
}