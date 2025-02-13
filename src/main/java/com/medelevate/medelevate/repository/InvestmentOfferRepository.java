package com.medelevate.medelevate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medelevate.medelevate.models.FundingRequest;
import com.medelevate.medelevate.models.InvestmentOffer;
import com.medelevate.medelevate.models.User;

public interface InvestmentOfferRepository extends JpaRepository<InvestmentOffer, Long>{
	public List<InvestmentOffer> findByFundingRequestAndInvestor(FundingRequest fundingRequest, User investor);
	public List<InvestmentOffer> findByInvestorAndStatus(User investor, String status);
	public List<InvestmentOffer> findByFundingRequestAndStatus(FundingRequest request, String status);
}
