package cn.com.august.gateway.action;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/gateway")
@Slf4j
public class GatewayController {
	 @RequestMapping("/timeout")
	    public String timeout() throws InterruptedException {
	        log.info("invoking timeout endpoint");
	        return "success:timeout";
	    }

	    @RequestMapping("/exception")
	    public String exception() {
	        log.info("invoking exception endpoint");
	        if (System.currentTimeMillis() % 2 == 0) {
	            throw new RuntimeException("random exception");
	        }
	        return "success:exception";
	    }
	    @RequestMapping("/error")
	    public String error() {
	    	log.info("invoking error endpoint");
	    	 
	    	return "success:error";
	    }
	    @RequestMapping("/request_rate_limiter")
	    public String RequestRateLimiter() {
	    	log.info("invoking request_rate_limiter");
	    	
	    	return "success:request_rate_limiter";
	    }
	 
}
