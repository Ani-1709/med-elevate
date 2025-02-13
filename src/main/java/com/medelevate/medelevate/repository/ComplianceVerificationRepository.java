package com.medelevate.medelevate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medelevate.medelevate.models.ComplianceVerification;
import com.medelevate.medelevate.models.Startup;
import com.medelevate.medelevate.models.User;

public interface ComplianceVerificationRepository extends JpaRepository<ComplianceVerification, Long>{
	List<ComplianceVerification> findByStatus(String status);
	List<ComplianceVerification> findByReviewedByAndStatus(User reviewer, String string);
	List<ComplianceVerification> findByStartup(Startup startup);
	List<ComplianceVerification> findByStatusOrderBySubmittedDateDesc(String status);
	List<ComplianceVerification> findByStartupAndStatus(Startup startup, String status);
}
