package com.medelevate.medelevate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medelevate.medelevate.dto.RegisterDto;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private EmailService emailService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user=userRepository.findByEmail(email);
		if(user!=null) {
			return org.springframework.security.core.userdetails.User
					.withUsername(user.getEmail())
					.password(user.getPassword())
					.roles(user.getRole())
					.build();
		}
		throw new UsernameNotFoundException("User does not exist");
	}
	//SAVE USER TO DATABASE
	private User saveUser(RegisterDto regDto, String role) {
		User user=new User();
		user.setEmail(regDto.getEmail());
		user.setName(regDto.getName());
		user.setPassword(passwordEncoder.encode(regDto.getPassword()));
		user.setRole(role);
		emailService.sendRegistrationSuccessfullEmail(user);
		return userRepository.save(user);
	}
	//FOR SAVING STARTUP USERS
	public User saveUserStartup(RegisterDto regDto) {
		return saveUser(regDto,"STARTUP");
	}
	//FOR SAVING MENTOR USERS
	public User saveUserMentor(RegisterDto regDto) {
		return saveUser(regDto,"MENTOR");
	}
	//FOR SAVING INVESTOR USERS
	public User saveUserInvestor(RegisterDto regDto) {
		return saveUser(regDto,"INVESTOR");
	}
	//FOR SAVING COMPLIANCE REVIEWER USERS
	public User saveUserReviewer(RegisterDto regDto) {
		return saveUser(regDto,"REVIEWER");
	}
}
