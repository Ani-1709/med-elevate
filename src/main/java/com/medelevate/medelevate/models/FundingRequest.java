package com.medelevate.medelevate.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class FundingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @Column(nullable = false)
    private Double amountRequested;

    @Column(nullable = false, length = 1000)
    private String purpose;

    @Column(nullable = false)
    private String fundingStage;

    @Column(nullable = false)
    private String investorPreferences;

    @Column(nullable = false)
    private Date submissionDate;

    @Column(nullable = false)
    private String status = "Pending"; // "Pending", "Partially Funded", "Fully Funded"

    @Column(length = 2000)
    private String feedback; 

    @OneToMany(mappedBy = "fundingRequest", cascade = CascadeType.ALL)
    private List<InvestmentOffer> investmentOffers = new ArrayList<>();

    @Column(nullable=false)
    private Double amountFunded = 0.0;

    public FundingRequest() {}

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

    public Double getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(Double amountRequested) {
        this.amountRequested = amountRequested;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getFundingStage() {
        return fundingStage;
    }

    public void setFundingStage(String fundingStage) {
        this.fundingStage = fundingStage;
    }

    public String getInvestorPreferences() {
        return investorPreferences;
    }

    public void setInvestorPreferences(String investorPreferences) {
        this.investorPreferences = investorPreferences;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Double getAmountFunded() {
        return amountFunded;
    }

    public void setAmountFunded(Double amountFunded) {
        this.amountFunded = amountFunded;
    }

    public List<InvestmentOffer> getInvestmentOffers() {
        return investmentOffers;
    }

    public void setInvestmentOffers(List<InvestmentOffer> investmentOffers) {
        this.investmentOffers = investmentOffers;
    }

    public void updateAmountFunded() {
        double totalFunded = 0.0;
        for (InvestmentOffer offer : investmentOffers) {
            if ("Approved".equals(offer.getStatus())) {
                totalFunded += offer.getAmountOffered();
            }
        }
        this.amountFunded = totalFunded;

        if (amountFunded == 0) {
            this.status = "Pending";
        } else if (amountFunded < amountRequested) {
            this.status = "Partially Funded";
        } else {
            this.status = "Fully Funded";
        }
    }
}
