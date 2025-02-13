package com.medelevate.medelevate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medelevate.medelevate.models.MentorshipRequest;
import com.medelevate.medelevate.models.Startup;
import com.medelevate.medelevate.models.User;

public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest,Long>{
	public List<MentorshipRequest> findByStartup(Startup startup);
	public List<MentorshipRequest> findByMentorAssigned(User mentor);
	public List<MentorshipRequest> findByStartupAndStatus(Startup startup, String status);
}
