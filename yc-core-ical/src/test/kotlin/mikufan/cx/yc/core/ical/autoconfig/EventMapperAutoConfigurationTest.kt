package mikufan.cx.yc.core.ical.autoconfig

import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.core.ical.component.AlarmMapper
import mikufan.cx.yc.core.ical.component.DateTimeFieldSetter
import mikufan.cx.yc.core.ical.component.EventMapper
import mikufan.cx.yc.core.ical.component.OtherFieldsSetter
import org.assertj.core.api.Assertions
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class EventMapperAutoConfigurationTest : ShouldSpec({
  val applicationContextRunner = ApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(EventMapperAutoConfiguration::class.java))
  should("auto config") {
    applicationContextRunner.run { ctx ->
      listOf(
        AlarmMapper::class.java,
        DateTimeFieldSetter::class.java,
        OtherFieldsSetter::class.java,
        EventMapper::class.java
      )
        .forEach { Assertions.assertThat(ctx).hasSingleBean(it) }
    }
  }
})
