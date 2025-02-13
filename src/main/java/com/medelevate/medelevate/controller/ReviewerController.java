package com.medelevate.medelevate.controller;

import java.net.MalformedURLException;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.medelevate.medelevate.dto.ReviewerCommentsDTO;
import com.medelevate.medelevate.models.ComplianceVerification;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.ComplianceVerificationRepository;
import com.medelevate.medelevate.repository.UserRepository;
import com.medelevate.medelevate.service.ReviewerService;

@Controller
@RequestMapping("/reviewer")
public class ReviewerController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ReviewerService reviewerService;
	@Autowired
	private ComplianceVerificationRepository complianceVerificationRepository;
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@GetMapping("/dashboard")
	public String getComplianceVerificationDashboard(Model model, Authentication authentication) {
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User reviewer = userRepository.findByEmail(userDetails.getUsername());

	    List<ComplianceVerification> approvedRequests = complianceVerificationRepository.findByReviewedByAndStatus(reviewer, "Approved");
	    List<ComplianceVerification> rejectedRequests = complianceVerificationRepository.findByReviewedByAndStatus(reviewer, "Rejected");

	    model.addAttribute("approvedDocuments", approvedRequests);
	    model.addAttribute("rejectedDocuments", rejectedRequests);
	    return "reviewer/reviewer-dashboard";
	}
	@GetMapping("/compliance-verification-applications")
	public String getComplianceVerificationRequests(Model model) {
		List<ComplianceVerification> requests=complianceVerificationRepository.findByStatusOrderBySubmittedDateDesc("Pending");
		model.addAttribute("requests", requests);
		return "reviewer/view-compliance-verification-requests";
	}
	
	@GetMapping("/compliance-verification-application/{complianceId}")
	public String viewComplianceVerificationApplication(@PathVariable Long complianceId,
			Model model, Authentication authentication) {
		ComplianceVerification complianceVerification=complianceVerificationRepository.findById(complianceId)
				.orElseThrow(()->new IllegalArgumentException("Invalid compliance request"));
		
		model.addAttribute("complianceVerification", complianceVerification);
		model.addAttribute("approved", false);
		model.addAttribute("rejected", false);
		return "reviewer/view-compliance-verification";
	}
	
	@PostMapping("/approve/{complianceId}")
	public String approveComplianceVerificationApplication(@PathVariable Long complianceId,@RequestParam("reviewerComments") String reviewerComments,
			Model model, Authentication authentication) {
		ComplianceVerification complianceVerification=complianceVerificationRepository.findById(complianceId)
				.orElseThrow(()->new IllegalArgumentException("Invalid compliance request"));
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User reviewer = userRepository.findByEmail(userDetails.getUsername());
		model.addAttribute("complianceVerification", complianceVerification);
		model.addAttribute("approved", true);
		model.addAttribute("rejected", false);		
		complianceVerification.setStatus("Approved");
		complianceVerification.setReviewedBy(reviewer);
		complianceVerification.setReviewerComments(reviewerComments);
		complianceVerificationRepository.save(complianceVerification);
		return "reviewer/home";
	}
	@PostMapping("/reject/{complianceId}")
	public String rejectComplianceVerificationApplication(@PathVariable Long complianceId,@RequestParam("reviewerComments") String reviewerComments,
			Model model, Authentication authentication) {
		ComplianceVerification complianceVerification=complianceVerificationRepository.findById(complianceId)
				.orElseThrow(()->new IllegalArgumentException("Invalid compliance request"));
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User reviewer = userRepository.findByEmail(userDetails.getUsername());
		model.addAttribute("complianceVerification", complianceVerification);
		model.addAttribute("approved", false);
		model.addAttribute("rejected", true);		
		complianceVerification.setStatus("Rejected");
		complianceVerification.setReviewedBy(reviewer);
		complianceVerification.setReviewerComments(reviewerComments);
		complianceVerificationRepository.save(complianceVerification);
		return "reviewer/home";
	}	
	@GetMapping("/{filename}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
	    try {
	        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
	        Resource resource = new UrlResource(filePath.toUri());

	        if (resource.exists() || resource.isReadable()) {
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_PDF)
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"") 
	                    .body(resource);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (MalformedURLException e) {
	        return ResponseEntity.badRequest().build();
	    }
	}
}
