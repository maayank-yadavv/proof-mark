package com.example.data.models

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import org.json.JSONObject

object BoundingBoxJson {
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val boxListType by lazy {
        Types.newParameterizedType(List::class.java, BoundingBox::class.java)
    }

    private val adapter by lazy {
        moshi.adapter<List<BoundingBox>>(boxListType)
    }

    fun toJson(boxes: List<BoundingBox>): String {
        return try {
            adapter.toJson(boxes)
        } catch (e: Throwable) {
            // Robust fallback using org.json
            try {
                val array = JSONArray()
                for (b in boxes) {
                    val obj = JSONObject()
                    obj.put("x", b.x.toDouble())
                    obj.put("y", b.y.toDouble())
                    obj.put("width", b.width.toDouble())
                    obj.put("height", b.height.toDouble())
                    obj.put("fieldKey", b.fieldKey)
                    obj.put("text", b.text)
                    obj.put("confidence", b.confidence.toDouble())
                    obj.put("status", b.status.name)
                    array.put(obj)
                }
                array.toString()
            } catch (e2: Exception) {
                "[]"
            }
        }
    }

    fun fromJson(json: String?): List<BoundingBox> {
        if (json.isNullOrBlank() || json.trim() == "[]") return emptyList()
        return try {
            adapter.fromJson(json) ?: parseManually(json)
        } catch (e: Throwable) {
            parseManually(json)
        }
    }

    private fun parseManually(json: String): List<BoundingBox> {
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<BoundingBox>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val statusStr = obj.optString("status", "PASS")
                val status = try {
                    ComplianceStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    when (statusStr) {
                        "FAIL", "NON_COMPLIANT" -> ComplianceStatus.POTENTIAL_NON_COMPLIANCE
                        "REVIEW" -> ComplianceStatus.REQUIRES_REVIEW
                        else -> ComplianceStatus.PASS
                    }
                }
                list.add(
                    BoundingBox(
                        x = obj.optDouble("x", 0.0).toFloat(),
                        y = obj.optDouble("y", 0.0).toFloat(),
                        width = obj.optDouble("width", 0.0).toFloat(),
                        height = obj.optDouble("height", 0.0).toFloat(),
                        fieldKey = obj.optString("fieldKey", obj.optString("label", "")),
                        text = obj.optString("text", ""),
                        confidence = obj.optDouble("confidence", 0.9).toFloat(),
                        status = status
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
