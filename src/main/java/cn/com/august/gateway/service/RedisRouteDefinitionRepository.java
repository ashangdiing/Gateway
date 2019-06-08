package cn.com.august.gateway.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class RedisRouteDefinitionRepository implements RouteDefinitionLocator {
	/**
	 *
	 不需要实现  RouteDefinitionRepository.实现了  RouteDefinitionWriter和RouteDefinitionLocator，经过测试动态路由关键点是
	 每次路径有变化时回去加载所有实现了RouteDefinitionLocator的getRouteDefinitions方法获取所有的路由，再进行匹配路径。
	 
	 做动态路由心得。
	 在启动的时候，spring gateway加载顺序
	 PropertiesRouteDefinitionLocator-->|配置文件加载初始化| CompositeRouteDefinitionLocator
	 RouteDefinitionRepository-->|存储器中加载初始化| CompositeRouteDefinitionLocator
	 DiscoveryClientRouteDefinitionLocator-->|注册中心加载初始化| CompositeRouteDefinitionLocator。
	 加载系统其他部分的。
	 
	 然后加载自定义实现的，也就是我这里的RedisRouteDefinitionRepository。
	 启动加载了系统配置文件，註冊中心，存储器之后，再加载自定义的，两部分合起来就是所有的路由。
	 几部分的路由ID一定不要一样，会出现访问时候随机走不同的路由，亲测。
	 
	 现在想做动态路由，根据网上资料，创建了DynamicRouteService  implements ApplicationEventPublisherAware在里面
	 自动注入了，RouteDefinitionLocator这个对象,它有添加，删除和查看的方法。经过测试，添加，查看没问题。不能重启，该方法应该是提供的
	 添加到内存或者注册中心位置里面去了。但是无法删除。进过测试发现通过管理口 也就是配置监控管理的actuator里面的东西其他API也没问题，删除直接提示404找不到页面。actuator使用方法官网上有。
	 做添加post的时候调整下json的数据类型提交上去即可。是可以成功添加，并通过几个接口查看到新加的路由记录，至于能否生效没进行测试。既然删除都有问题，动态路由肯定是没办法做的。
 
	 通过实现 DynamicRouteService  implements ApplicationEventPublisherAware方法的路堵死，然后又跟着资料实现 RouteDefinitionRepository类，做完了一大堆工作。
	 只要实现 getRouteDefinitions方法，就可以在启动的时候加载路由，直接配置到redis上即可。
	 开始以为还需要 DynamicRouteService  implements ApplicationEventPublisherAware里面的publisher.publishEvent(new RefreshRoutesEvent(this))方法在修改了配置路由生效，经过测试
	 发现，每次路由请求的路径有变化都会调用getRouteDefinitions来加载路由。饶了一大圈，动态路由的关键就是这个了。
	 所以动态路由很简单，实现这个方法就可以了。坑的我几天，还去学习了一下webfulx。
	 
	 再怎么设计是我们自己的事情了，可以做一个map缓存所有的路由，需要更新的时候去redis或者数据库里面加载新的路由即可或者提供rest接口管理。目前我的办法是，redis里面配置json，配置文件去除gateway的路由配置来实现动态路由。
	 亲测，删除redis里面的路由后无法打开指定页面，添加后马上能打开，修改跳转路径后会打开指定页面。都是即时生效无需重启服务。
	 
	 想修改配置文件，存储器，注册中心的路由不太可能，应该是需要修改RouteDefinitionLocator里面的getRouteDefinitions方法里面获取的数据。目前想到的只能这种不配置配置文件，存储器，注册中心的
	 办法，自己来实现这些路由。配置文件什么的可以做一些默认路由。
	 
	 spring gateway感觉不错，使用的是响应式编程。里面的filter可以修改头信息，加一些东西，然后进行转发。配置路由可以根据时间什么的进行转发，可以限制同时多少人访问，每个人一秒能访问多少次进行现在，唯一不能
	 限制流速，毕竟下载的时候需要限制下客户端的下载速度，这个应该是服务器级别的事情吧。
	 
	 疑问1:限速是根据请求信息的头或者其他关键字进行限制的，目前用的使用redis，gateway会是集群，如果配置的是n台gateway，每台gateway限制20个能访问。
	 猜想的:如果同一台redis下的同一个库，是不是可能出现整个集群总共只能处理20个请求，大于20的会被拒绝。目前打算集群的时候配置不同的redis库吧。虽然测试只在高速的时候才会看到redis里面存放了限制信息。需要后期测试才知道。
	 
	 
	 觉得不好的地方
	 1.流量限制返回的是http状态429。里面啥都没有，就说访问请求太多，需要前端ajax去判断进行处理，后端无法自定义输出，也没找到一些处理，因为有的业务场景可能需要对这些被拒绝的人进行回访或者说其他分析。
	 也只能通过ajax收到429发送给后端进行记录。希望后面能找打好的办法吧。
	 2.配置不是注册中心节点时不是很友好的感觉，跳转配置uri多条用逗号分开，请求一直被打倒第一个uri上，即时第一个不能使用。建议用lb访问注册中心的id吧。
	 
	 好的想法:
	 1.如果需要根据不同的请求信息跳转不同的地方，可以再过滤器里面使用resttemple什么的进行跳转吧。进行权限控制也可以在这里面做。暂时没计划去实现。
	 目前我项目spring security是另一个，已经完成了所有的自定义功能，前期没规划好，把管理页面集成进去了用的是spring web，与gateway现在无法直接集成，
	 因为spring gateway用的是webfulx，webflux导致很多东西都不能集成进来，比如hystrix-dashboard什么的。只能当做网关，所有的请求往后面打。在这里做一些
	 拦截上的事情，需要登录和鉴权的就打到spring security上，spring security的认证中心和鉴权中心需要调整调整。
	 
	 
	 *
	 */
	private final Map<String, RouteDefinition> routes = new  ConcurrentHashMap<String, RouteDefinition>();
	  
	@Autowired
	 private StringRedisTemplate stringRedisTemplate;
	 public static final String  GATEWAY_ROUTES = "geteway:routes";
	 @Autowired
	 ObjectMapper mapper;
	 
	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		/*
		 * List<RouteDefinition> routeDefinitions = new ArrayList<>();
		 * redisTemplate.opsForHash().values(GATEWAY_ROUTES).stream().forEach(
		 * routeDefinition -> {
		 * routeDefinitions.add(JSON.parseObject(routeDefinition.toString(),
		 * RouteDefinition.class)); });
		 */
		routes.clear();
		currentAllRoute();
        
        
        return Flux.fromIterable(routes.values());
        
	}

	public void currentAllRoute() {
	//	routes.put(definition.getId(), definition);
		
	       log.debug(" sss RedisRouteDefinitionRepository currentAllRoute 一共 {}",routes.size());	
		/*
		 * Map<String, String> routesString = new ConcurrentHashMap<String, String>();
		 * for (RouteDefinition rdtemp : routes.values()) { try {
		 * routesString.put(rdtemp.getId(), mapper.writeValueAsString(rdtemp)); } catch
		 * (JsonProcessingException e) { log.debug("writeValueAsString {}",e); } }
		 */
	      
	       
	       // 获取redis中的路由
	    //   Map<String, RouteDefinition> routes2 = new  ConcurrentHashMap<String, RouteDefinition>();
			  stringRedisTemplate.opsForHash().values(GATEWAY_ROUTES).stream().forEach(
			  routeDefinition -> {
				  try {
					  RouteDefinition temp=mapper.readValue(routeDefinition.toString(),RouteDefinition.class );
					  log.debug("redis 获取到了id:{}  详细:  {}",temp.getId(),routes.toString());	
					  routes.put(temp.getId(), temp);
				} catch (IOException e) {
					log.debug("routeDefinition readValue error: {}  ",routeDefinition.toString(),e);
				}
			     });
	       
	       
	       
	       
	       
	}
	
	
	 
	
	/**
	 * 这个方法没什么鸟用，留着吧
		* <strong>Title: <strong>
		* <p>Description: <p>
		* <p>Company: </p> 
		*<strong>Copyright:</strong> Copyright (c) 2019
		* @version: 1.0
		* @author  ASUS:augustWei
		* @date  2019 下午11:39:54
		* @param route
		* @return
	 */
	
	public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> {
                	String temp=null;
                	try {
						  temp=mapper.writeValueAsString(routeDefinition);
					} catch (JsonProcessingException e) {
						 log.debug(" add route error:{}", routeDefinition.getId());
					}
                	
                	stringRedisTemplate.opsForHash().put(GATEWAY_ROUTES, routeDefinition.getId(),
                			temp);
                    return Mono.empty();
                });
	
	}
/**
 * 这个方法没什么鸟用，留着吧
	* <strong>Title: <strong>
	* <p>Description: <p>
	* <p>Company: </p> 
	*<strong>Copyright:</strong> Copyright (c) 2019
	* @version: 1.0
	* @author  ASUS:augustWei
	* @date  2019 下午11:40:11
	* @param routeId
	* @return
 */
	public Mono<Void> delete(Mono<String> routeId) {
		 return routeId.flatMap(id -> {
	            if (stringRedisTemplate.opsForHash().hasKey(GATEWAY_ROUTES, id)) {
	            	stringRedisTemplate.opsForHash().delete(GATEWAY_ROUTES, id);
	                return Mono.empty();
	            }
	            return Mono.defer(() -> Mono.error( new NotFoundException("RouteDefinition not found: " + routeId)  ));
	        });
	
	}

}
