package com.vibenotes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
	@UniqueConstraint(columnNames = "username")
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 3, max = 50)
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank
	@Size(min = 6)
	@Column(nullable = false)
	private String password;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	public User() {
	}

	public User(Long id, String username, String password, LocalDateTime createdAt) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

}

