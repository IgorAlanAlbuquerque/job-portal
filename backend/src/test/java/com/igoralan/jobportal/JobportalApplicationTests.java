package com.igoralan.jobportal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Tag;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class JobportalApplicationTests {

	@Test
	void contextLoads() {
	}

}
