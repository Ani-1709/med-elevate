package com.medelevate.medelevate.models;

import java.util.Date;

import jakarta.persistence.*;

@Entity
public class MentorshipRequest {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name="startup_id",nullable=false)
	private Startup startup;
	@Column(nullable=false)
	private String topic;
	@Column(nullable=false)
	private String description;
	@Column(nullable=false)
	private Date requestDate;
	
	@Column(nullable = false)
    private String status = "Pending"; // "Pending", "Approved"
	@ManyToOne
    @JoinColumn(name = "assigned_mentor")
	private User mentorAssigned;
	@Column
	private String feedback;
	@Column
	private String meetingLink;
	
	public MentorshipRequest() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Startup getStartup() {
		return startup;
	}

	public void setStartup(Startup startup) {
		this.startup = startup;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getMentorAssigned() {
		return mentorAssigned;
	}

	public void setMentorAssigned(User mentorAssigned) {
		this.mentorAssigned = mentorAssigned;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getMeetingLink() {
		return meetingLink;
	}

	public void setMeetingLink(String meetingLink) {
		this.meetingLink = meetingLink;
	}
	
	
}
