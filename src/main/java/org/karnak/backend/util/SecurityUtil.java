/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.shared.ApplicationConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.stream.Stream;
import org.karnak.backend.enums.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public final class SecurityUtil {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityUtil.class);

	private SecurityUtil() {
	}

	/**
	 * Determines if a request is internal to Vaadin
	 * @param request Request
	 * @return true if it is a internal request
	 */
	public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
		final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
		return parameterValue != null && Stream.of(HandlerHelper.RequestType.values())
			.anyMatch(r -> r.getIdentifier().equals(parameterValue));
	}

	/**
	 * Checks if the current user is logged in
	 * @return true if the current user is logged in
	 */
	public static boolean isUserLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}

	/**
	 * Checks if the user is logged and is an admin
	 * @return true if the user is logged and is an admin
	 */
	public static boolean isUserAdmin() {
		return SecurityUtil.isUserLoggedIn() && SecurityContextHolder.getContext()
			.getAuthentication()
			.getAuthorities()
			.stream()
			.anyMatch(ga -> Objects.equals(ga.getAuthority(), SecurityRole.ADMIN_ROLE.getRole()));
	}

	/**
	 * Sign out method
	 */
	public static void signOut() {
		try {
			VaadinServletService.getCurrentServletRequest().logout();
		}
		catch (ServletException e) {
			LOG.error("Error during logout");
		}
	}

}
