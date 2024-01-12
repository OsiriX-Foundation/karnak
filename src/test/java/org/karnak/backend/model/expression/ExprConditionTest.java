package org.karnak.backend.model.expression;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;

class ExprConditionTest {

  /**
   * Method under test:
   *
   * <ul>
   *   <li>{@link ExprCondition#ExprCondition(Attributes)} ()}
   *   <li>{@link ExprCondition#tagValueBeginsWith(int, String)} ()}
   *   <li>{@link ExprCondition#tagValueContains(int, String)}
   *   <li>{@link ExprCondition#tagValueEndsWith(int, String)}
   *   <li>{@link ExprCondition#tagValueIsPresent(String, String)}
   *   <li>{@link ExprCondition#tagIsPresent(int)}
   * </ul>
   */
  @Test
  void testExprConditionsWithMultipleStrings() {
    Attributes attributes = new Attributes();
    attributes.setString(Tag.ImageType, VR.CS, "ORIGINAL", "PRIMARY", "LABEL", "NONE");
    ExprCondition exprCondition = new ExprCondition(attributes);

    assertTrue(exprCondition.tagIsPresent(Tag.ImageType));
    assertTrue(exprCondition.tagValueIsPresent(Tag.ImageType, "ORIGINAL\\PRIMARY\\LABEL\\NONE"));

    assertTrue(exprCondition.tagValueContains(Tag.ImageType, "LABEL"));
    assertTrue(exprCondition.tagValueContains(Tag.ImageType, "NONE"));

    assertTrue(exprCondition.tagValueBeginsWith(Tag.ImageType, "ORIGINAL\\PRIMARY"));
    assertTrue(exprCondition.tagValueEndsWith(Tag.ImageType, "NONE"));
  }
}
