package org.karnak.backend.model.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.model.action.AbstractAction;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.expression.ExprConditionDestination;
import org.karnak.backend.model.expression.ExpressionError;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.profilepipe.TagActionMap;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateTags extends AbstractProfileItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateTags.class);

    private final TagActionMap tagsAction;
    private final TagActionMap exceptedTagsAction;
    private final ActionItem actionByDefault;

    public PrivateTags(ProfileElement profileElement) throws Exception{
        super(profileElement);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = AbstractAction.convertAction(this.action);
        profileValidation();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {

        if(tags != null && tags.size() > 0) {
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
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        final int tag = dcmElem.tag();
        if (TagUtils.isPrivateGroup(tag)) {
            if (tagsAction.isEmpty() == false && exceptedTagsAction.isEmpty()) {
                return tagsAction.get(tag);
            }

            if (tagsAction.isEmpty() && exceptedTagsAction.isEmpty() == false) {
                if(exceptedTagsAction.get(tag) != null){
                    return null;
                }
            }

            if (tagsAction.isEmpty() == false && exceptedTagsAction.isEmpty() == false) {
                if (exceptedTagsAction.get(dcmElem.tag()) == null) {
                    return tagsAction.get(dcmElem.tag());
                }
                return null;
            }
            return actionByDefault;
        }
        return null;
    }

    public void profileValidation() throws Exception{
        if (action == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
        }

        final ExpressionError expressionError = ExpressionResult.isValid(condition, new ExprConditionDestination(1, VR.AE,
                DicomObject.newDicomObject(), DicomObject.newDicomObject()), Boolean.class);
        if (condition != null && !expressionError.isValid()) {
            throw new Exception(expressionError.getMsg());
        }
    }
}
