package org.divsgaur.goodload.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HttpConfiguration {
    private static final String THREAD_SCOPE = "threadScope";

    /*
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

     */
}
