package ru.mudan.config;

import net.bull.javamelody.MonitoringFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс с конфигурацией для Java Melody
 */
@Configuration
public class JavaMelodyConfig {

    @Bean
    public FilterRegistrationBean<MonitoringFilter> monitoringFilter() {
        FilterRegistrationBean<MonitoringFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MonitoringFilter());
        registrationBean.addUrlPatterns("/monitoring/*");
        return registrationBean;
    }
}
