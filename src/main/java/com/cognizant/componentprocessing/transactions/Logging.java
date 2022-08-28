package com.cognizant.componentprocessing.transactions;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cognizant.componentprocessing.model.Users;

@Aspect
@Component
public class Logging {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Pointcut("within(@org.springframework.stereotype.Repository *)"
			+ " || within(@org.springframework.stereotype.Service *)"
			+ " || within(@org.springframework.web.bind.annotation.RestController *)")
	public void springPointCut() {
		// Point cut for Spring
	}

	@Pointcut("within(com.cognizant.componentprocessing.service..*)"
			+ " || within(com.cognizant.componentprocessing.controller..*)"
			+ " || within(com.cognizant.componentprocessing..*)")
	public void applicationPointcut() {
		// Point cut for applications
	}

	@Pointcut("within(com.cognizant.componentprocessing.repository.UserRepository)")
	public void userLog() {

	}

	@AfterThrowing(pointcut = "springPointCut() && applicationPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
	}

	@After("springPointCut() && applicationPointcut()")
	public void logAfterCalling(JoinPoint joinPoint) {
		log.info("Called Application for " + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName());
	}
	
	@AfterReturning(pointcut = "springPointCut() && applicationPointcut() && userLog()", returning = "retVal")
	public void logForUser(Object retVal) {
		System.out.println((((Users)retVal)).toString());
		
		
	}

}
