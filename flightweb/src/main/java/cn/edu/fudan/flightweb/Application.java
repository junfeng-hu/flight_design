package cn.edu.fudan.flightweb;

/**
 * Created by junfeng on 7/30/15.
 */

import cn.edu.fudan.flightweb.interceptor.AuthenticateInterceptor;
import com.fasterxml.jackson.databind.MapperFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;


/**
 * make the web app runnable
 */
@SpringBootApplication
public class Application {
    private static final Logger logger =
            LoggerFactory.getLogger(Application.class);

    /**
     * custom ObjectMapper
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToEnable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS);
        return builder;
    }

    /**
     * custom WebMvcConfigurer
     * @return
     */
    @Bean
    protected WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                super.addInterceptors(registry);
                registry.addInterceptor(new AuthenticateInterceptor());
            }
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // ...
            }
        };
    }

    public static void main(String[] args) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();

        logger.info("start print classpath:");
        for(URL url: urls){
            logger.info(url.getFile());
        }
        logger.info("start print environment:");
        Map<String, String> environments = System.getenv();
        for (String k : environments.keySet()) {
            // print environment
            if (k.startsWith("META"))
                logger.info("{}: {}", k, environments.get(k));
        }
        SpringApplication.run(Application.class, args);
    }
}