package ru.hlynov.oit.panel;

import java.sql.Timestamp;

public class AttachmentParams {
    private String fileName;
    private Long fileSize;
    private String author;
    private Timestamp created;
    private Long id;
    private String fileSizeDisplay;

    public AttachmentParams(String fileName, Long fileSize, String author, Timestamp created, Long id) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.author = author;
        this.created = created;
        this.id = id;
        this.fileSizeDisplay = getFileSizeDisplayFromLong(fileSize);

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        this.fileSizeDisplay = getFileSizeDisplayFromLong(fileSize);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileSizeDisplay() {
        return fileSizeDisplay;
    }

    private String getFileSizeDisplayFromLong(Long fileSize) {
        if (fileSize % 1000 > 0) {
            return String.valueOf((Long)(fileSize / 1000)) + " kB";
        }

        if (fileSize % 1000000 > 0) {
            return String.valueOf((Long)(fileSize / 1000000)) + " MB";
        }

        return String.valueOf(fileSize);
    }
}
