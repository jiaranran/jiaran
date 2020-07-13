package org.jeecg.config.mongodb;

import com.mongodb.MongoClient;
import org.jeecg.config.converter.BigDecimalToDecimal128Converter;
import org.jeecg.config.converter.Decimal128ToBigDecimalConverter;
import org.jeecg.modules.util.ResouceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据配置连接主库
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.primary")
public class PrimaryMongoConfig extends AbstractMongoConfig  {

    @Primary
    @Override
//    @Bean(name = "primaryMongoTemplate")
    public MongoTemplate getMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
