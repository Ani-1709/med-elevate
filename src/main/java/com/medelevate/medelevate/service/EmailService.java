package com.medelevate.medelevate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.UserRepository;

@Service
public class EmailService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmailId;
	
	public void sendRegistrationSuccessfullEmail(User user) {
		String email=user.getEmail();
		SimpleMailMessage message=new SimpleMailMessage();
		String text="Dear "+user.getName()+", you have successfull registered yourself on our platform. Hope you have a nice time!";
		String subject="MED-ELEVATE || SUCCESSFULL REGISTRATION";
		message.setTo(email);
		message.setSubject(subject);
		message.setText(text);
		message.setFrom(fromEmailId);
		javaMailSender.send(message);
	}

	public void sendSuccessfullStartupRegistrationMail(User user) {
		String email=user.getEmail();
		SimpleMailMessage message=new SimpleMailMessage();
		String text="Dear "+user.getName()+", your startup "+user.getStartup()+" has been registered on our platform.";
		String subject="MED-ELEVATE || SUCCESSFULL STARTUP REGISTRATION";
		message.setTo(email);
		message.setSubject(subject);
		message.setFrom(fromEmailId);
		message.setText(text);
		javaMailSender.send(message);
	}
	
	public void sendMeetingLink(User user, User mentor, String meetingLink) {
	    String userEmail = user.getEmail();
	    String mentorEmail = mentor.getEmail();
	    
	    SimpleMailMessage userMessage = new SimpleMailMessage();
	    userMessage.setTo(userEmail);
	    userMessage.setSubject("MED-ELEVATE || MEETING LINK FOR MENTORSHIP");
	    userMessage.setFrom(fromEmailId);
	    userMessage.setText("Here is the link for your mentorship session by " + mentor.getName() + 
	                        " (" + mentorEmail + ") : " + meetingLink + 
	                        ". For more details, visit your dashboard.");
	    javaMailSender.send(userMessage);
	    
	    SimpleMailMessage mentorMessage = new SimpleMailMessage();
	    mentorMessage.setTo(mentorEmail);
	    mentorMessage.setSubject("MED-ELEVATE || MEETING LINK FOR CLASS");
	    mentorMessage.setFrom(fromEmailId);
	    mentorMessage.setText("Here is the link you generated for your meeting with " + userEmail + 
	                          " : " + meetingLink + 
	                          ". For more details, visit your dashboard.");
	    javaMailSender.send(mentorMessage);
	}

	public void sendQueryMail(String senderMail, String query) {
		SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(fromEmailId);
	    message.setSubject("MED-ELEVATE || QUERY");
	    message.setFrom(senderMail);
	    message.setText(query);
	    javaMailSender.send(message);
	}
}
