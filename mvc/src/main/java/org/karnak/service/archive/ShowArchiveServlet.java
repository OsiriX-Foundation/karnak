package org.karnak.service.archive;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.karnak.data.FileInfo;
import org.karnak.service.AbstractGateway;
import org.karnak.service.GlobalConfig;
import org.karnak.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = "/archive.xml")
public class ShowArchiveServlet extends HttpServlet {

    private static final long serialVersionUID = -4229230848823235305L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowArchiveServlet.class);

    @Override
    public final void init() throws ServletException {
        GlobalConfig globalConfig = (GlobalConfig) this.getServletContext().getAttribute("globalConfig");
        if (globalConfig == null) {
            LOGGER.error("GlobalConfig is missing. Echo service cannot start.");
            destroy();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();
        GlobalConfig globalConfig = (GlobalConfig) this.getServletContext().getAttribute("globalConfig");
        if (globalConfig == null) {
            String errorMsg = "Missing 'GlobalConfig' from current ServletContext";
            LOGGER.error(errorMsg);
            ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
            return;
        }
        
        final File archiveDir = globalConfig.getConfigIn().getStoreDir();
        String result;
        if (archiveDir == null || !archiveDir.isDirectory() || !archiveDir.canRead()) {
            result = "<archive/>";
        } else {
            StringBuilder sb = new StringBuilder("<archive>\n");

            File[] aet = archiveDir.listFiles((FileFilter) pathname -> {
                if (pathname.isDirectory()) {
                    return AbstractGateway.isFolderContainsFile(pathname);
                }
                return false;
            });

            for (int i = 0; i < aet.length; i++) {
                sb.append("<aet name=\"");
                sb.append(aet[i].getName());
                sb.append("\">\n");
                scanFiles(aet[i], sb);
                sb.append("</aet>\n");
            }

            sb.append("</archive>\n");
            result = sb.toString();
        }
        out.println(result);
    }

    private static void scanFiles(File aStartingDir, StringBuilder sb) {
        File[] filesAndDirs = aStartingDir.listFiles();
        for (File file : filesAndDirs) {
            if (file.isDirectory()) {
                scanFiles(file, sb);
            }
            // Dicom Listener use ".part" extension when it is writing the file until the file is completely downloaded
            else if (!file.getName().endsWith(".part")) {
                FileInfo info = new FileInfo(file);

                sb.append("<file tsuid=\"");
                sb.append(info.getTsuid());
                sb.append("\" cuid\"");
                sb.append(info.getCuid());
                sb.append("\" iuid\"");
                sb.append(info.getIuid());
                sb.append("\">\n");
                sb.append(file.getName());
                sb.append("</file>\n");
            }
        }
    }
}
