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
}
