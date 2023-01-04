package mikufan.cx.yc.cliapp.config.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import mikufan.cx.yc.cliapp.config.AlarmConfigRaw
import mikufan.cx.yc.cliapp.config.DateTimeConfig
import mikufan.cx.yc.core.ical.model.EventType
import mikufan.cx.yc.core.ical.model.ShiftBasedOn
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * @author CX无敌
 * 2023-01-04
 */
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.FIELD,
  AnnotationTarget.ANNOTATION_CLASS,
  AnnotationTarget.CLASS
)
@Retention(
  AnnotationRetention.RUNTIME
)
@Constraint(validatedBy = [AlarmConfigValidator::class])
annotation class IsValidAlarmConfig(
  val message: String = "Invalid Alarm Config",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

@Component
internal class AlarmConfigValidator(
  dateTimeConfig: DateTimeConfig,
) : ConstraintValidator<IsValidAlarmConfig, AlarmConfigRaw> {

  private val eventType = dateTimeConfig.eventType

  override fun isValid(config: AlarmConfigRaw, context: ConstraintValidatorContext): Boolean {
    if (!config.enabled) return true

    val validEndRelated = !(eventType == EventType.ONE_DAY_EVENT && config.shiftBasedOn == ShiftBasedOn.END)
    if (!validEndRelated) {
      context.disableDefaultConstraintViolation()
      context.buildConstraintViolationWithTemplate("End related alarm is not supported for one day event")
        .addPropertyNode("shiftBasedOn")
        .addConstraintViolation()
    }
    return validEndRelated
  }
}
