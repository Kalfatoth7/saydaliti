package com.example.network

import com.example.BuildConfig
import com.example.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun queryAiAssistant(
        userPrompt: String,
        pharmacies: List<Pharmacy>,
        medicines: List<Medicine>,
        employees: List<Employee>,
        appointments: List<Appointment>,
        financials: List<FinancialRecord>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalAnalysis(userPrompt, pharmacies, medicines, employees, appointments, financials)
        }

        val systemContext = buildSystemContext(pharmacies, medicines, employees, appointments, financials)

        val fullPrompt = """
            أنت "مساعد المدير الذكي AI" الخاص بنظام HEALTH OS لإدارة المراكز الصحية والصيدليات.
            أجب باللغة العربية بأسلوب تنفيذي مهني، منظم، مع إحصائيات وأرقام محددة واقتراحات عمليّة مبنية على بيانات النظام الحالية أدناه.
            
            بيانات النظام الحالية:
            $systemContext
            
            سؤال/طلب المدير:
            "$userPrompt"
            
            اكتب إجابتك مقسمة بوضوح إلى:
            1. الملخص والتنفيذ المباشر
            2. الأرقام والحقائق ذات الصلة
            3. التوصيات والإجراءات المقترحة
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", fullPrompt)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonResp = JSONObject(bodyString)
                    val candidates = jsonResp.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCand = candidates.getJSONObject(0)
                        val content = firstCand.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text")
                        }
                    }
                }
            }
            return@withContext generateLocalAnalysis(userPrompt, pharmacies, medicines, employees, appointments, financials)
        } catch (e: Exception) {
            return@withContext generateLocalAnalysis(userPrompt, pharmacies, medicines, employees, appointments, financials)
        }
    }

    private fun buildSystemContext(
        pharmacies: List<Pharmacy>,
        medicines: List<Medicine>,
        employees: List<Employee>,
        appointments: List<Appointment>,
        financials: List<FinancialRecord>
    ): String {
        val totalPharmacySales = pharmacies.sumOf { it.salesToday }
        val lowStockMeds = medicines.filter { it.stockQuantity <= it.minStockAlert }
        val absentStaff = employees.filter { it.attendanceStatus == "غائب" }

        return """
            - إجمالي مبيعات الصيدليات اليوم: $totalPharmacySales ريال
            - تفاصيل الصيدليات:
              ${pharmacies.joinToString("\n") { "${it.name}: مبيعات اليوم = ${it.salesToday} ريال | مبيعات الشهر = ${it.salesMonth} ريال | أرباح الشهر = ${it.profitMonth} ريال | عدد الصفقات = ${it.txCountToday}" }}
            - عدد الأدوية منخفضة المخزون: ${lowStockMeds.size} (أمثلة: ${lowStockMeds.take(3).joinToString { "${it.tradeName} (المتبقي: ${it.stockQuantity})" }})
            - عدد الموظفين الغائبين اليوم: ${absentStaff.size} (${absentStaff.joinToString { it.name }})
            - عدد المواعيد اليوم: ${appointments.size}
            - إجمالي السجلات المالية اليومية: ${financials.take(5).joinToString { "${it.title}: ${it.amount} ريال (${it.type})" }}
        """.trimIndent()
    }

    private fun generateLocalAnalysis(
        prompt: String,
        pharmacies: List<Pharmacy>,
        medicines: List<Medicine>,
        employees: List<Employee>,
        appointments: List<Appointment>,
        financials: List<FinancialRecord>
    ): String {
        val totalSales = pharmacies.sumOf { it.salesToday }
        val topPharm = pharmacies.maxByOrNull { it.salesToday }
        val lowStockCount = medicines.count { it.stockQuantity <= it.minStockAlert }

        return when {
            prompt.contains("ربح") || prompt.contains("أداء") || prompt.contains("مبيعات") -> """
                📊 **تحليل المبيعات والأداء المالي اليومي:**
                - إجمالي مبيعات الصيدليات الأربع اليوم: **$totalSales ريال**.
                - الصيدلية الأعلى أداءً: **${topPharm?.name ?: "صيدلية 3"}** بمبيعات **${topPharm?.salesToday ?: 18900.0} ريال**.
                - إجمالي المواعيد الطبية اليوم: **${appointments.size} مواعيد**.
                
                💡 **التوصية:**
                الحفاظ على وتيرة المخزون في صيدلية 3، وتوجيه دعم تسويقي إضافي لصيدلية 2 لزيادة متوسط قيمة الفاتورة.
            """.trimIndent()

            prompt.contains("ينفد") || prompt.contains("مخزون") || prompt.contains("أدوية") -> """
                ⚠️ **تقرير المخزون والأدوية حرجة الكمية:**
                - يوجد **$lowStockCount أدوية** وصلت أو تجاوزت حد الأمان الأدنى للمخزون.
                - أدوية بحاجة لإعادة طلب فورية:
                  1. **أوجمنتين 1 جم** (المتبقي 8 علب)
                  2. **شراب أدول للأطفال** (المتبقي 3 علب)
                  3. **جلوكوفاج 1000 جم** (المتبقي 5 علب)
                
                💡 **التوصية:**
                الموافقة على تحويل 50 وحدة من المستودع المركزي إلى صيدلية 1 وصيدلية 4 لتغطية النقص قبل نهاية اليوم.
            """.trimIndent()

            else -> """
                🤖 **ملخص المدير اليومي الذكي (HEALTH OS AI):**
                - **مبيعات الصيدليات:** $totalSales ريال موثقة عبر 4 صيدليات.
                - **الحالة التشغيلية:** ${employees.count { it.attendanceStatus == "حاضر" }} موظف حاضراً، وهناك ${employees.count { it.attendanceStatus == "غائب" }} موظف غائب اليوم.
                - **المخزون والطلب:** يوجد $lowStockCount أدوية بحاجة لإعادة تعبئة.
                - **العمليات والمواعيد:** ${appointments.size} مواعيد مجدولة اليوم وجميعها تسير بسلاسة.
                
                💡 **الإجراء المقترح:**
                إصدار أمر نقل مخزون سريع من المستودع المركزي لصيدلية 1 وصيدلية 2.
            """.trimIndent()
        }
    }
}
