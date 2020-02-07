package org.karnak.data;

import java.io.File;
import java.util.Objects;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfo.class);

    private final String iuid;
    private final String cuid;
    private final String tsuid;
    private final String filename;

    public FileInfo(File file) {
        Objects.requireNonNull(file);
        this.filename = file.getName();

        String t = null;
        String i = null;
        String c = null;
        try (DicomInputStream in = new DicomInputStream(file)) {
            in.setIncludeBulkData(IncludeBulkData.NO);
            t = in.getTransferSyntax();
            Attributes fmi = in.readFileMetaInformation();
            if (fmi == null || !fmi.containsValue(Tag.MediaStorageSOPClassUID)
                || !fmi.containsValue(Tag.MediaStorageSOPInstanceUID)) {
                Attributes ds = in.readDataset(-1, Tag.PixelData);
                fmi = ds.createFileMetaInformation(in.getTransferSyntax());
            }
            i = fmi.getString(Tag.MediaStorageSOPInstanceUID);
            c = fmi.getString(Tag.MediaStorageSOPClassUID);
        } catch (Exception e) {
            LOGGER.error("Cannot read FileMetaInformation of {}", file.getName(), e);
        }

        this.iuid = i;
        this.cuid = c;
        this.tsuid = t;
    }

    public FileInfo(String filename, String iuid, String cuid, String tsuid) {
        this.filename = filename;
        this.iuid = iuid;
        this.cuid = cuid;
        this.tsuid = tsuid;
    }

    public String getFilename() {
        return filename;
    }

    public String getCuid() {
        return cuid;
    }

    public String getIuid() {
        return iuid;
    }

    public String getTsuid() {
        return tsuid;
    }

}