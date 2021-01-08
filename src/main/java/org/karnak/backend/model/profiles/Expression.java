package org.karnak.backend.model.profiles;

import java.util.List;
import java.util.stream.Collectors;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.backend.data.entity.ExcludedTagEntity;
import org.karnak.backend.data.entity.IncludedTagEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.model.action.AbstractAction;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.expression.ExprAction;
import org.karnak.backend.model.expression.ExpressionError;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.profilepipe.TagActionMap;

public class Expression extends AbstractProfileItem {

    private final TagActionMap tagsAction;
    private final TagActionMap exceptedTagsAction;
    private final ActionItem actionByDefault;

    public Expression(ProfileElementEntity profileElementEntity) throws Exception {
        super(profileElementEntity);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = AbstractAction.convertAction("K");
        profileValidation();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        if (tagEntities != null) {
            for (IncludedTagEntity tag : tagEntities) {
                tagsAction.put(tag.getTagValue(), actionByDefault);
            }
            if (excludedTagEntities != null) {
                for (ExcludedTagEntity tag : excludedTagEntities) {
                    exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
                }
            }
        }
    }

    @Override
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        if (exceptedTagsAction.get(dcmElem.tag()) == null && tagsAction.get(dcmElem.tag()) != null) {
            final String expr = argumentEntities.get(0).getValue();
            final ExprAction exprAction = new ExprAction(dcmElem.tag(), dcmElem.vr(), dcm, dcmCopy);
            return (ActionItem) ExpressionResult.get(expr, exprAction, ActionItem.class);
        }
        return null;
    }

    public void profileValidation() throws Exception {
        if (!argumentEntities.stream().anyMatch(argument -> argument.getKey().equals("expr"))) {
            List<String> args = argumentEntities.stream()
                .map(argument -> argument.getKey())
                .collect(Collectors.toList());
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                "Cannot build the expression: Missing argument, the class need [expr] as parameters. Parameters given "
                    + args
            );
            throw missingParameters;
        }

        final String expr = argumentEntities.get(0).getValue();
        final ExpressionError expressionError = ExpressionResult
            .isValid(expr, new ExprAction(1, VR.AE,
                DicomObject.newDicomObject(), DicomObject.newDicomObject()), ActionItem.class);

        if (!expressionError.isValid()) {
            IllegalArgumentException expressionNotValid = new IllegalArgumentException(
                    String.format("Expression is not valid: \n\r%s", expressionError.getMsg())
            );
            throw expressionNotValid;
        }
    }


}
