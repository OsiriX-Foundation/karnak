/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.authentication;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

/**
 * Class for retrieving and setting the name of the current user of the current session
 * (without using JAAS). All methods of this class require that a {@link VaadinRequest} is
 * bound to the current thread.
 *
 * @see VaadinService#getCurrentRequest()
 */
public final class CurrentUser {

	/** The attribute key used to store the username in the session. */
	public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();

	private CurrentUser() {
	}

	/**
	 * Returns the name of the current user stored in the current session, or an empty
	 * string if no user name is stored.
	 * @throws IllegalStateException if the current session cannot be accessed.
	 */
	public static String get() {
		String currentUser = (String) getCurrentRequest().getWrappedSession()
			.getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
		if (currentUser == null) {
			return "";
		}
		else {
			return currentUser;
		}
	}

	/**
	 * Sets the name of the current user and stores it in the current session. Using a
	 * {@code null} username will remove the username from the session.
	 * @throws IllegalStateException if the current session cannot be accessed.
	 */
	public static void set(String currentUser) {
		if (currentUser == null) {
			getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
		}
		else {
			getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
		}
	}

	private static VaadinRequest getCurrentRequest() {
		VaadinRequest request = VaadinService.getCurrentRequest();
		if (request == null) {
			throw new IllegalStateException("No request bound to current thread.");
		}
		return request;
	}

}
