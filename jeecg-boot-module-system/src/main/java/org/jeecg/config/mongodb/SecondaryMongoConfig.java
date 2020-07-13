package org.jeecg.config.mongodb;

import com.mongodb.MongoClient;
import org.jeecg.config.converter.BigDecimalToDecimal128Converter;
import org.jeecg.config.converter.Decimal128ToBigDecimalConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据配置连接mongoDB副库
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.secondary")
public class SecondaryMongoConfig extends  AbstractMongoConfig{


    @Override
//    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate getMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
