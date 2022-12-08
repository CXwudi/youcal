package mikufan.cx.yc.apiclient.api.issues

/**
 * @author CX无敌
 * 2022-11-29
 */
// @OptIn(ExperimentalKotest::class)
// @SpringBootTest
// class IssuesApiTest(
//  apiServiceConfiguration: ApiServiceConfiguration,
// ) : ShouldSpec({
//
//  context("api client").config(enabledOrReasonIf = ENABLE_BY_TOKEN) {
//    val issuesApi = apiServiceConfiguration.issuesApi()
//    should("get issues list") {
//      val iterator = issuesApi.getIssuesLazily(
//        "project: {Toolbox App}",
//        listOf("idReadable", "summary", "description", "customFields(name,id,value(name,id))"),
//        listOf("OS", "Assignee", "State", "Priority", "Type", "Fix versions"),
//        5,
//        5
//      )
//      iterator.hasNext() shouldBe true
//      repeat(5) {
//        log.info { "issue = ${iterator.next()}" }
//      }
//    }
//  }
// }) {
//  @Bean
//  fun myAuthConfig(): YouTrackApiAuthInfo {
//    return YouTrackApiAuthInfo(YOUTRACK_TEST_URI, YOUTRACK_TEST_BEARER_TOKEN)
//  }
// }
//
// private val log = KInlineLogging.logger()
