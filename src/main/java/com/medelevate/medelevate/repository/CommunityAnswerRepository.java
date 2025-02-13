package com.medelevate.medelevate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medelevate.medelevate.models.CommunityAnswer;
import com.medelevate.medelevate.models.CommunityForum;

public interface CommunityAnswerRepository extends JpaRepository<CommunityAnswer, Long>{
	public List<CommunityAnswer> findByForum(CommunityForum forum);
}
