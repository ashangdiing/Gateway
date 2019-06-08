package cn.com.august.gateway.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DynamicRouteService implements ApplicationEventPublisherAware{
	@Resource
    private RouteDefinitionWriter routeDefinitionWriter;
	private ApplicationEventPublisher publisher;
	 @Autowired
	 RouteDefinitionLocator routeDefinitionLocator;
	 
	 
	 @Autowired
	 ObjectMapper mapper;
	 
    private void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    private final Map<String, RouteDefinition> routes = new  ConcurrentHashMap<String, RouteDefinition>();
	 @Resource
	 private StringRedisTemplate stringRedisTemplate;
	 
	 public static final String  GATEWAY_ROUTES = "geteway:routes";
    
    
    
 
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		 this.publisher = applicationEventPublisher;
	}
	public Flux<RouteDefinition> getAllRoute() {
		 
		return routeDefinitionLocator.getRouteDefinitions();
	}
	 

	 
    /**
     * 增加路由。经过测试能够成功。
     *
     */
    public String add(RouteDefinition definition) {
    	try {
       routes.put(definition.getId(), definition);
    		
       log.debug("《DynamicRouteServiceccc——————————g  {}",routes.toString());	
       Map<String, String> routesString = new  ConcurrentHashMap<String, String>();
       for (RouteDefinition rdtemp : routes.values()) {
    	   routesString.put(rdtemp.getId(), mapper.writeValueAsString(rdtemp));
	}
       
       stringRedisTemplate.opsForHash().putAll(GATEWAY_ROUTES, routesString);
    	log.debug("》DynamicRouteServiceaaa——————《 \r\n {}",mapper.writeValueAsString(routesString));	
    	
       
    	
			/*
			 * Map<String, RouteDefinition> routes2 = new ConcurrentHashMap<String,
			 * RouteDefinition>();
			 * 
			 * stringRedisTemplate.opsForHash().values(GATEWAY_ROUTES).stream().forEach(
			 * routeDefinition -> { try { RouteDefinition
			 * temp=mapper.readValue(routeDefinition.toString(),RouteDefinition.class );
			 * routes2.put(temp.getId(), temp); } catch (IOException e) {
			 * log.debug("routeDefinition readValue error: {}  ",routeDefinition.toString(),
			 * e); } });
			 */
    	
    	
    	
       
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
        }catch (Exception e) {
        	log.debug("add route fail id: {} ",definition.getId(),e);
        	return "add route  fail:"+ definition.getId();
		}
        return "success:"+ definition.getId();
    }
 
 
    /**
     * 更新路由
     */
    public String update(RouteDefinition definition) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail,not find route  routeId: " + definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            notifyChanged();
            return "success:"+ definition.getId();
        } catch (Exception e) {
        	log.debug("update route fail id: {} ",definition.getId(),e);
            return "update route  fail:"+ definition.getId();
        }
 
 
    }
 
    /**
     * 删除路由
     *
     */
 
    public String delete(String id) {
    	try {
    		this.routeDefinitionWriter.delete(Mono.just(id));
    		
    	 	notifyChanged();
    		return "delete success:"+id;
    	} catch (Exception e) {
    		log.debug("delete route fail id: {} ",id,e);
    		return "delete fail:"+id+" ecception:"+e.toString();
    	}
    	
    }
    public String delete1(String id) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(id));
             
            notifyChanged();
            return "delete success:"+id;
        } catch (Exception e) {
           log.debug("delete route fail id: {} ",id,e);
            return "delete fail:"+id+" ecception:"+e.toString();
        }
 
    }

 
	
	
}
