package org.karnak.backend.enums;

import java.util.Arrays;

public enum SecurityRole {

  // Role admin
  ADMIN_ROLE("ROLE_ADMIN", "ADMIN"),
  // Role user
  USER_ROLE("ROLE_USER", "USER");

  /**
   * Role of the enum
   */
  private final String role;

  /**
   * Type of the enum
   */
  private final String type;

  /**
   * Constructor
   *
   * @param role Role of the enum
   * @param type Type of the enum
   */
  SecurityRole(final String role, final String type) {
    this.role = role;
    this.type = type;
  }

  /**
   * Get the enum from the role in parameter
   *
   * @param role Role of the enum
   * @return SecurityRole found
   */
  public static SecurityRole fromCode(final String role) {
    if (role != null) {
      return Arrays.stream(SecurityRole.values())
          .filter(r -> role.trim().equalsIgnoreCase(r.getRole()))
          .findFirst()
          .orElse(null);
    }
    return null;
  }

  /**
   * Getter for code
   *
   * @return Role of the enum
   */
  public String getRole() {
    return role;
  }

  /**
   * Getter for type
   *
   * @return Type of the enum
   */
  public String getType() {
    return type;
  }

  @Override
  public String toString() {
    return "SecurityRole{" + "role='" + role + '\'' + "type='" + type + '\'' + '}';
  }
}
