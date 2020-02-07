package org.karnak.service.archive;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.karnak.service.AbstractGateway;
import org.karnak.service.GlobalConfig;
import org.karnak.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.api.util.FileUtil;

@WebServlet(urlPatterns = "/download")
public class DownloadingServlet extends HttpServlet {

    private static final long serialVersionUID = -3991470951272725755L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadingServlet.class);

    @Override
    public final void init() throws ServletException {
        GlobalConfig globalConfig = (GlobalConfig) this.getServletContext().getAttribute("globalConfig");
        if (globalConfig == null) {
            LOGGER.error("DownloadingServlet service cannot start: {}", "GlobalConfig is missing.");
            destroy();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        GlobalConfig globalConfig = (GlobalConfig) this.getServletContext().getAttribute("globalConfig");
        if (globalConfig == null) {
            String errorMsg = "Missing 'GlobalConfig' from current ServletContext";
            LOGGER.error(errorMsg);
            ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
            return;
        }
        final File archiveDir = globalConfig.getConfigIn().getStoreDir();

        try {
            if (archiveDir == null || !archiveDir.isDirectory() || !archiveDir.canRead()) {
                throw new IllegalStateException("Cannot access to the archive directory");
            } else {
                String aet = req.getParameter("aet");
                String filename = req.getParameter("sopuid");
                if (aet != null && filename != null) {
                    File file = new File(archiveDir, aet + File.separator + filename);
                    String delete = req.getParameter("delete");
                    if ("true".equals(delete)) {
                        FileUtil.delete(file);
                    } else {
                        download(res, file);
                    }
                }
            }
        } catch (Exception e1) {
            LOGGER.error("Unexpected exception when downloading", e1);
            ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage());
        }

        try {
            AbstractGateway.deleteOldFiles(archiveDir);
        } catch (SecurityException e) {
            LOGGER.error("SecurityException:", e);
        }
    }

    /**
     * Sends a file to the ServletResponse output stream. Typically you want the browser to receive a different name
     * than the name the file has been saved in your local database, since your local names need to be unique.
     *
     * @param req
     *            The request
     * @param resp
     *            The response
     * @param filename
     *            The name of the file you want to download.
     * @param original_filename
     *            The name the browser should receive.
     * @throws IOException
     */
    private boolean download(HttpServletResponse resp, File file) throws IOException {
        if (file == null || !file.canRead()) {
            LOGGER.warn("Cannot get this file for downloading: {}", file);
            return false;
        }

        try (DataInputStream in = new DataInputStream(new FileInputStream(file));
                        ServletOutputStream op = resp.getOutputStream()) {
            int length = 0;
            resp.setContentType("application/octet-stream");
            resp.setContentLength((int) file.length());
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            byte[] buf = new byte[4096];
            while ((length = in.read(buf)) != -1) {
                op.write(buf, 0, length);
            }
            op.flush();
            return true;
        }
    }

}
