package cn.com.august.gateway.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class PreAuthorizeGatewayFilter implements GlobalFilter,Ordered{
	 private static final String AUTHORIZE_TOKEN = "Authorization";
	 private static final String SERVER_AUTHORIZE_TOKEN = "Server_Authorization";
	 
	 
	/*
	 * 可以在这个类里面进行一些处理，首先删除掉非法信息。比如与后台认证的头信息可能被外界模仿放在头，需要删掉，然后自己加上与后台交互的信息
	 *  
	 */
	@Override
	public int getOrder() {
		//数字越低优先级越高
		return -10;
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
        
        
        
        request.mutate()
        .header(SERVER_AUTHORIZE_TOKEN, "SERVER_AUTHORIZE_TOKEN---------------->1")
        .header(SERVER_AUTHORIZE_TOKEN, "SERVER_AUTHORIZE_TOKEN---------------->2")
        .header(SERVER_AUTHORIZE_TOKEN, "SERVER_AUTHORIZE_TOKEN---------------->3")
        .build();
      //将现在的request 变成 change对象 
        ServerWebExchange build = exchange.mutate().request(request).build();
        
		/*
		 *
		 
		 https://blog.csdn.net/zzhuan_1/article/details/85282783
		 spring-cloud-gateway反向代理的原理是，首先读取原请求的数据，然后构造一个新的请求，将原请求的数据封装到新的请求中，然后再转发出去。
		 然而我们在他封装之前读取了一次request body，而request body只能读取一次
		 
		 读取信息会导致报错
        ServerHttpResponse response = exchange.getResponse();
		 * Route route =
		 * exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR); URI
		 * uri = route.getUri();
		 */
        
        
		/*
		 * https://www.codercto.com/a/79061.html
		 * https://www.codercto.com/a/52970.html
		 * https://blog.csdn.net/fayeyiwang/article/details/86542375
		 * https://www.cnblogs.com/liukaifeng/p/10055862.html
		 修改目标下游地址或是修改请求返回信息参考
		 
		 RouteToRequestUrlFilter 
		 */
        
		/*
		 * if ( token==null) { response.setStatusCode(HttpStatus.UNAUTHORIZED); return
		 * response.setComplete(); }
		 * 
		 */
        
		/* ForwardRoutingFilter 
		 * 将uri转发到当前本地的url。
		 * 
		 * */
        
         
        return chain.filter(exchange);
	}

}
