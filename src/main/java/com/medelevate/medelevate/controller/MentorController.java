package com.medelevate.medelevate.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medelevate.medelevate.dto.CommunityAnswerDTO;
import com.medelevate.medelevate.dto.MeetingLinkDTO;
import com.medelevate.medelevate.models.CommunityForum;
import com.medelevate.medelevate.models.MentorshipRequest;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.CommunityAnswerRepository;
import com.medelevate.medelevate.repository.CommunityForumRepository;
import com.medelevate.medelevate.repository.MentorshipRequestRepository;
import com.medelevate.medelevate.repository.UserRepository;
import com.medelevate.medelevate.service.CommunityService;
import com.medelevate.medelevate.service.EmailService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/mentor")
public class MentorController {

	@Autowired
	private MentorshipRequestRepository mentorshipRequestRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CommunityAnswerRepository communityAnswerRepository;
	@Autowired
	private CommunityForumRepository communityForumRepository;
	@Autowired
	private CommunityService communityService;
	
	
	@GetMapping("/mentorship-requests")
	public String accessMentorshipRequests(Model model) {
		List<MentorshipRequest> requests=mentorshipRequestRepository.findAll();
		List<MentorshipRequest> unattendedRequests=new ArrayList<>();
		for(MentorshipRequest request : requests) {
			if(request.getStatus().equals("Pending")) {
				unattendedRequests.add(request);
			}
		}
		if(unattendedRequests.size()==0) {
			model.addAttribute("noRequestsFound",true);
			model.addAttribute("requestsFound", false);
		}
		else {
			model.addAttribute("noRequestsFound",false);
			model.addAttribute("requestsFound", true);
			model.addAttribute("unattendedRequests",unattendedRequests);
		}
		return "mentor/mentorship-requests";
	}
	
	@PostMapping("/get-assigned-mentorship/{mentorshipRequestId}")
	public String getAssignedMentorship(@PathVariable Long mentorshipRequestId,
	                                    RedirectAttributes redirectAttributes,
	                                    Authentication authentication) {
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User mentor = userRepository.findByEmail(userDetails.getUsername());

	    MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(mentorshipRequestId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid mentorship request ID"));

	    mentorshipRequest.setStatus("Approved");
	    mentorshipRequest.setMentorAssigned(mentor);
	    mentorshipRequestRepository.save(mentorshipRequest);
	    redirectAttributes.addFlashAttribute("successfullAssigning", true);

	    return "redirect:/mentor/mentorship-requests";
	}

	@PostMapping("/remove-assigned-mentorship/{mentorshipRequestId}")
	public String removeAssignedMentorship(@PathVariable Long mentorshipRequestId,
	                                       Authentication authentication,
	                                       RedirectAttributes redirectAttributes) {

	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User mentor = userRepository.findByEmail(userDetails.getUsername());

	    MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(mentorshipRequestId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid mentorship request ID"));

	    if (!mentorshipRequest.getMentorAssigned().equals(mentor)) {
	        redirectAttributes.addFlashAttribute("error", "You are not authorized to remove this mentorship.");
	        return "redirect:/mentor/dashboard";
	    }
	    mentorshipRequest.setMentorAssigned(null);
	    mentorshipRequest.setStatus("Completed"); 
	    mentorshipRequestRepository.save(mentorshipRequest);

	    redirectAttributes.addFlashAttribute("mentorshipRemoved", true);
	    return "redirect:/mentor/dashboard";
	}
	
	@GetMapping("/mentorship-dashboard")
	public String accessAllCurrentMentorships(Model model, Authentication authentication) {
		
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User mentor = userRepository.findByEmail(userDetails.getUsername());
	    
	    
	    List<MentorshipRequest> assignedRequests = mentorshipRequestRepository.findByMentorAssigned(mentor);
	    
	    List<MentorshipRequest> incompleteAssignments = new ArrayList<>();
	    
	    List<MentorshipRequest> completeAssignments = new ArrayList<>();

	   
	    for (MentorshipRequest assignment : assignedRequests) {
	        if (assignment.getStatus().equals("Completed")) {
	            completeAssignments.add(assignment);
	        } else {
	            incompleteAssignments.add(assignment);
	        }
	    }
	    
	    model.addAttribute("completeAssignments", completeAssignments);
	    model.addAttribute("incompleteAssignments", incompleteAssignments);

	    return "mentor/mentorship-dashboard";
	}
	
	@GetMapping("/schedule-session/{mentorshipRequestId}")
	public String getScheduleSessionPage(@PathVariable Long mentorshipRequestId,Model model, Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User mentor = userRepository.findByEmail(userDetails.getUsername());
	    MentorshipRequest mentorshipRequest=mentorshipRequestRepository.findById(mentorshipRequestId)
	    		.orElseThrow(()->new IllegalArgumentException("Mentorship request doesn't exit"));
	    model.addAttribute("meetingLinkDTO", new MeetingLinkDTO());
	    model.addAttribute("successfullScheduling", false);
	    return "mentor/schedule-session";
	}
	
	@PostMapping("/schedule-session/{mentorshipRequestId}")
	public String scheduleSession(@PathVariable Long mentorshipRequestId,Model model, Authentication authentication
			,@Valid @ModelAttribute MeetingLinkDTO meetingLinkDTO) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User mentor = userRepository.findByEmail(userDetails.getUsername());
	    MentorshipRequest mentorshipRequest=mentorshipRequestRepository.findById(mentorshipRequestId)
	    		.orElseThrow(()->new IllegalArgumentException("Mentorship request doesn't exit"));
	    if(!mentorshipRequest.getMentorAssigned().equals(mentor)) {
	    	model.addAttribute("noMeeting", true);
	    	return "mentor/schedule-session";
	    }
	    mentorshipRequest.setMeetingLink(meetingLinkDTO.getMeetingLink());
	    mentorshipRequestRepository.save(mentorshipRequest);
	    emailService.sendMeetingLink(mentorshipRequest.getStartup().getFounder(), mentor, meetingLinkDTO.getMeetingLink());
	    //return "https://meet.google.com/new";
	    model.addAttribute("successfullScheduling", true);
	    model.addAttribute("meetingLinkDTO", new MeetingLinkDTO());
	    return "mentor/schedule-session";
	    
	}
	@PostMapping("/send-session-details")
	public String sendSessionDetails() {
		//To be done later
		return "";
	}
	
	@PostMapping("/post-community-answer/{forumId}")
	public String postCommunityAnswer(@PathVariable Long forumId,
									  @ModelAttribute CommunityAnswerDTO communityAnswerDTO,
									  Model model, Authentication authentication) {
		CommunityForum forum=communityForumRepository.findById(forumId)
				.orElseThrow(()->new IllegalArgumentException("Invalid forum"));
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User mentor = userRepository.findByEmail(userDetails.getUsername());
		communityService.postAnswer(forum,mentor,communityAnswerDTO);
		List<CommunityForum> forumList = communityForumRepository.findAllByOrderByAskedOnDesc();
	    model.addAttribute("forumList", forumList);
	    
		model.addAttribute("communityAnswerDTO", new CommunityAnswerDTO());
		return "redirect:/mentor/community-forum";
	}
	
	@GetMapping("/community-forum")
	public String getCommunityForumPage(Model model, Authentication authentication) {
		List<CommunityForum> forumList=communityForumRepository.findAllByOrderByAskedOnDesc();
		model.addAttribute("forumList", forumList);
		model.addAttribute("communityAnswerDTO", new CommunityAnswerDTO());
		return "mentor/community-forum";
	}
}
