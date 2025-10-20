package ru.avtoAra.AvtoSochi.users.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/home.html");
        registry.addViewController("/home").setViewName("forward:/home.html");
        registry.addViewController("/hello").setViewName("forward:/hello.html");
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/registration").setViewName("forward:/registration.html");
        registry.addViewController("/main").setViewName("forward:/main.html");
        registry.addViewController("/createProduct").setViewName("forward:/createProduct.html");
        registry.addViewController("/AllProducts").setViewName("forward:/AllProducts.html");
        registry.addViewController("/orders-admin").setViewName("forward:/orders-admin.html");
        registry.addViewController("/my-orders").setViewName("forward:/my-orders.html");
        registry.addViewController("/profile-edit").setViewName("forward:/profile-edit.html");
        registry.addViewController("/forgot-password").setViewName("forward:/forgot-password.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/templates/");
    }
}