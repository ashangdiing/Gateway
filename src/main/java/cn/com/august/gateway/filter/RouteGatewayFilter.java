package cn.com.august.gateway.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer.Element;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class RouteGatewayFilter implements GlobalFilter,Ordered{
	private static final String SERVER_AUTHORIZE_TOKEN = "Server_Authorization";
	private static final String URI_BAIDU = "https://www.tmall.com/?ali_trackid=2:mm_26632258_3504122_55934697:1560912849_201_1541990482&clk1=8cfe73fce9469140f81ce241c7311d25&upsid=8cfe73fce9469140f81ce241c7311d25";
	private static final String URI_TIANMAO = "https://www.baidu.com/s?wd=%E4%BA%AC%E4%B8%9C&rsv_spt=1&rsv_iqid=0xc77913f00010616f&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=2&rsv_sug1=1&rsv_sug7=100";
	private static final String URI_OTHER = "https://www.liangzl.com/get-article-detail-127242.html";
	private static final String URI_OTHER2 = "https://www.cnblogs.com/yinjihuan/p/10474768.html";
	
	private static final Map UriMap=new ConcurrentHashMap<String,URI>();
	
	static {
		try {
			UriMap.put("tianmao", new URI("https://www.tmall.com/?ali_trackid=2:mm_26632258_3504122_55934697:1560912849_201_1541990482&clk1=8cfe73fce9469140f81ce241c7311d25&upsid=8cfe73fce9469140f81ce241c7311d25"));
			UriMap.put("baidu", new URI("https://www.baidu.com/s?wd=%E4%BA%AC%E4%B8%9C&rsv_spt=1&rsv_iqid=0xc77913f00010616f&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=2&rsv_sug1=1&rsv_sug7=100"));
			UriMap.put("other", new URI("https://www.liangzl.com/get-article-detail-127242.html"));
			UriMap.put("other2", new URI("https://www.cnblogs.com/yinjihuan/p/10474768.html"));
			UriMap.put("other3", new URI("https://translate.google.cn/"));
		
		} catch (URISyntaxException e) {
			 log.error("URI:",e);
		}	
	}
	
	
	@Override
	public int getOrder() {
		/*该类可以到内存或者其他位置获取需要跳转的路径进行跳转
		 * LoadBalancerClientFilter是负责选择路由服务的负载过滤器，里面会通过loadBalancer去选择转发的服务，
		 * 然后传递到下面的路由NettyRoutingFilter过滤器去执行，那么我们就可以基于这个机制来实现。
		 * LoadBalancerClientFilter的order是10100，我们这边比它大1，这样就能在它执行完之后来替换要路由的地址了。
		 */
		//数字越低优先级越高
		return 10101;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange  exchange, GatewayFilterChain  chain) {
		  ServerHttpRequest request = exchange.getRequest();
	        HttpHeaders headers = request.getHeaders();
	        for (String headr : headers.get(SERVER_AUTHORIZE_TOKEN)) {
	        	log.warn(" 获取到headr {}",headr);
			}
	        
	        log.warn("uri :{}",request.getPath().toString());
	        for (Element element: request.getPath().elements()) {
	        	log.warn("uri element:{}",element.value());
			}
	        //  获取查询参数。?后的参数 。  例如与 ?forwardurl=11asda
	        if(request.getQueryParams().containsKey("forwardurl"))
	        {
	        	  String forwardurl=""; 
	        for (String temp: request.getQueryParams().get("forwardurl")) {
	        	 
	        	forwardurl=temp;
	        	log.warn("uri forwardurl:{}",temp);
	        }
	        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, UriMap.get(forwardurl));
			 log.warn("正在跳转............"+forwardurl);
	        
	        
	        }
	        
	        
	        
	        return chain.filter(exchange);
	}
}
