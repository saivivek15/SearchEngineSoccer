package org.soccer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HomeService {

	private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

	public String getDesc() {

		logger.debug("getDesc() is executed!");

		return "Hello World";

	}


}