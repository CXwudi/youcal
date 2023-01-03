package mikufan.cx.yc.core.ical.model.exception

/**
 * This exception is thrown when it is from the YouTrack side that the returned json is unmappable.
 *
 * App should give up mapping the issue addressing when this exception is thrown
 * @author CX无敌
 * 2023-01-02
 */
class MappingException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
  constructor(cause: Throwable) : super(cause)
}
