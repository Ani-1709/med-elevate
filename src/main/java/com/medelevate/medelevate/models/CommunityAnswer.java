package com.medelevate.medelevate.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "community_answers")
public class CommunityAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false,length = 1000)
    private String answer;
    
    @ManyToOne
    @JoinColumn(name = "forum_id", nullable = false)
    private CommunityForum forum; 

    @ManyToOne
    @JoinColumn(name = "answered_by", nullable = false)
    private User answeredBy; 

    @Column(nullable = false)
    private Date answeredOn;

    public CommunityAnswer() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CommunityForum getForum() {
		return forum;
	}

	public void setForum(CommunityForum forum) {
		this.forum = forum;
	}

	public User getAnsweredBy() {
		return answeredBy;
	}

	public void setAnsweredBy(User answeredBy) {
		this.answeredBy = answeredBy;
	}

	public Date getAnsweredOn() {
		return answeredOn;
	}

	public void setAnsweredOn(Date answeredOn) {
		this.answeredOn = answeredOn;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
