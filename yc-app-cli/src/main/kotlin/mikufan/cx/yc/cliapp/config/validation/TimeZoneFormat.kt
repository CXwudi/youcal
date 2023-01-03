package mikufan.cx.yc.cliapp.config.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.time.ZoneId
import kotlin.reflect.KClass

/**
 * From https://gist.github.com/jorge-aranda/79af3add5ce3205f1fcbc23e29a15c32
 * @author CX无敌
 * 2023-01-02
 */
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.FIELD,
  AnnotationTarget.ANNOTATION_CLASS
)
@Retention(
  AnnotationRetention.RUNTIME
)
@Constraint(validatedBy = [ValidTimeZoneValidator::class])
annotation class TimeZoneFormat(
  val message: String = "Invalid Time-zone ID",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

internal class ValidTimeZoneValidator : ConstraintValidator<TimeZoneFormat?, String?> {
  override fun isValid(
    value: String?,
    context: ConstraintValidatorContext,
  ): Boolean {
    val result = value.isNullOrBlank() || ZoneId.getAvailableZoneIds().contains(value)
    if (!result) {
      context.apply {
        disableDefaultConstraintViolation()
        buildConstraintViolationWithTemplate("Invalid Time-zone ID: $value")
          .addConstraintViolation()
      }
    }
    return result
  }
}
