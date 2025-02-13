package com.medelevate.medelevate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medelevate.medelevate.models.Startup;
import com.medelevate.medelevate.models.User;

public interface StartupRepository extends JpaRepository<Startup,Long>{
	public Startup findByRegistrationNumber(String registrationNumber);
	public Startup findByFounder(User founder);
}
