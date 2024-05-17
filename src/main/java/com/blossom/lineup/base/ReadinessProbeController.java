package com.blossom.lineup.base;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadinessProbeController {

	@GetMapping("/")
	public String readinessProbeHealthCheck() {
		return "hello world!";
	}
}
