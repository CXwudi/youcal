package mikufan.cx.yc.cliapp.util

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * @author CX无敌
 * 2023-01-01
 */
object EnabledByEnvExtension : TestCaseExtension, SpecExtension {
  /**
   * Invoked to override if a test or spec is enabled or disabled.
   *
   * This method can choose to override that status by returning:
   *  - [Enabled.enabled] if this test or spec should be enabled
   *  - [Enabled.disabled] if this test or spec should be disabled
   */
  private fun isEnabled(): Enabled =
    if (System.getenv("CLIAPP_AUTH_BASE_URI") != null && System.getenv("CLIAPP_AUTH_BEARER_TOKEN") != null) {
      Enabled(true, "CLIAPP_AUTH_BASE_URI and CLIAPP_AUTH_BEARER_TOKEN are set")
    } else {
      Enabled(false, "CLIAPP_AUTH_BASE_URI and CLIAPP_AUTH_BEARER_TOKEN are not set")
    }

  /**
   * Intercepts a [TestCase], returning the result of the execution.
   *
   * Allows implementations to add logic around a [TestCase] as well as
   * control if (or when) the test is executed.
   *
   * The supplied `execute` function should be invoked if implementations wish to
   * execute the test case.
   *
   * This function accepts the test case instance to be executed, which is normally
   * the same instance supplied, but can be changed by this method.
   *
   * This allows you to change config for example:
   *
   * `execute(testCase.copy(config = ...))`
   *
   * Typically, the result recieved from invoking [execute] will be the result
   * returned, but this is not required. Implementations may wish to
   * skip the test case and return a result without executing the test, or they
   * may wish to inspect the test and return a different result to that received.
   *
   * Note: A test cannot be skipped after it has been executed. Executing a test
   * and then returning [TestStatus.Ignored] may result in an error.
   *
   * @param testCase the [TestCase] under interception
   *
   * @param execute a function that is invoked to execute the test. Can be ignored if you
   * wish to return a result without executing the test itself.
   */
  override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
    val enabled = isEnabled()
    return when {
      enabled.isEnabled -> execute(testCase)
      else -> TestResult.Ignored
    }
  }

  override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
    val enabled = isEnabled()
    if (enabled.isEnabled) {
      execute(spec)
    }
  }
}
