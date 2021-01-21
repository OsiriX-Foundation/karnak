/*
* Copyright (c) 2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
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
