package com.medelevate.medelevate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medelevate.medelevate.models.FundingRequest;
import com.medelevate.medelevate.models.Startup;
import com.medelevate.medelevate.models.User;

@Repository
public interface FundingRequestRepository extends JpaRepository<FundingRequest,Long>{
	public List<FundingRequest> findByStartup(Startup startup);
	//public List<FundingRequest> findByApprovedByInvestor(User user);
	public List<FundingRequest> findByStatus(String status);
	public List<FundingRequest> findByStartupAndStatus(Startup startup, String status);
}
