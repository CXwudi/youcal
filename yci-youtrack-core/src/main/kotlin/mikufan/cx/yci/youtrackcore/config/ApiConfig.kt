package mikufan.cx.yci.youtrackcore.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * @date 2022-10-09
 * @author CX无敌
 */
@Configuration
@ConditionalOnMissingBean(RestTemplate::class)
class ApiConfig {

  @Bean
  fun config(restTemplateBuilder: RestTemplateBuilder): RestTemplate = restTemplateBuilder.build()
}
