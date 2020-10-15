package org.karnak.expression;

import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.profilepipe.Profiles;
import org.karnak.profilepipe.action.ActionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

//https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
public class ExpressionResult {
    public static Object get(String condition, ExpressionItem expressionItem, Class<?> typeOfReturn){
        final Logger LOGGER = LoggerFactory.getLogger(ExpressionResult.class);
        try {
            final ExpressionParser parser = new SpelExpressionParser();
            final EvaluationContext context = new StandardEvaluationContext(expressionItem);
            final String cleanCondition = expressionItem.conditionInterpreter(condition);
            context.setVariable("VR", VR.class);
            context.setVariable("Tag", Tag.class);
            final Expression exp = parser.parseExpression(cleanCondition);
            return exp.getValue(context, typeOfReturn);
        } catch (final Exception e) {
            throw new IllegalStateException(String.format("Cannot execute the parser expression for this expression: %s", condition));
        }
    }





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

}
