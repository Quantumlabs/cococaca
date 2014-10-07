package org.quantumlabs.cococaca.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quantumlabs.cococaca.backend.BackEnd;

public class SystemInitializer implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		BackEnd.main(null);
	}
}
