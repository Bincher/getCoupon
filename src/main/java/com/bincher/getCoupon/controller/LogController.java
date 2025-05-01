package com.bincher.getCoupon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LogController {
    @GetMapping("/")
	public void log(){
		log.info("info message"); // default
		log.warn("warn message");
		log.error("error message");
	}
}
