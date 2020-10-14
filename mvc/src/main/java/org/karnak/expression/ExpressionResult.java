package org.karnak.expression;

import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.expression.ExprConditionKheops;
import org.karnak.profilepipe.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ExpressionResult {
    public static boolean getBoolean(String condition, ExprConditionKheops exprConditionKheops){
        try {
            //https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
            final ExpressionParser parser = new SpelExpressionParser();
            final EvaluationContext context = new StandardEvaluationContext(exprConditionKheops);
            final String cleanCondition = exprConditionKheops.conditionInterpreter(condition);
            context.setVariable("VR", VR.class);
            context.setVariable("Tag", Tag.class);
            final Expression exp = parser.parseExpression(cleanCondition);
            boolean valid = exp.getValue(context, Boolean.class);
            return valid;
        } catch (final Exception e) {
            return false;
        }
    }

    public static boolean getResultCondition(String condition, ExprDCMElem exprDCMElem){
        final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);
        if (condition!=null) {
            try {
                //https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
                final ExpressionParser parser = new SpelExpressionParser();
                final EvaluationContext context = new StandardEvaluationContext(exprDCMElem);
                final String cleanCondition = exprDCMElem.conditionInterpreter(condition);
                context.setVariable("VR", VR.class);
                context.setVariable("Tag", Tag.class);
                final Expression exp = parser.parseExpression(cleanCondition);
                return exp.getValue(context, Boolean.class);
            } catch (final Exception e) {
                LOGGER.error("Cannot execute the parser expression for this expression: {}", condition, e);
            }
        }
        return true; // if there is no condition we return true by default
    }
}
