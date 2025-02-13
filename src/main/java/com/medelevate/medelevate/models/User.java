package com.medelevate.medelevate.models;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	@Column(unique = true, nullable = false)
	public String email;
	@Column(nullable=false)
	public String name;
	@Column(nullable=false)
	public String password;
	@Column(nullable=false)
	public String role;
	@OneToOne(mappedBy = "founder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Startup startup;
	
	public User() {}

	public User(Long id, String email, String name, String password, String role) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	public Startup getStartup() {
		return startup;
	}

	public void setStartup(Startup startup) {
		this.startup = startup;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", name=" + name + ", password=" + password + ", role=" + role
				+ "]";
	}
	
}
