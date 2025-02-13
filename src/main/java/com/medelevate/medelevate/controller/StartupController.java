package com.medelevate.medelevate.controller;

import java.util.List;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medelevate.medelevate.dto.CommunityQuestionDTO;
import com.medelevate.medelevate.dto.ComplianceVerificationDTO;
import com.medelevate.medelevate.dto.FundingRequestDTO;
import com.medelevate.medelevate.dto.MentorshipRequestDTO;
import com.medelevate.medelevate.dto.StartupDTO;
import com.medelevate.medelevate.models.*;
import com.medelevate.medelevate.repository.CommunityForumRepository;
import com.medelevate.medelevate.repository.ComplianceVerificationRepository;
import com.medelevate.medelevate.repository.FundingRequestRepository;
import com.medelevate.medelevate.repository.InvestmentOfferRepository;
import com.medelevate.medelevate.repository.MentorshipRequestRepository;
import com.medelevate.medelevate.repository.StartupRepository;
import com.medelevate.medelevate.repository.UserRepository;
import com.medelevate.medelevate.service.CommunityService;
import com.medelevate.medelevate.service.EmailService;
import com.medelevate.medelevate.service.StartupService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/startup")
public class StartupController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MentorshipRequestRepository mentorshipRequestRepository;
	@Autowired
	private StartupRepository startupRepository;
	@Autowired
	private StartupService startupService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CommunityForumRepository communityForumRepository;
	@Autowired
	private CommunityService communityService;
	@Autowired
	private ComplianceVerificationRepository complianceVerificationRepository;
	@Autowired
	private FundingRequestRepository fundingRequestRepository;
	@Value("${file.upload-dir}")
	private String uploadDir;
	@Autowired
	private InvestmentOfferRepository investmentOfferRepository;
	
	@GetMapping("/dashboard")
	public String getStartupDashboard(Model model, Authentication authentication) {
		UserDetails userDetails=(UserDetails)authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		Startup startup=user.getStartup();
		List<MentorshipRequest> ongoingMentorships=mentorshipRequestRepository.findByStartupAndStatus(startup, "Approved");
		List<MentorshipRequest> unassignedMentorships=mentorshipRequestRepository.findByStartupAndStatus(startup, "Pending");
		model.addAttribute("ongoingMentorships", ongoingMentorships);
		model.addAttribute("unassignedMentorships", unassignedMentorships);
	    List<FundingRequest> pendingRequests=fundingRequestRepository.findByStartupAndStatus(startup, "Pending");
	    List<FundingRequest> partialRequests=fundingRequestRepository.findByStartupAndStatus(startup, "Partially Funded");
	    List<FundingRequest> fullRequests=fundingRequestRepository.findByStartupAndStatus(startup, "Fully Funded");
	    model.addAttribute("fullRequests", fullRequests);
	    model.addAttribute("partialRequests", partialRequests);
	    model.addAttribute("pendingRequests", pendingRequests);
	    List<ComplianceVerification> approvedComplianceRequests=complianceVerificationRepository.findByStartupAndStatus(startup,"Approved");
	    List<ComplianceVerification> rejectedComplianceRequests=complianceVerificationRepository.findByStartupAndStatus(startup,"Rejected");
	    List<ComplianceVerification> pendingComplianceRequests=complianceVerificationRepository.findByStartupAndStatus(startup,"Pending");
	    model.addAttribute("approvedComplianceRequests", approvedComplianceRequests);
	    model.addAttribute("rejectedComplianceRequests", rejectedComplianceRequests);
	    model.addAttribute("pendingComplianceRequests", pendingComplianceRequests);
	    return "startup/dashboard";
	}
	
	@GetMapping("/funding-request/{fundingRequestId}/offers")
	public String getInvestmentOffersPage(@PathVariable Long fundingRequestId,
			Authentication authentication, Model model) {
		UserDetails userDetails=(UserDetails)authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		FundingRequest request=fundingRequestRepository.findById(fundingRequestId)
				.orElseThrow(()->new IllegalArgumentException("Funding request invalid"));
		List<InvestmentOffer> pendingOffers=investmentOfferRepository.findByFundingRequestAndStatus(request,"Pending");
		List<InvestmentOffer> approvedOffers=investmentOfferRepository.findByFundingRequestAndStatus(request,"Approved");
		List<InvestmentOffer> declinedOffers=investmentOfferRepository.findByFundingRequestAndStatus(request,"Declined");
		model.addAttribute("pendingOffers",pendingOffers);
		model.addAttribute("approvedOffers", approvedOffers);
		model.addAttribute("declinedOffers", declinedOffers);
		return "startup/view-investment-offers";	
	}
	
	
	@PostMapping("/submit-application")
	public String submitStartupApplication(Model model, Authentication authentication) {
		model.addAttribute("submitted", true);
		return "startup/startup-application-form";
	}
	
	@GetMapping("/register-startup")
	public String getStartupRegistrationPage(Model model, Authentication authentication) {
		model.addAttribute("startupDTO", new StartupDTO());
		model.addAttribute("success", false);
		return "startup/startup-registration-form";
	}
	
	@PostMapping("/register-startup")
	public String postStartupRegistrationForm(Model model, @Valid @ModelAttribute StartupDTO 
			startupDTO, BindingResult result, Authentication authentication) {
		UserDetails userDetails=(UserDetails)authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		if (user.getStartup() != null) {
		    result.addError(new FieldError("startupDTO", "name", "You have already registered a startup."));
		    model.addAttribute("success", false);
		    model.addAttribute("startupDTO", new StartupDTO());
		    return "startup/startup-registration-form"; 
		}
		Startup startup=startupRepository.findByRegistrationNumber(startupDTO.getRegistrationNumber());
		if(startup!=null) {
			result.addError(new FieldError("startupDTO","registrationNumber","Startup with this registration number already exists"));
		    model.addAttribute("success", false);
		    model.addAttribute("startupDTO", new StartupDTO());
		    return "startup/startup-registration-form";
		}
		if (result.hasErrors()) {
		    model.addAttribute("success", false);
		    model.addAttribute("startupDTO", new StartupDTO());
		    return "startup/startup-registration-form";
		}

		startupService.saveStartupApplication(startupDTO,user);
		model.addAttribute("success", true);
		model.addAttribute("startupDTO", new StartupDTO());
		return "startup/startup-registration-form";
	}
	
	@GetMapping("/mentorship-request")
	public String getMentorshipRequestPage(Model model) {
		model.addAttribute("success",false);
		model.addAttribute("mentorshipRequestDTO", new MentorshipRequestDTO());
		return "startup/mentorship-request";
	}
	
	@PostMapping("/mentorship-request")
	public String submitMentorshipRequest(Model model, Authentication authentication,
			@Valid @ModelAttribute MentorshipRequestDTO mentorshipRequestDTO, BindingResult result) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		Startup startup=startupRepository.findByFounder(user);
		startupService.sendMentorshipRequestApplication(mentorshipRequestDTO, startup);
		model.addAttribute("success", true);
		model.addAttribute("mentorshipRequestDTO", new MentorshipRequestDTO());
		return "startup/mentorship-request";
	}
	
	@GetMapping("/funding-request")
	public String getFundingRequestPage(Model model) {
		model.addAttribute("success", false);
		model.addAttribute("fundingRequestDTO", new FundingRequestDTO());
		return "startup/funding-request";
	}
	
	@PostMapping("/funding-request")
	public String submitFundingRequest(@Valid @ModelAttribute FundingRequestDTO fundingRequestDTO
			, BindingResult result, Model model, Authentication authentication) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		Startup startup=user.getStartup();
		startupService.sendFundingRequestApplication(startup, fundingRequestDTO);
		model.addAttribute("success", true);
		model.addAttribute("fundingRequestDTO", new FundingRequestDTO());
		return "startup/funding-request";
	}
	
	@PostMapping("/mentor-feedback/{mentorshipRequestId}")
	public String submitMentorFeedback(@PathVariable Long mentorshipRequestId,
									   @RequestParam("feedback") String feedback,
									   Authentication authentication,
									   RedirectAttributes redirectAttributes) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		MentorshipRequest mentorshipRequest=mentorshipRequestRepository.findById(mentorshipRequestId)
								.orElseThrow(() -> new IllegalArgumentException("Invalid mentorship request ID"));
		if(!mentorshipRequest.getStartup().getFounder().equals(user)) {
			redirectAttributes.addFlashAttribute("error", "You are not authorized to give feedback.");
	        return "redirect:/startup/mentor-feedback";
		}
		mentorshipRequest.setFeedback(feedback);
		mentorshipRequestRepository.save(mentorshipRequest);
		redirectAttributes.addFlashAttribute("feedbackAdded", true);
		return "redirect:/startup/mentor-feedback";
	}
	
	@PostMapping("/submit-query")
	public String postQuery(@Valid @ModelAttribute CommunityQuestionDTO communityQuestionDTO
			,BindingResult result, Model model, Authentication authentication) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		communityService.postQuestion(user, communityQuestionDTO);
		model.addAttribute("success", true);
		model.addAttribute("communityQuestionDTO", new CommunityQuestionDTO());
		return "startup/submit-query";
	}
	
	@GetMapping("/community-forum")
	public String getCommunityForumPage(Model model) {
		List<CommunityForum> forum=communityForumRepository.findAllByOrderByAskedOnDesc();
		model.addAttribute("forum", forum);
		
		return "startup/community-forum";
	}
	
	@GetMapping("/submit-query")
	public String submitQuery(Model model) {
		model.addAttribute("communityQuestionDTO", new CommunityQuestionDTO());
		model.addAttribute("success", false);
		
		return "startup/submit-query";
	}
	
	@GetMapping("/mentorship-meeting-details/{mentorshipRequestId}")
	public String getMentorshipMeetingDetails(@PathVariable Long mentorshipRequestId,
			Model model, Authentication authentication) {
		MentorshipRequest mentorshipRequest=mentorshipRequestRepository.findById(mentorshipRequestId)
				.orElseThrow(()->new IllegalArgumentException("Invalid mentorship request"));
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User user=userRepository.findByEmail(userDetails.getUsername());
		if (!mentorshipRequest.getStartup().getFounder().equals(user)) {
		    throw new IllegalArgumentException("You do not have permission to view this meeting link.");
		}
		String meetingLink=mentorshipRequest.getMeetingLink();
		if(meetingLink==null) {
			model.addAttribute("meetingLinkPresent", false);
		}
		else {
			model.addAttribute("meetingLinkPresent", true);
			model.addAttribute("meetingLink", meetingLink);
		}
		return "startup/meeting-details";
	}
	@GetMapping("/submit-compliance-verification-form")
	public String getComplianceVerificationPage(Model model) {
		model.addAttribute("success", false);
		return "startup/submit-compliance-verification-form";
	}
	
	@PostMapping("/submit-compliance-verification-form")
	public String uploadComplianceFiles(@RequestParam("file") MultipartFile file, @RequestParam("founderComments") String founderComments,
			Authentication authentication, Model model) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User founder=userRepository.findByEmail(userDetails.getUsername());
		ComplianceVerification cv=startupService.saveComplianceVerification(founder.getStartup(),file,founderComments);
		model.addAttribute("success",true);
		return "startup/submit-compliance-verification-form";
	}
	@PostMapping("/terminate-mentorship/{id}")
	public String terminateMentorship(@PathVariable Long id,
			Authentication authentication) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User founder=userRepository.findByEmail(userDetails.getUsername());
		 
	    MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid mentorship request ID"));

	    if (!mentorshipRequest.getStartup().getFounder().equals(founder)) {
	        return "redirect:/startup/dashboard";
	    }
	    mentorshipRequest.setStatus("Completed"); 
	    mentorshipRequestRepository.save(mentorshipRequest);
	    return "redirect:/startup/dashboard";
	}
	
	@PostMapping("/approve-investment/{investmentOfferId}")
	public String approveInvestmentOffer(@PathVariable Long investmentOfferId, Authentication authentication
			) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User founder=userRepository.findByEmail(userDetails.getUsername());
		InvestmentOffer offer=investmentOfferRepository.findById(investmentOfferId)
				.orElseThrow(()->new IllegalArgumentException("Illegal investment request"));
		FundingRequest request=offer.getFundingRequest();
		if (!request.getStartup().getFounder().equals(founder)) {
	        throw new SecurityException("Unauthorized approval attempt");
	    }
		request.getInvestmentOffers().add(offer);
		offer.setStatus("Approved");
		request.setAmountFunded(request.getAmountFunded()+offer.getAmountOffered());
		if(request.getAmountFunded()==0) {
			request.setStatus("Pending");
		}
		else if(request.getAmountFunded()>=request.getAmountRequested()) {
			request.setStatus("Fully Funded");
		}
		else {
			request.setStatus("Partially Funded");
		}
		investmentOfferRepository.save(offer);
		fundingRequestRepository.save(request);
		return "redirect:/startup/dashboard";
	}
	
	@PostMapping("/decline-investment/{investmentOfferId}")
	public String declineInvestmentOffer(@PathVariable Long investmentOfferId, Authentication authentication
			) {
		UserDetails userDetails=(UserDetails) authentication.getPrincipal();
		User founder=userRepository.findByEmail(userDetails.getUsername());
		InvestmentOffer offer=investmentOfferRepository.findById(investmentOfferId)
				.orElseThrow(()->new IllegalArgumentException("Illegal investment request"));
		FundingRequest request=offer.getFundingRequest();
		if (!request.getStartup().getFounder().equals(founder)) {
	        throw new SecurityException("Unauthorized approval attempt");
	    }
		offer.setStatus("Declined");
		investmentOfferRepository.save(offer);
		return "redirect:/startup/dashboard";
	}
}
