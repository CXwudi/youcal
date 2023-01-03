package mikufan.cx.yc.core.ical.model.exception

/**
 * @author CX无敌
 * 2023-01-02
 */
class MappingException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
  constructor(cause: Throwable) : super(cause)
}
