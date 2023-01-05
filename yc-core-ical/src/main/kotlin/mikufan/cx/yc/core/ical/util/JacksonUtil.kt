package mikufan.cx.yc.core.ical.util

import com.fasterxml.jackson.databind.JsonNode
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * @author CX无敌
 * 2023-01-04
 */

/**
 * Check if the json node is not null and not a null json node
 * @receiver JsonNode?
 * @return Boolean true if the json node is not null
 */
@OptIn(ExperimentalContracts::class)
fun JsonNode?.isNotNull(): Boolean {
  contract {
    returns(false) implies (this@isNotNull == null)
  }
  return !isNull()
}

/**
 * Either the value itself is null or the json node is a null json node
 * @receiver JsonNode?
 * @return Boolean true if the json node is null
 */
@OptIn(ExperimentalContracts::class)
fun JsonNode?.isNull(): Boolean {
  contract {
    returns(false) implies (this@isNull != null)
  }
  return this == null || this.isNull
}
