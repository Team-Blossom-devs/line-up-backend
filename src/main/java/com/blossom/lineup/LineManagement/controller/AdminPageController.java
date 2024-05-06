package com.blossom.lineup.LineManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminPageController {

	@GetMapping("/admin/entrance-process/{waitingId}")
	public String entranceProcess(@PathVariable("waitingId") Long id, Model model) {
		model.addAttribute("id", id);
		return "admin/entrance-info";
	}
}
