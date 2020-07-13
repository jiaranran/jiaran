package org.jeecg;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.executor.handle.ExecutorConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableSwagger2
@EnableScheduling
@SpringBootApplication
@MapperScan("org.jeecg.config.rule.mapper.*")
public class JeecgApplication {

  public static void main(String[] args) throws UnknownHostException {
    ConfigurableApplicationContext application = SpringApplication.run(org.jeecg.JeecgApplication.class, args);
    Environment env = application.getEnvironment();
    String ip = InetAddress.getLocalHost().getHostAddress();
    String port = env.getProperty("server.port");
    String path = env.getProperty("server.servlet.context-path");
    log.info("\n----------------------------------------------------------\n\t" +
            "Application Jeecg-Boot is running! Access URLs:\n\t" +
            "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
            "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
            "swagger-ui: \thttp://" + ip + ":" + port + path + "/swagger-ui.html\n\t" +
            "Doc: \t\thttp://" + ip + ":" + port + path + "/doc.html\n" +
            "----------------------------------------------------------");
    ExecutorConfig executorConfig = SpringContextUtils.getBean(ExecutorConfig.class);
    ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) executorConfig.asyncServiceExecutor();
    if(args!=null&&args.length>=1){
      executor.setMaxPoolSize(Integer.parseInt(args[0]));
      executor.setCorePoolSize(Integer.parseInt(args[0]));
    }
  }
}