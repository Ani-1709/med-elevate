package com.medelevate.medelevate.models;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "compliance_verification_requests")
public class ComplianceVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String documentUrl; // Stores the file URL instead of LONGBLOB

    @Column(nullable = false)
    private String status; // Status: Pending, Approved, Rejected

    @Column
    private String reviewerComments;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date submittedDate;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewedBy;
    
    @Column
    private double fee;
    
    @Column
    private String founderComments;
    
    @Column
    private String founderFeedback;

    public ComplianceVerification() {}

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = "Pending";
        }
        if (this.submittedDate == null) {
            this.submittedDate = new Date();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Startup getStartup() {
        return startup;
    }

    public void setStartup(Startup startup) {
        this.startup = startup;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public String getFounderComments() {
		return founderComments;
	}

	public void setFounderComments(String founderComments) {
		this.founderComments = founderComments;
	}

	public String getFounderFeedback() {
		return founderFeedback;
	}

	public void setFounderFeedback(String founderFeedback) {
		this.founderFeedback = founderFeedback;
	}
    
}
