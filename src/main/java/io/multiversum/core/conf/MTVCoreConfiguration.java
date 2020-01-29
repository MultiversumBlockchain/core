package io.multiversum.core.conf;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//@EnableMapRepositories(basePackages = { "io.multiversum.core.repositories" })
@EnableJpaRepositories(basePackages = { "io.multiversum.core.repositories" })
@ComponentScan(basePackages = { "io.multiversum.core.services" })
@EntityScan(basePackages = { "io.multiversum.core.entities" })

public class MTVCoreConfiguration {
	public MTVCoreConfiguration() {
		
	}

//	@Bean
//	public DozerBeanMapperFactoryBean dozerMapper(ResourcePatternResolver resourcePatternResolver) throws IOException {
//		DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
//		factoryBean.setMappingFiles(resourcePatternResolver.getResources("classpath*:/*mapping.xml"));
//		
//		return factoryBean;
//	}
}
