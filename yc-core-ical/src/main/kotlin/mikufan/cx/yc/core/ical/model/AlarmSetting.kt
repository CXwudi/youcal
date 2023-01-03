package mikufan.cx.yc.core.ical.model

import java.time.Duration

/**
 * @author CX无敌
 * 2023-01-02
 */
sealed interface AlarmSetting {
  val shiftBasedOn: ShiftBasedOn
}

data class FixedDurationSetting(
  val duration: Duration,
  override val shiftBasedOn: ShiftBasedOn,
) : AlarmSetting

data class FromFieldSetting(
  val durationFieldName: String,
  override val shiftBasedOn: ShiftBasedOn = ShiftBasedOn.BEFORE_START,
) : AlarmSetting

enum class ShiftBasedOn {
  BEFORE_START,
  AFTER_START,
  BEFORE_END,
  AFTER_END,
}
