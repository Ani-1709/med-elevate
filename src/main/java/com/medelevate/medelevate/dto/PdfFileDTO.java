package com.medelevate.medelevate.dto;

import org.springframework.web.multipart.MultipartFile;

public class PdfFileDTO {

    private String fileName;
    private MultipartFile file; 

    public PdfFileDTO() {}

    public PdfFileDTO(String fileName, MultipartFile file) {
        this.fileName = fileName;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
