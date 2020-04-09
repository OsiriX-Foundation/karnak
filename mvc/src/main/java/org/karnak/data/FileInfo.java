package org.karnak.data;

public final class FileInfo {

    private final String iuid;
    private final String cuid;
    private final String tsuid;
    private final String filename;

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