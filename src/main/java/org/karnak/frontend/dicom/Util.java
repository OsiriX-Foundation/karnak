/*
 * Copyright (c) 2020-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.dicom;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.dcm4che3.net.Status;
import org.weasis.dicom.op.Echo;
import org.weasis.dicom.param.AdvancedParams;
import org.weasis.dicom.param.ConnectOptions;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.DicomState;

public class Util {

	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

	private Util() {
	}

	private static <T> T timedCall(Callable<T> c, long timeout, TimeUnit timeUnit)
			throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<T> task = new FutureTask<>(c);
		THREAD_POOL.execute(task);
		return task.get(timeout, timeUnit);
	}

	private static Future<Boolean> portIsOpen(final ExecutorService es, final String host, final int port,
			final int timeout) {
		return es.submit(() -> {
      try (Socket socket = new Socket()) {
        socket.connect(new InetSocketAddress(host, port), timeout);
        socket.close();
        return true;
      } catch (Exception ex) {
        return false;
      }
    });
	}

	public static boolean getNetworkResponse(StringBuilder result, String host, int port,
			boolean fontIcon) {

		return getNetworkResponse(result, host, port, fontIcon, "HTML");
	}

	public static boolean getNetworkResponse(StringBuilder result, String host, int port, boolean fontIcon,
			String format) {

		boolean reachable = false;
		boolean xml = "XML".equalsIgnoreCase(format);
		try {
			if ("XML".equalsIgnoreCase(format)) {
				result.append("<DcmNetworkStatus>");
			}

			InetAddress address = InetAddress.getByName(host);
			reachable = isReachableByPing(host);

			if (reachable) {

				if (xml) {
					result.append(address);
					result.append(" machine is turned on and can be pinged");
				}
				else {
					result.append("<span style=\"color:green\">");
					result.append(getOKItem(fontIcon));
					result.append("</span> ");
					result.append(address);
					result.append(" machine is turned on and can be pinged.<br>");
				}
			}
			else if (address.getHostAddress().equals(address.getHostName())) {
				if (xml) {
					result.append(address);
					result.append(" host address and host name are equal, meaning the host name could not be resolved");
				}
				else {
					result.append("<span style=\"color:orange\">");
					result.append(getWarningItem(fontIcon));
					result.append("</span> ");
					result.append(address);
					result.append(
							" host address and host name are equal, meaning the host name could not be resolved.<br>");
				}
			}
			else {
				if (xml) {
					result.append(address);
					result.append(" machine is known in a DNS lookup but cannot be pinged");
				}
				else {
					result.append("<span style=\"color:orange\">");
					result.append(getWarningItem(fontIcon));
					result.append("</span> ");
					result.append(address);
					result.append(" machine is known in a DNS lookup but cannot be pinged.<br>");
				}
			}

			if (xml) {
				result.append("</DcmNetworkStatus>");
			}

		}
		catch (Throwable e) {
			if (xml) {
				result.append("Network unexpected error: ");
				result.append(e.getMessage());
				result.append("</DcmNetworkStatus>");
			}
			else {
				result.append("<span style=\"color:red\">");
				result.append(getWarningItem(fontIcon));
				result.append(" Network unexpected error: ");
				result.append(e.getMessage());
				result.append("</span><br>");
			}
		}

		if (!reachable) {

			if (xml) {
				// Do nothing
			}
			else {
				Future<Boolean> future = portIsOpen(THREAD_POOL, host, port, 2500);
				try {
					reachable = future.get();
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				catch (Exception e) {
					// Do nothing
				}

				result.append(reachable ? "<span style=\"color:green\">" + getOKItem(fontIcon)
						: "<span style=\"color:red\">" + getWarningItem(fontIcon));
				result.append("</span> ");
				result.append(host);
				result.append(reachable ? " is listening on port " : " is not listening on port ");
				result.append(port);
				result.append(".<br>");
			}
		}
		return reachable;
	}

	private static boolean isReachableByPing(String host) throws Exception {
		final String[] cmd;
		if (System.getProperty("os.name").startsWith("Win")) {
			// For Windows
			cmd = new String[] { "ping", "-n", "1", host };
		}
		else {
			// For Linux and OSX
			cmd = new String[] { "ping", "-c", "1", host };
		}

		try {
			int returnCode = timedCall(() -> {
				Process process = Runtime.getRuntime().exec(cmd);
				process.waitFor();
				return process.exitValue();
			}, 2500, TimeUnit.MILLISECONDS);
			return returnCode == 0;
		}
		catch (TimeoutException e) {
			System.out.println("Ping timeout after 2.5 sec of " + host);
		}

		return false;
	}

	public static boolean getEchoResponse(StringBuilder result, String callingAET, DicomNode calledNode,
			boolean fontIcon, String format) {

		return getEchoResponse(result, callingAET, calledNode, fontIcon, format, null);
	}

	public static boolean getEchoResponse(StringBuilder result, String callingAET, DicomNode calledNode,
			boolean fontIcon, String format, Integer connectTimeout) {

		boolean success = false;
		boolean xml = "XML".equalsIgnoreCase(format);
		try {
			AdvancedParams params = new AdvancedParams();

			DicomState state;
			if (connectTimeout != null) {
				ConnectOptions connectOptions = new ConnectOptions();
				connectOptions.setConnectTimeout(connectTimeout);
				params.setConnectOptions(connectOptions);
				state = Echo.process(params, new DicomNode(callingAET), calledNode);
			}
			else {
				state = Echo.process(callingAET, calledNode);
			}
			success = state.getStatus() == Status.Success;

			if (xml) {
				result.append("<DcmStatus>");

				if (success) {
					result.append("Success").append("</DcmStatus>");
					result.append("<DcmStatusMessage>").append(state.getMessage()).append("</DcmStatusMessage>");
				}
				else {
					result.append("Error ").append(Integer.toHexString(state.getStatus())).append("</DcmStatus>");
					result.append("<DcmStatusMessage>").append(state.getMessage()).append("</DcmStatusMessage>");
				}
			}
			else {
				// "HTML" and anything else
				result.append(success ? "<span style=\"color:green\">" + getOKItem(fontIcon)
						: "<span style=\"color:red\">" + getWarningItem(fontIcon));

				result.append("</span> DICOM Status: ");
				if (success) {
					result.append("Success");
				}
				else {
					result.append("error code ");
					result.append(Integer.toHexString(state.getStatus()));
				}
				result.append("<br>DICOM Message: ");
				result.append(state.getMessage());
				result.append("<br>");
			}

		}
		catch (Throwable e) {
			if (xml) {
				result.append("<DcmStatus>DICOM unexpected error</DcmStatus>");
				result.append("<DcmStatusMessage>").append(e.getMessage()).append("</DcmStatusMessage>");
			}
			else {
				// "HTML" and anything else
				result.append("<span style=\"color:red\">");
				result.append(getWarningItem(fontIcon));
				result.append(" DICOM unexpected error: ");
				result.append(e.getMessage());
				result.append("</span><br>");
			}
		}
		return success;
	}

	private static String getWarningItem(boolean fontIcon) {
		if (fontIcon) {
			return "<vaadin-icon icon=\"vaadin:exclamation-circle\" style=\"width:1em; height:1em;\"></vaadin-icon>";
		}
		return "WARN";
	}

	private static String getOKItem(boolean fontIcon) {
		if (fontIcon) {
			return "<vaadin-icon icon=\"vaadin:check-circle\" style=\"width:1em; height:1em;\"></vaadin-icon>";
		}
		return "OK";
	}

}
