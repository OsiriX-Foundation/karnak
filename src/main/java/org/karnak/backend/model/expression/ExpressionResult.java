package org.karnak.backend.model.expression;

import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
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
            context.setVariable("VR", VR.class);
            context.setVariable("Tag", Tag.class);
            final Expression exp = parser.parseExpression(condition);
            return exp.getValue(context, typeOfReturn);
        } catch (final Exception e) {
            throw new IllegalStateException(String.format("Cannot execute the parser expression for this expression: %s", condition));
        }
    }

    public static ExpressionError isValid(String condition, ExpressionItem expressionItem, Class<?> typeOfReturn){
        try {
            final ExpressionParser parser = new SpelExpressionParser();
            final EvaluationContext context = new StandardEvaluationContext(expressionItem);
            context.setVariable("VR", VR.class);
            context.setVariable("Tag", Tag.class);
            final Expression exp = parser.parseExpression(condition);
            Object o = exp.getValue(context, typeOfReturn);
            return new ExpressionError(true, null);
        } catch (final Exception e) {
            return new ExpressionError(false, String.format("Expression is not valid: \n\r%s", e.getMessage()));
        }
    }
}
