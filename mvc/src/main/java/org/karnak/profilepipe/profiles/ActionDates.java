package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.Action;
import org.karnak.profilepipe.option.datemanager.ShiftDate;
import org.karnak.profilepipe.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionDates extends AbstractProfileItem {
    private final Logger LOGGER = LoggerFactory.getLogger(ActionDates.class);
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private Action actionByDefault;

    public ActionDates(ProfileElement profileElement) throws Exception {
        super(profileElement);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = Action.REPLACE;
    }

    @Override
    public Action getAction(DicomObject dcmCopy, DicomElement dcmElem) {
        final String stringValue = dcmCopy.getString(dcmElem.tag()).orElse(null);
        String defaultDummyValue = ShiftDate.execute(dcmCopy, dcmElem, args);
        if (defaultDummyValue != null) {
            actionByDefault.setDummyValue(defaultDummyValue);
            return actionByDefault;
        }
        return null;
    }
}
