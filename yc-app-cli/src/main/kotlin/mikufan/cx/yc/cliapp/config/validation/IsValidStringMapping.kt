package mikufan.cx.yc.cliapp.config.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import mikufan.cx.yc.cliapp.config.StringMappingRaw
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
@Constraint(validatedBy = [StringMappingValidator::class])
annotation class IsValidStringMapping(
  val message: String = "Invalid String Value Mapping from YouTrack field to iCal field",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

internal class StringMappingValidator : ConstraintValidator<IsValidStringMapping, StringMappingRaw> {
  override fun isValid(value: StringMappingRaw, context: ConstraintValidatorContext): Boolean {
    val hasAtLeastOne = !(value.fromFieldName.isBlank() && value.defaultValue.isBlank())
    if (!hasAtLeastOne) {
      context.disableDefaultConstraintViolation()
      val errMsg = "At least one of fromFieldName and defaultValue is required to be non-blank"
      context.buildConstraintViolationWithTemplate(errMsg)
        .addPropertyNode("fromFieldName")
        .addConstraintViolation()
      context.buildConstraintViolationWithTemplate(errMsg)
        .addPropertyNode("defaultValue")
        .addConstraintViolation()
    }
    return hasAtLeastOne
  }
}
