package com.oshan.webfluxdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.oshan.webfluxdemo.service.ResourceLoaderService;

@SpringBootApplication
public class WebfluxDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxDemoApplication.class, args);
		//service\ResourceLoaderService
		/*
		ApplicationContext ctx = new AnnotationConfigApplicationContext("com.oshan.webfluxdemo.service");
		ResourceLoaderService loader = (ResourceLoaderService) ctx.getBean("resourceLoaderService");
		try {
		   loader.showResourceDataUsingRoot();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("** Resource loader using file path **");

		try {
			loader.showResourceDataUsingFilePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("** Resource loader using class path **");

		try {
			loader.showResourceDataUsingClassPath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("** Resource loader using URL **");

		try {
			loader.showResourceDataUsingURL();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	
	
	
	}


