package org.karnak.profilepipe.profiles;

import java.awt.Color;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.expression.ExprAction;
import org.karnak.expression.ExprConditionDestination;
import org.karnak.expression.ExpressionError;
import org.karnak.expression.ExpressionResult;
import org.karnak.profilepipe.action.AbstractAction;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.profilepipe.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionTags extends AbstractProfileItem {
    private static final Logger LOGGER = LoggerFactory.getLogger( ActionTags.class );

    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private ActionItem actionByDefault;

    public ActionTags(ProfileElement profileElement) throws Exception {
        super(profileElement);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = AbstractAction.convertAction(this.action);
        profileValidation();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        for (IncludedTag tag: tags) {
            tagsAction.put(tag.getTagValue(), actionByDefault);
        }
        if (excludedTags != null) {
            for (ExcludedTag tag : excludedTags) {
                exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
            }
        }
    }

    @Override
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        if (exceptedTagsAction.get(dcmElem.tag()) == null) {
            return tagsAction.get(dcmElem.tag());
        }
        return null;
    }

    @Override
    public void profileValidation() throws Exception{
        if (action == null && (tags == null || tags.size() <= 0)) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action and no tags defined");
        }

        if (action == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
        }

        if (tags == null || tags.size() <= 0) {
            throw new Exception("Cannot build the profile " + codeName + ": No tags defined");
        }

        final ExpressionError expressionError = ExpressionResult.isValid(condition, new ExprConditionDestination(1, VR.AE,
                DicomObject.newDicomObject(), DicomObject.newDicomObject()), Boolean.class);
        if (condition != null && !expressionError.isValid()) {
            throw new Exception(expressionError.getMsg());
        }
    }

    public static String color2Hexadecimal(Color c, boolean alpha) {
        int val = c == null ? 0 : alpha ? c.getRGB() : c.getRGB() & 0x00ffffff;
        return Integer.toHexString(val);
    }

    public static Color hexadecimal2Color(String hexColor) {
        int intValue = 0xff000000;

        try {
            if (hexColor != null && hexColor.length() > 6) {
                intValue = (int) (Long.parseLong(hexColor, 16) & 0xffffffff);
            } else {
                intValue |= Integer.parseInt(hexColor, 16);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot parse color {} into int", hexColor); //$NON-NLS-1$
        }
        return new Color(intValue, true);
    }
}
