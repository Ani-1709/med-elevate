package com.medelevate.medelevate.dto;

import java.util.Date;

public class MentorshipRequestDTO {

	private String topic;
	private String description;
	public MentorshipRequestDTO() {}
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
}
