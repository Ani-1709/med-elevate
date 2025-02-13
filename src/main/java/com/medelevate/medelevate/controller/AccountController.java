package com.medelevate.medelevate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.medelevate.medelevate.dto.RegisterDto;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.UserRepository;
import com.medelevate.medelevate.service.EmailService;
import com.medelevate.medelevate.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AccountController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/register")
	public String getRegistrationPage(Model model, Authentication authentication) {
	    if (authentication != null) {
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        User user = userRepository.findByEmail(userDetails.getUsername());

	        if (user != null) {  
	            return redirectToRoleHome(user);
	        }
	    }

	    model.addAttribute("registerDto", new RegisterDto());
	    model.addAttribute("success", false);
	    return "register";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute RegisterDto registerDto, 
            Model model, BindingResult result) {
		int passwordLength=registerDto.getPassword().length();
		if(passwordLength<8) {
			result.addError(new FieldError("registerDto","password","Password should be of minimum 8 characters"));
		}
		int cntLetters=0,cntDigits=0,cntSpecial=0;
    	String password=registerDto.getPassword();
    	for(int i=0;i<passwordLength;i++) {
    		char c=password.charAt(i);
    		if(c>='0' && c<='9') {
    			cntDigits++;
    		}
    		else if(c>='A' && c<='Z'  || c>='a' && c<='z') {
    			cntLetters++;
    		}
    		else {
    			cntSpecial++;
    		}
    	}
    	if(cntSpecial==0 || cntDigits==0 || cntLetters==0) {
    		result.addError(new FieldError("registerDto","password","Password should be consist of special characters,letters and digits"));
    	}
    	if(!registerDto.getConfirmPassword().equals(password)) {
    		result.addError(new FieldError("registerDto","confirmPassword","Password and confirm password do not match"));
    	}
    	User existingUser = userRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            result.addError(new FieldError("registerDto", "email", 
                                           "This Email id is already being used"));
        }
        if (result.hasErrors()) {
            return "register";
        }
        String role=registerDto.getRole();
        switch(role) {
        case "STARTUP":
        	userService.saveUserStartup(registerDto);
        	break;
        case "REVIEWER":
        	userService.saveUserReviewer(registerDto);
        	break;
        case "MENTOR":
        	userService.saveUserMentor(registerDto);
        	break;
        case "INVESTOR":
        	userService.saveUserInvestor(registerDto);
        	break;
        }
        model.addAttribute("success", true);
        model.addAttribute("registerDto", new RegisterDto());
		return "register";
	}
	
	@GetMapping("/login")
	public String showLoginPage(Authentication authentication) {
	    if (authentication != null) {
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        User user = userRepository.findByEmail(userDetails.getUsername());

	        if(user!=null) {
	        	return redirectToRoleHome(user);
	        }
	    }
	    return "login";
	}

	private String redirectToRoleHome(User user) {
		switch(user.getRole()) {
		case "STARTUP":
			return "startup/home";
		case "REVIEWER":
			return "reviewer/home";
		case "MENTOR":
			return "mentor/home";
		case "INVESTOR":
			return "investor/home";
		}
		return null;
	}
}
