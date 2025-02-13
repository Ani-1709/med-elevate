package com.medelevate.medelevate.dto;

import jakarta.persistence.Column;

public class FundingRequestDTO {

	private Double amountRequested;
    private String purpose;
    private String fundingStage;
    private String investorPreferences;
    
    public FundingRequestDTO() {}

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
}
