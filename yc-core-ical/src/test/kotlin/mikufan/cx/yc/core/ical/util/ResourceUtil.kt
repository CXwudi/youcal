package mikufan.cx.yc.core.ical.util

import org.springframework.util.ResourceUtils

/**
 * @author CX无敌
 * 2023-01-03
 */

fun loadResourceAsString(resourceName: String): String {
  val stream = ResourceUtils::class.java.classLoader.getResourceAsStream(resourceName)
  if (stream != null) {
    return stream.bufferedReader().use { it.readText() }
  } else {
    throw IllegalArgumentException("resource not found: $resourceName")
  }
}
