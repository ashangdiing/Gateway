package cn.com.august.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class AuthorizeGatewayFilter implements GlobalFilter,Ordered{
	 private static final String AUTHORIZE_TOKEN = "Authorization";
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange  exchange, GatewayFilterChain  chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(AUTHORIZE_TOKEN);
        if (token == null) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        if(token != null)
        log.info("网关获取到的token，可以进行权限拦截 。AuthorizeGatewayFilter  token:"+token);
       

        ServerHttpResponse response = exchange.getResponse();
        
		/*
		 * if ( token==null) { response.setStatusCode(HttpStatus.UNAUTHORIZED); return
		 * response.setComplete(); }
		 * 
		 */
        

        return chain.filter(exchange);
	}

}
