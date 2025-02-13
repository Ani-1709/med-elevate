package com.medelevate.medelevate.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medelevate.medelevate.dto.CommunityAnswerDTO;
import com.medelevate.medelevate.dto.CommunityQuestionDTO;
import com.medelevate.medelevate.models.CommunityAnswer;
import com.medelevate.medelevate.models.CommunityForum;
import com.medelevate.medelevate.models.User;
import com.medelevate.medelevate.repository.CommunityAnswerRepository;
import com.medelevate.medelevate.repository.CommunityForumRepository;

@Service
public class CommunityService {

	@Autowired
	private CommunityForumRepository communityForumRepository;
	@Autowired
	private CommunityAnswerRepository communityAnswerRepository;
	
	public CommunityForum postQuestion(User user,CommunityQuestionDTO communityQuestionDTO) {
		CommunityForum forum=new CommunityForum();
		forum.setQuery(communityQuestionDTO.getQuery());
		forum.setAskedBy(user);
		forum.setAskedOn(new Date());
		return communityForumRepository.save(forum);
	}
	
	public CommunityAnswer postAnswer(CommunityForum query, User user, CommunityAnswerDTO communityAnswerDTO) {
		CommunityAnswer answer=new CommunityAnswer();
		answer.setAnswer(communityAnswerDTO.getAnswer());
		answer.setAnsweredBy(user);
		answer.setAnsweredOn(new Date());
		answer.setForum(query);
		CommunityAnswer newAnswer=communityAnswerRepository.save(answer);
		if (query.getAnswers() == null) {
			query.setAnswers(new ArrayList<>());
		}
		query.getAnswers().add(newAnswer);
		communityForumRepository.save(query);
		return newAnswer;
	}
}