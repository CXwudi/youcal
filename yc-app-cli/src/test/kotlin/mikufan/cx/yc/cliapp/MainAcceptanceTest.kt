package mikufan.cx.yc.cliapp

import mikufan.cx.yc.apiclient.yt.util.SpringBootTestWithTestProfile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author CX无敌
 * 2023-01-01
 */
@SpringBootTestWithTestProfile(
  customProperties = [
    "cliapp.search.query=for: me State: Open,Reopened sort by: {Due Date} asc ",
    "cliapp.datetime.fields=Due Date",
    "cliapp.datetime.field-type=date"
  ]
)
@EnabledIfEnvironmentVariables(
  EnabledIfEnvironmentVariable(named = "CLIAPP_AUTH_BASE_URI", matches = ".*"),
  EnabledIfEnvironmentVariable(named = "CLIAPP_AUTH_BEARER_TOKEN", matches = ".*"),
)
class Acceptance1Test {

  @Autowired
  lateinit var main: Main

  @Test
  fun `should work`() {
    main.run()
  }
}
