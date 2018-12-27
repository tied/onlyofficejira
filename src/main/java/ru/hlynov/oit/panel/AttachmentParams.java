package ru.hlynov.oit.panel;

import java.sql.Timestamp;

public class AttachmentParams {
    private String fileName;
    private Long fileSize;
    private String author;
    private Timestamp created;
    private Long id;


    public AttachmentParams(String fileName, Long fileSize, String author, Timestamp created, Long id) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.author = author;
        this.created = created;
        this.id = id;

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
}
