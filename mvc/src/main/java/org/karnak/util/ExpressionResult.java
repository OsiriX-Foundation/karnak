package org.karnak.util;

import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.kheops.ExprConditionKheops;
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
}
