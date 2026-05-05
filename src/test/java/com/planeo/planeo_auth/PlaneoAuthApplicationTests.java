package com.planeo.planeo_auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class PlaneoAuthApplicationTests {
	private final PasswordEncoder passwordEncoder;

	public PlaneoAuthApplicationTests(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Test
	void contextLoads() {
		System.out.println(passwordEncoder.encode("password"));
	}



}
