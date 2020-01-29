package io.multiversum.core.services;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

	@Value("#{'${wallet.home:${node.home:${HOME}/.multiversum/}}'}")
	private String home;

	private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

//	@Value("${env}")
//	private String env;
	
	@PostConstruct
	public void createHome() {
		File configDir = new File(home);
		
		if (! configDir.exists()) {
			log.info("Creating multiversum home dir");
			configDir.mkdir();
		}
	}
	
	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

}
