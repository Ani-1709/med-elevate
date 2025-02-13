package com.medelevate.medelevate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import com.medelevate.medelevate.dto.ComplianceVerificationDTO;
import com.medelevate.medelevate.dto.FundingRequestDTO;
import com.medelevate.medelevate.dto.MentorshipRequestDTO;
import com.medelevate.medelevate.dto.StartupDTO;
import com.medelevate.medelevate.models.ComplianceVerification;
import com.medelevate.medelevate.models.FundingRequest;
import com.medelevate.medelevate.models.MentorshipRequest;
import com.medelevate.medelevate.models.Startup;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.ComplianceVerificationRepository;
import com.medelevate.medelevate.repository.FundingRequestRepository;
import com.medelevate.medelevate.repository.MentorshipRequestRepository;
import com.medelevate.medelevate.repository.StartupRepository;
import com.medelevate.medelevate.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class StartupService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	public StartupRepository startupRepository;
	@Autowired
	public MentorshipRequestRepository mentorshipRequestRepository;
	@Autowired
	public FundingRequestRepository fundingRequestRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ComplianceVerificationRepository complianceVerificationRepository;
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	public Startup saveStartupApplication(StartupDTO startupDto,User user) {
		Startup startup=new Startup();
		startup.setFounder(user);
		startup.setName(startupDto.getName());
		startup.setIndustry(startupDto.getIndustry());
		startup.setRegistrationNumber(startupDto.getRegistrationNumber());
		Long timestamp = System.currentTimeMillis();
		Date registrationDate = new Date(timestamp);
		startup.setRegistrationDate(registrationDate);
		startup.setAddress(startupDto.getAddress());
		startup.setContact(startupDto.getContact());
		startup.setCountry(startupDto.getCountry());
		startup.setDescription(startupDto.getDescription());
		Startup savedStartup = startupRepository.save(startup);
		user.setStartup(savedStartup);
		userRepository.save(user);
		emailService.sendSuccessfullStartupRegistrationMail(user);
		return savedStartup;
	}
	
	public MentorshipRequest sendMentorshipRequestApplication(MentorshipRequestDTO requestDTO,Startup  startup) {
		MentorshipRequest request=new MentorshipRequest();
		request.setStartup(startup);
		request.setTopic(requestDTO.getTopic());
		request.setDescription(requestDTO.getDescription());
		Long timestamp = System.currentTimeMillis();
		Date requestDate = new Date(timestamp);
		request.setRequestDate(requestDate);
		return mentorshipRequestRepository.save(request);
	}
	
	public FundingRequest sendFundingRequestApplication(Startup startup, FundingRequestDTO fundingRequestDTO) {
		FundingRequest request=new FundingRequest();
		request.setStartup(startup);
		request.setPurpose(fundingRequestDTO.getPurpose());
		request.setAmountRequested(fundingRequestDTO.getAmountRequested());
		request.setFundingStage(fundingRequestDTO.getFundingStage());
		request.setInvestorPreferences(fundingRequestDTO.getInvestorPreferences());
		request.setSubmissionDate(new Date());
		return fundingRequestRepository.save(request);
	}

	public ComplianceVerification saveComplianceVerification(Startup startup, MultipartFile file, String founderComments) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String documentUrl = "/uploads/" + fileName;
            
            ComplianceVerification compliance = new ComplianceVerification();
            compliance.setFilename(fileName);
            compliance.setDocumentUrl(documentUrl);  
            compliance.setStatus("Pending");
            compliance.setSubmittedDate(new Date());
            compliance.setStartup(startup);
            compliance.setFounderComments(founderComments);
            return complianceVerificationRepository.save(compliance);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + file.getOriginalFilename(), e);
        }
    }	
}
