package org.divsgaur.goodload.http;

import com.squareup.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.SimpleThreadScope;

@Configuration
@Slf4j
public class HttpConfiguration {
    private static final String THREAD_SCOPE = "threadScope";

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory ->
            beanFactory.registerScope("threadScope", new SimpleThreadScope());
    }

    @Bean
    @Scope(scopeName = THREAD_SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}
