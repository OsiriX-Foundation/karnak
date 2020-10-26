package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.expression.ExpressionError;
import org.karnak.expression.ExpressionResult;
import org.karnak.profilepipe.action.AbstractAction;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.expression.ExprAction;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.profilepipe.utils.TagActionMap;

import java.util.List;
import java.util.stream.Collectors;

public class Expression extends AbstractProfileItem {
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private ActionItem actionByDefault;

    public Expression(ProfileElement profileElement) throws Exception {
        super(profileElement);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = AbstractAction.convertAction("K");
        profileValidation();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        if(tags != null){
            for (IncludedTag tag: tags) {
                tagsAction.put(tag.getTagValue(), actionByDefault);
            }
            if (excludedTags != null) {
                for (ExcludedTag tag : excludedTags) {
                    exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
                }
            }
        }
    }

    @Override
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        if (exceptedTagsAction.get(dcmElem.tag()) == null && tagsAction.get(dcmElem.tag()) != null) {
            final String expr = arguments.get(0).getValue();
            final ExprAction exprAction = new ExprAction(dcmElem.tag(), dcmElem.vr(), dcm, dcmCopy);
            return (ActionItem) ExpressionResult.get(expr, exprAction, ActionItem.class);
        }
        return null;
    }

    public void profileValidation() throws Exception{
        if (!arguments.stream().anyMatch(argument -> argument.getKey().equals("expr"))) {
            List<String> args = arguments.stream()
                    .map(argument -> argument.getKey())
                    .collect(Collectors.toList());
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                    "Cannot build the expression: Missing argument, the class need [expr] as parameters. Parameters given " + args
            );
            throw missingParameters;
        }

        final String expr = arguments.get(0).getValue();
        final ExpressionError expressionError = ExpressionResult.isValid(expr, new ExprAction(1, VR.AE,
                DicomObject.newDicomObject(), DicomObject.newDicomObject()), ActionItem.class);

        if (!expressionError.isValid()) {
            IllegalArgumentException expressionNotValid = new IllegalArgumentException(
                    String.format("Expression is not valid: \n\r%s", expressionError.getMsg())
            );
            throw expressionNotValid;
        }
    }


}
