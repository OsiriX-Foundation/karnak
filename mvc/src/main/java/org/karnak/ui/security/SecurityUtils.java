package org.karnak.ui.security;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.shared.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class SecurityUtils {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);

  /**
   * Determines if a request is internal to Vaadin
   *
   * @param request Request
   * @return true if it is a internal request
   */
  static boolean isFrameworkInternalRequest(HttpServletRequest request) {
    final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
    return parameterValue != null
            && Stream.of(HandlerHelper.RequestType.values())
            .anyMatch(r -> r.getIdentifier().equals(parameterValue));
  }

  /**
   * Checks if the current user is logged in
   *
   * @return true if the current user is logged in
   */
  static boolean isUserLoggedIn() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
  }

  /**
   * Checks if the user is logged and is an admin
   *
   * @return true if the user is logged and is an admin
   */
  static boolean isUserAdmin() {
    return SecurityUtils.isUserLoggedIn()
            && SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(ga -> Objects.equals(ga.getAuthority(), SecurityRole.ADMIN_ROLE.getRole()));
  }

  /**
   * Check if the role of the user can access the view
   *
   * @param securedClass Secured Class
   * @return true if access is granted
   */
  public static boolean isAccessGranted(Class<?> securedClass) {
    boolean isAccessGranted = false;

    if (isUserLoggedIn()) {
      // get the secured annotation
      Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);

      // allow if no roles are required
      if (secured == null) {
        isAccessGranted = true;
      } else {
        // lookup needed role in user roles
        List<String> allowedRoles = Arrays.asList(secured.value());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        isAccessGranted =
                authentication != null
                        && authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(allowedRoles::contains);
      }
    }
    return isAccessGranted;
  }

  /**
   * Sign out method
   */
  public static void signOut() {
    try {
      VaadinServletService.getCurrentServletRequest().logout();
    } catch (ServletException e) {
      LOG.error("Error during logout");
    }
  }
}
