package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.action.Replace;
import org.karnak.profilepipe.option.datemanager.ShiftDate;
import org.karnak.profilepipe.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionDates extends AbstractProfileItem {
    private final Logger LOGGER = LoggerFactory.getLogger(ActionDates.class);
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private ActionItem actionByDefault;

    public ActionDates(ProfileElement profileElement) throws Exception {
        super(profileElement);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = new Replace("D");
    }

    @Override
    public ActionItem getAction(DicomObject dcmCopy, DicomElement dcmElem) {
        final String stringValue = dcmCopy.getString(dcmElem.tag()).orElse(null);

        final String defaultDummyValue  = switch (option) {
            case "shift" -> ShiftDate.shift(dcmCopy, dcmElem, null);
            default -> null;
        };

        if (defaultDummyValue != null) {
            actionByDefault.setDummyValue(defaultDummyValue);
            return actionByDefault;
        }
        return null;
    }
}
