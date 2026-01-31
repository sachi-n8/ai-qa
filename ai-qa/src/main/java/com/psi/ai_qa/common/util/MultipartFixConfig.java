package com.psi.ai_qa.common.util;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;

@Configuration
public class MultipartFixConfig {

    @Bean
    public FilterRegistrationBean<FormContentFilter> disableFormContentFilter() {
        FilterRegistrationBean<FormContentFilter> filter =
                new FilterRegistrationBean<>();
        filter.setFilter(new FormContentFilter());
        filter.setEnabled(false);
        return filter;
    }
}
