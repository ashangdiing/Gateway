package cn.com.august.gateway;



import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class GatewayRunner implements ApplicationRunner{
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info(" ApplicationRunner gatewayRunner start--aaaaa---------->");
	}
}
