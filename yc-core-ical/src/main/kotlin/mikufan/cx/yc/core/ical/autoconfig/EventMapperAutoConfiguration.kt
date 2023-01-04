package mikufan.cx.yc.core.ical.autoconfig

import mikufan.cx.yc.core.ical.component.AlarmMapper
import mikufan.cx.yc.core.ical.component.DateTimeFieldSetter
import mikufan.cx.yc.core.ical.component.EventMapper
import mikufan.cx.yc.core.ical.component.OtherFieldsSetter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean

/**
 * @author CX无敌
 * 2023-01-03
 */
@AutoConfiguration
class EventMapperAutoConfiguration {
  @Bean
  fun alarmMapper() = AlarmMapper()

  @Bean
  fun dateTimeFieldSetter() = DateTimeFieldSetter()

  @Bean
  fun otherFieldsSetter() = OtherFieldsSetter()

  @Bean
  fun eventMapper(
    dateTimeFieldSetter: DateTimeFieldSetter,
    alarmMapper: AlarmMapper,
    otherFieldsSetter: OtherFieldsSetter,
  ) = EventMapper(dateTimeFieldSetter, alarmMapper, otherFieldsSetter)
}
