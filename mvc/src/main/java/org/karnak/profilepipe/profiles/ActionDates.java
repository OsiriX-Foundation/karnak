package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.Action;
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
    public Action getAction(DicomElement dcmElem) {
        String defaultDummyValue = switch (dcmElem.vr()) {
            case AS -> "045Y";
            case DA -> "19991111";
            case DT -> "19991111111111";
            case TM -> "111111";
            default -> null;
        };
        if (defaultDummyValue != null) {
            actionByDefault.setDummyValue(defaultDummyValue);
            return actionByDefault;
        }
        return null;
    }
}
