package cn.com.august.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class Beans {

	 @Bean
	    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		 return builder.routes()
				 .build();
	    
	 }
	 
	 @Bean KeyResolver userKeyResolver() {
		 log.debug("------------------------------------------>userKeyResolver  建议取cooike值进行限流，避免攻击");
		 return exchange ->
		 Mono.just(exchange.getRequest().getQueryParams().getFirst("userName")); 
		 }
	 
	/**
	 *  
		 
	 @Bean
	 public KeyResolver ipKeyResolver() {
		 log.debug("------------------------------------------>ipKeyResolver");
		 return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
	 }
	
	  
	  
	  @Bean KeyResolver apiKeyResolver() {
	  log.debug("------------------------------------------>apiKeyResolver");
	  return exchange -> Mono.just(exchange.getRequest().getPath().value()); }
	 
	 
	 
	 * <strong>Title: <strong>
		* <p>Description: <p>
		* <p>Company: </p> 
		*<strong>Copyright:</strong> Copyright (c) 2019
		* @version: 1.0
		* @author  ASUS:augustWei
		* @date  2019 下午4:42:06
		* @return
	 */
	 
	 
	 
}
