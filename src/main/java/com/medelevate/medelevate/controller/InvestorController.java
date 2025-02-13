package com.medelevate.medelevate.controller;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.medelevate.medelevate.models.FundingRequest;
import com.medelevate.medelevate.models.InvestmentOffer;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.FundingRequestRepository;
import com.medelevate.medelevate.repository.InvestmentOfferRepository;
import com.medelevate.medelevate.repository.UserRepository;

@Controller
@RequestMapping("/investor")
public class InvestorController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FundingRequestRepository fundingRequestRepository;
	@Autowired
	private InvestmentOfferRepository investmentOfferRepository;
	
	@GetMapping("/funding-requests")
	public String accessFundingRequests(Model model, Authentication authentication) {
		UserDetails userDetails=(UserDetails)authentication.getPrincipal();
		User investor=userRepository.findByEmail(userDetails.getUsername());
		List<FundingRequest> needFundings=fundingRequestRepository.findAll();
		List<FundingRequest> availableFundings=new ArrayList<>();
		for(FundingRequest request : needFundings) {
			if(request.getStatus().equals("Fully funded")) {
				continue;
			}
			List<InvestmentOffer> offers = investmentOfferRepository.findByFundingRequestAndInvestor(request, investor);
	        if (offers.isEmpty()) {  
	            availableFundings.add(request);
	        }
		}
		if(availableFundings.isEmpty()) {
			model.addAttribute("noFundingsPresent", true);
			model.addAttribute("fundingsNeeded", false);
		}
		else {
			model.addAttribute("fundingsNeeded", true);
			model.addAttribute("fundingRequests", availableFundings);
		}
		return "investor/funding-requests";
	}

	@PostMapping("/approve/{fundingRequestId}")
	public String approveFundingRequest(@PathVariable Long fundingRequestId,
	                                    @RequestParam Double amountOffered,
	                                    @RequestParam String returnRequest,
	                                    Authentication authentication) {
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User investor = userRepository.findByEmail(userDetails.getUsername());

	    FundingRequest fundingRequest = fundingRequestRepository.findById(fundingRequestId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid funding request ID"));

	    
	    List<InvestmentOffer> existingOffers = investmentOfferRepository.findByFundingRequestAndInvestor(fundingRequest, investor);
	    if (!existingOffers.isEmpty()) {
	        return "redirect:/investor/funding-requests"; 
	    }

	    InvestmentOffer investmentOffer = new InvestmentOffer();
	    investmentOffer.setFundingRequest(fundingRequest);
	    investmentOffer.setInvestor(investor);
	    investmentOffer.setAmountOffered(amountOffered);
	    investmentOffer.setReturnRequest(returnRequest);
	    investmentOffer.setStatus("Pending"); 

	    investmentOfferRepository.save(investmentOffer);

	    return "redirect:/investor/funding-requests";
	}


	@PostMapping("/decline/{fundingRequestId}")
	public String declineFundingRequest(@PathVariable Long fundingRequestId,
										Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User investor = userRepository.findByEmail(userDetails.getUsername());

	    FundingRequest fundingRequest = fundingRequestRepository.findById(fundingRequestId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid funding request ID"));
	    List<InvestmentOffer> existingOffers = investmentOfferRepository.findByFundingRequestAndInvestor(fundingRequest, investor);
	    if (!existingOffers.isEmpty()) {
	        return "redirect:/investor/funding-requests"; 
	    }

	    InvestmentOffer investmentOffer = new InvestmentOffer();
	    investmentOffer.setFundingRequest(fundingRequest);
	    investmentOffer.setInvestor(investor);
	    investmentOffer.setAmountOffered(0.0);
	    investmentOffer.setReturnRequest("N/A");
	    investmentOffer.setStatus("Declined"); 

	    investmentOfferRepository.save(investmentOffer);

	    return "redirect:/investor/funding-requests";	
	 }
	
	@GetMapping("/dashboard")
	public String getDashboard(Model model, Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    User investor = userRepository.findByEmail(userDetails.getUsername());
	    List<InvestmentOffer> approvedOffers=investmentOfferRepository.findByInvestorAndStatus(investor, "Approved");
	    List<InvestmentOffer> pendingOffers=investmentOfferRepository.findByInvestorAndStatus(investor, "Pending");
	    model.addAttribute("pendingOffers", pendingOffers);
	    model.addAttribute("approvedOffers", approvedOffers);
	    return "investor/dashboard";
	}
}
