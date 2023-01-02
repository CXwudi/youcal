package mikufan.cx.yc.cliapp.config

import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

/**
 * @author CX无敌
 * 2023-01-01
 */
@ConfigurationProperties(prefix = "cliapp.io")
class IOConfig(
  @get:NotNull
  val outputFile: Path,
) {
  init {
    if (outputFile.parent.notExists()) {
      outputFile.parent.createDirectories()
    }
  }
}
