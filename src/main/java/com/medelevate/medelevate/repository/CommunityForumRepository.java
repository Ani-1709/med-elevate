package com.medelevate.medelevate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medelevate.medelevate.models.CommunityForum;
import com.medelevate.medelevate.models.User;

public interface CommunityForumRepository extends JpaRepository<CommunityForum, Long>{
	public List<CommunityForum> findByAskedBy(User askedBy);
	List<CommunityForum> findAllByOrderByAskedOnDesc();
}
