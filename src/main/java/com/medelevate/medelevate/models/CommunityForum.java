package com.medelevate.medelevate.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "community_forum")
public class CommunityForum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String query;

    @ManyToOne
    @JoinColumn(name = "asked_by", nullable = false)
    private User askedBy; 

    @Column(nullable = false)
    private Date askedOn;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityAnswer> answers; 

    public CommunityForum() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public User getAskedBy() {
		return askedBy;
	}

	public void setAskedBy(User askedBy) {
		this.askedBy = askedBy;
	}

	public Date getAskedOn() {
		return askedOn;
	}

	public void setAskedOn(Date askedOn) {
		this.askedOn = askedOn;
	}

	public List<CommunityAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<CommunityAnswer> answers) {
		this.answers = answers;
	}
}
