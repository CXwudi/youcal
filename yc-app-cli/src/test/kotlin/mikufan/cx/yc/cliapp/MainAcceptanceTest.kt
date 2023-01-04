package mikufan.cx.yc.cliapp

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.apiclient.yt.util.SpringBootTestWithTestProfile
import mikufan.cx.yc.cliapp.util.EnabledByEnvExtension

/**
 * @author CX无敌
 * 2023-01-01
 */
@SpringBootTestWithTestProfile(
  customProperties = [
    "cliapp.search.query=for: me State: Open,Reopened sort by: {Due Date} asc ",
    "cliapp.datetime.event-type=ONE_DAY_EVENT",
    "cliapp.datetime.field-names=Due Date",
  ]
)
class Acceptance1Test(
  private val main: Main,
) : ShouldSpec({
  should("run") {
    main.run()
  }
}) {
  override fun extensions(): List<Extension> = listOf(EnabledByEnvExtension)
}
