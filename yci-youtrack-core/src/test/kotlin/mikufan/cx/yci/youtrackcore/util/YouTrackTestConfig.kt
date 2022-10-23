package mikufan.cx.yci.youtrackcore.util

import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import java.net.URI

/**
 * @date 2022-10-22
 * @author CX无敌
 */

val ENABLE_BY_TOKEN: EnabledOrReasonIf = {
  if (System.getenv("YOUTRACK_BEARER_TOKEN") != null) Enabled.enabled else Enabled.disabled("Skip tests that require YOUTRACK_BEARER_TOKEN")
}

val YOUTRACK_TEST_URI = URI("https://youtrack.jetbrains.com/api/")
val YOUTRACK_TEST_BEARER_TOKEN = System.getenv("YOUTRACK_BEARER_TOKEN") ?: ""
