package mikufan.cx.yc.cliapp

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import mikufan.cx.yc.apiclient.yt.util.SpringBootTestWithTestProfile
import mikufan.cx.yc.cliapp.util.EnabledByEnvExtension

/**
 * This must be run with custom YouTrack URI and bearer token, see [EnabledByEnvExtension]
 * @author CX无敌
 * 2023-01-01
 */
@SpringBootTestWithTestProfile(
  customProperties = [
    "cliapp.search.query=for: me State: Open,Reopened sort by: {Due Date} asc ",
    "cliapp.datetime.event-type=ONE_DAY_EVENT",
    "cliapp.datetime.field-names=Due Date",
    "cliapp.other-mapping.list[0].from-field-name=Assignee",
    "cliapp.other-mapping.list[0].default-value=Unassigned",
    "cliapp.other-mapping.list[0].to-field-name=ATTENDEE",
    "cliapp.other-mapping.list[1].from-field-name=State",
    "cliapp.other-mapping.list[1].default-value=Unresolved",
    "cliapp.other-mapping.list[1].to-field-name=STATUS",
    "cliapp.other-mapping.list[2].default-value=OPAQUE",
    "cliapp.other-mapping.list[2].to-field-name=TRANSP",
    "cliapp.other-mapping.list[3].from-field-name=Submodule",
    "cliapp.other-mapping.list[3].to-field-name=CATEGORIES",
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
