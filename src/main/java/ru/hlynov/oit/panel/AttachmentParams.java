package ru.hlynov.oit.panel;

import java.sql.Timestamp;

public class AttachmentParams {
    private String fileName;
    private Long fileSize;
    private String author;
    private Timestamp created;
    private Long id;
    private String fileSizeDisplay;
    private boolean Editable;

    public AttachmentParams(String fileName, Long fileSize, String author, Timestamp created, Long id) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.author = author;
        this.created = created;
        this.id = id;
        this.fileSizeDisplay = getFileSizeDisplayFromLong(fileSize);
        this.Editable = getEditableFromFileName(fileName);

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

    public boolean isEditable() {
        return Editable;
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

    private boolean getEditableFromFileName(String fileName) {
        // разрешаем редактирование только для doc docx xls xlsx
        String fileExt;

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {

            fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (fileExt.equals("doc") || fileExt.equals("docx") || fileExt.equals("xls") || fileExt.equals("xlsx")) {
                return true;
            }
        }
        return false;
    }
}
