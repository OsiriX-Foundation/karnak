package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.Profiles;
import org.karnak.profilepipe.action.AbstractAction;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.utils.ExprDCMElem;
import org.karnak.profilepipe.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, String PatientID) {
        if (exceptedTagsAction.get(dcmElem.tag()) == null && tagsAction.get(dcmElem.tag()) != null) {
            final String expr = arguments.get(0).getValue();
            final ExprDCMElem exprDCMElem = new ExprDCMElem(dcmElem.tag(), dcmElem.vr(), dcm, dcmCopy);
            return getResultCondition(expr, exprDCMElem);
        }
        return null;
    }

    public static ActionItem getResultCondition(String expr, ExprDCMElem exprDCMElem){
        final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);
        if (expr!=null) {
            try {
                final ExpressionParser parser = new SpelExpressionParser();
                final EvaluationContext context = new StandardEvaluationContext(exprDCMElem);
                final String cleanCondition = exprDCMElem.conditionInterpreter(expr);
                context.setVariable("VR", VR.class);
                context.setVariable("TAG", Tag.class);
                final org.springframework.expression.Expression exp = parser.parseExpression(cleanCondition);
                return exp.getValue(context, ActionItem.class);
            } catch (final Exception e) {
                LOGGER.error("Cannot execute the parser expression for this expression: {}", expr, e);
            }
        }
        return null; // if there is no action we return null by default
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
    }


}
