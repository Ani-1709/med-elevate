package com.medelevate.medelevate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.userdetails.UserDetails;
import com.medelevate.medelevate.repository.UserRepository;
import com.medelevate.medelevate.service.EmailService;
import com.medelevate.medelevate.models.User;

@Controller
public class ContentController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/")
	public String homepage(Authentication authentication, Model model) {
		if(authentication==null) {
			return "index";
		}
		UserDetails userDetails=(UserDetails)authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		 if (user != null) {
		        if (user.getRole().equals("STARTUP")) {
		            String upperCaseName = user.getName().toUpperCase();
		            model.addAttribute("userName", upperCaseName);
		            return "startup/home";
		        } else if(user.getRole().equals("REVIEWER")){
		        	String upperCaseName = user.getName().toUpperCase();
		            model.addAttribute("userName", upperCaseName);
		            return "reviewer/home";
		        } else if(user.getRole().equals("INVESTOR")) {
		        	String upperCaseName = user.getName().toUpperCase();
		            model.addAttribute("userName", upperCaseName);
		            return "investor/home";
		        }else {
		        	String upperCaseName = user.getName().toUpperCase();
		            model.addAttribute("userName", upperCaseName);
		            return "mentor/home";
		        }
		    }
		return "index";
	}
	
	@PostMapping("/submit-doubts")
	public String submitDoubts(@RequestParam("email") String email, @RequestParam("query") String query) {
		emailService.sendQueryMail(email,query);
		return "redirect:/";
	}
	
}
