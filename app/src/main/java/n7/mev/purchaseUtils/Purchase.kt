/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package n7.mev.purchaseUtils

import org.json.JSONObject

/**
 * Represents an in-app billing purchase.
 */
class Purchase(// ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
        val itemType: String?, val originalJson: String, signature: String) {
    val orderId: String
    val packageName: String
    val sku: String
    val purchaseTime: Long
    val purchaseState: Int
    val developerPayload: String
    val token: String
    val signature: String
    val isAutoRenewing: Boolean

    override fun toString(): String {
        return "PurchaseInfo(type:" + itemType + "):" + originalJson
    }

    init {
        val o = JSONObject(originalJson)
        orderId = o.optString("orderId")
        packageName = o.optString("packageName")
        sku = o.optString("productId")
        purchaseTime = o.optLong("purchaseTime")
        purchaseState = o.optInt("purchaseState")
        developerPayload = o.optString("developerPayload")
        token = o.optString("token", o.optString("purchaseToken"))
        isAutoRenewing = o.optBoolean("autoRenewing")
        this.signature = signature
    }
}