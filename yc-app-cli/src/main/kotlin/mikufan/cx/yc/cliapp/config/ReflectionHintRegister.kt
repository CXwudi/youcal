package mikufan.cx.yc.cliapp.config

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

/**
 * @author CX无敌
 * 2023-03-13
 */
@Configuration
@RegisterReflectionForBinding(
  AlarmConfigRaw::class,
  AuthConfigRaw::class,
  DateTimeConfigRaw::class,
  IOConfig::class,
  OtherMappingConfigRaw::class,
    StringMappingRaw::class,
  SearchConfig::class,
)
class ReflectionHintRegister
