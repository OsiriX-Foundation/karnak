package org.karnak.backend.security;

import java.util.Objects;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Default behaviour to load the IDP If property IDP is missing or different from keycloak: load the
 * in memory IDP
 */
public class DefaultIdpLoadCondition implements Condition {

  // Environment properties defining IDP method chosen
  private static final String PROPERTY_IDP = "IDP";
  private static final String PROPERTY_IDP_KEYCLOAK_VALUE = "keycloak";

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    // Define default IDP
    return !Objects.equals(
        context.getEnvironment().getProperty(PROPERTY_IDP), PROPERTY_IDP_KEYCLOAK_VALUE);
  }
}