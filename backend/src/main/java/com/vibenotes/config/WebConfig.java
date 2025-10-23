package com.vibenotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload.avatar.dir}")
	private String avatarUploadDir;

	@Value("${file.upload.attachment.dir}")
	private String attachmentUploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path avatarPath = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
		String avatarPathString = avatarPath.toUri().toString();

		registry.addResourceHandler("/uploads/avatars/**")
				.addResourceLocations(avatarPathString + "/");
	}

}

