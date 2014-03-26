package com.smart.security;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;

public class SmartWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void init(ServletContext servletContext) {
        // 添加 EnvironmentLoaderListener
        servletContext.addListener(EnvironmentLoaderListener.class);
        // 添加 ShiroFilter
        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("ShiroFilter", ShiroFilter.class);
        shiroFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}
