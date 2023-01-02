package mikufan.cx.yc.cliapp.util

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

/**
 * @date 2022-10-09
 * @author CX无敌
 */
class KotestSpringConfig : AbstractProjectConfig() {
  override fun extensions() = listOf(SpringExtension)
}
