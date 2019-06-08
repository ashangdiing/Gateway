package cn.com.august.gateway.action;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.com.august.gateway.service.DynamicRouteService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/route")
@Slf4j
public class RouteController {
	
	/*
	 * 官方默认提供了这些接口进行网关的管理，例如获取所有的路由.管理端口上访问
	 *  GET http://ip:port/actuator/gateway/routes
	 
ID				HTTP Method				Description
globalfilters	GET						Displays the list of global filters applied to the routes.
routefilters	GET						Displays the list of GatewayFilter factories applied to a particular route.
refresh			POST					Clears the routes cache.
routes			GET						Displays the list of routes defined in the gateway.
routes/{id}		GET						Displays information about a particular route.
routes/{id}		POST					Add a new route to the gateway.
routes/{id}		DELETE					Remove an existing route from the gateway.


  配置文件，注册中心的路由配置不能 和自定义的 路由ID相同。通过actuator 监控管理接口访问指定路由ID信息时。会提示 Source emitted more than one item。有多条路由。

	 *
	 */	
	
	
	
	
	@Autowired
	DynamicRouteService dynamicRouteService;
	
	 @RequestMapping(value="/",method =RequestMethod.GET)
	 public Flux<RouteDefinition>  getRoutes(Principal principal) {
		 
		 log.debug("--------------aaaa--------------=---------------===------aa-->");
	 Flux<RouteDefinition> all= dynamicRouteService.getAllRoute();
	 
	  
	  
	 
	 
		  return all; 
	 } 
	 
	 @RequestMapping(value="/{id}",method =RequestMethod.GET)
	 public void getRoute(Principal principal,@PathVariable("id") String id){
		 
	 }
	 //添加
	 @PostMapping("/")
	 public String  addRoute(Principal principal,@RequestBody RouteDefinition routeDefinition) {
		 return  dynamicRouteService.add(routeDefinition);
	 } 
	 
	 //修改数据
	 @RequestMapping(value="/{id}",method =RequestMethod.PUT)
	 public String updateRoute(Principal principal,@PathVariable("id") String id,@RequestBody RouteDefinition routeDefinition){
		 return  dynamicRouteService.update(routeDefinition);
	 }
	 //删除数据
	 @RequestMapping(value="/{id}",method =RequestMethod.DELETE)
	 public String deleteRoute(Principal principal,@PathVariable("id") String id){
		return  dynamicRouteService.delete(id);
	 }
	 
	 
	 
	 
	 
	 
	 
}
