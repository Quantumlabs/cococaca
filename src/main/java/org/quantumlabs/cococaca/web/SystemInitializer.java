package org.quantumlabs.cococaca.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quantumlabs.cococaca.backend.BackEnd;
import org.quantumlabs.cococaca.backend.service.preference.Config;
import org.quantumlabs.cococaca.backend.service.preference.PreferenceConfig;
import org.quantumlabs.cococaca.backend.service.preference.ResourceUtil;

public class SystemInitializer implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ResourceUtil.setSystemClassLoader(event.getServletContext().getClassLoader());
		BackEnd.main(null);
	}
}
