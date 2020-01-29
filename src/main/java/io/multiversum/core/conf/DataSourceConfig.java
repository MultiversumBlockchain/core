package io.multiversum.core.conf;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.multiversum.core.services.SettingsService;

@Configuration
public class DataSourceConfig {

	@Autowired
	private SettingsService settingsService;

	@Bean()
	public DataSource dataSource() {
		return DataSourceBuilder.create().username("sa").password("")
				.url(String.format("jdbc:h2:file:%s/%s", settingsService.getHome(), "mtv"))
				.driverClassName("org.h2.Driver")

				.build();
	}
}
