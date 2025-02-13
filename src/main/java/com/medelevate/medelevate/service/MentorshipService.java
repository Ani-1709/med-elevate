package com.medelevate.medelevate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medelevate.medelevate.models.MentorshipRequest;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.MentorshipRequestRepository;
import com.medelevate.medelevate.repository.UserRepository;

@Service
public class MentorshipService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MentorshipRequestRepository mentorshipRequestRepository;
	
	
}
