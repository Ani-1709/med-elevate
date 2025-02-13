package com.medelevate.medelevate.dto;

import org.springframework.web.multipart.MultipartFile;

public class ComplianceVerificationDTO {

	private String fileName;
    private MultipartFile file; 
	private String documentType;
	private byte[] document;
	
	public ComplianceVerificationDTO() {}

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

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}
}
