package project.Meta2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import project.Meta2.beans.RMIbean;

@SpringBootApplication
public class SpringApp
{
    @Bean
    public RMIbean myBean() {
        RMIbean myBean = new RMIbean();
        myBean.connectToRMIserver();
        return myBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
    }
}