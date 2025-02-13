package com.medelevate.medelevate.models;

import jakarta.persistence.*;

@Entity
@Table(name = "investment_offers")
public class InvestmentOffer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "funding_request_id", nullable = false)
    private FundingRequest fundingRequest;
    
    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    private User investor;
    
    @Column(nullable = false)
    private Double amountOffered;
    
    @Column(nullable = false, length = 1000)
    private String returnRequest; 

    @Column(nullable = false)
    private String status = "Pending"; // "Pending", "Approved", "Declined"

    public InvestmentOffer() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FundingRequest getFundingRequest() {
        return fundingRequest;
    }

    public void setFundingRequest(FundingRequest fundingRequest) {
        this.fundingRequest = fundingRequest;
    }

    public User getInvestor() {
        return investor;
    }

    public void setInvestor(User investor) {
        this.investor = investor;
    }

    public Double getAmountOffered() {
        return amountOffered;
    }

    public void setAmountOffered(Double amountOffered) {
        this.amountOffered = amountOffered;
    }

    public String getReturnRequest() {
        return returnRequest;
    }

    public void setReturnRequest(String returnRequest) {
        this.returnRequest = returnRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
