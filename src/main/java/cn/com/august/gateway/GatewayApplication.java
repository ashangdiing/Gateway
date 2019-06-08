package cn.com.august.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
@EnableDiscoveryClient
@SpringBootApplication
@EnableAutoConfiguration
@EnableCircuitBreaker //开启断路器功能
public class GatewayApplication {

	
	
	 
 
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(GatewayApplication.class, args);
		
	}

}
