package com.yuyue.app.annotation;


import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
        import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
        import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
        import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;

/**
 * Create by IntelliJ IDEA
 *访问接口的url属性的数据中若存在<>[\]^`{|} 特殊字符，使用该配置
 * @author chenlei
 * @dateTime 2019/5/23 18:09
 * @description TomcatConfig
 */
@Configuration
public class TomcatConfig {

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new MyCustomizer();
    }

    private static class MyCustomizer implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer factory) {
            if (factory instanceof TomcatEmbeddedServletContainerFactory) {
                customizeTomcat((TomcatEmbeddedServletContainerFactory) factory);
            }
        }

        void customizeTomcat(TomcatEmbeddedServletContainerFactory factory) {
            factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
                connector.setAttribute("relaxedPathChars", "<>[\\]^`{|}");
                connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}");
            });
        }

    }
}
