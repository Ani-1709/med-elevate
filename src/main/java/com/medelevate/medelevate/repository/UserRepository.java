package com.medelevate.medelevate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medelevate.medelevate.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
	public User findByEmail(String email);
}
