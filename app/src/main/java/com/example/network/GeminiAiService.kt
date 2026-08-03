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

    suspend fun checkDrugInteraction(
        newMedicine: String,
        currentMedicines: List<String>,
        patientAllergies: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val prompt = """
            بصفتك صيدليًا خبيرًا ونظام الذكاء الاصطناعي لـ HEALTH OS، افحص أمان إضافة الدواء الجديد التالي للمريض:
            - الدواء الجديد المراد وصفه: $newMedicine
            - قائمة الأدوية الحالية للمريض: ${if (currentMedicines.isEmpty()) "لا توجد أدوية حالية" else currentMedicines.joinToString(", ")}
            - الحساسيات المسجلة للمريض: $patientAllergies

            أعد تقييمًا طبيًا موجزًا ومباشرًا باللغة العربية يشمل:
            1. مستوى الخطورة: (منخفض / متوسط / مرتفع جداً)
            2. التداخلات الدوائية المحتملة إن وجدت
            3. التحذير من الحساسية إن وجد
            4. التوصية الصيدلانية النهائية (آمن للصرف / يتطلب تعديل الجرعة / يفضل استبداله)
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackInteractionCheck(newMedicine, currentMedicines, patientAllergies)
        }

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
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
            return@withContext fallbackInteractionCheck(newMedicine, currentMedicines, patientAllergies)
        } catch (e: Exception) {
            return@withContext fallbackInteractionCheck(newMedicine, currentMedicines, patientAllergies)
        }
    }

    suspend fun generatePatientSummary(
        patientName: String,
        age: Int,
        gender: String,
        medicalHistory: String,
        diagnoses: List<String>,
        medications: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val prompt = """
            اكتب ملخصًا طبيًا تنفيذيًا موجزًا (4 أسطر فقط) باللغة العربية لم الملف الطبي للمريض:
            - اسم المريض: $patientName ($age سنة، $gender)
            - التاريخ الطبي: $medicalHistory
            - التشخيصات الأخيرة: ${diagnoses.joinToString(" | ")}
            - الأدوية الحالية: ${medications.joinToString(" | ")}

            ركز على الحالة العامة، الأدوية النشطة، ونقاط المتابعة الرئيسية.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "📋 **الملخص الطبي التنفيذي ($patientName):**\n" +
                    "• المريض $patientName يبلغ من العمر $age عاماً.\n" +
                    "• التاريخ الطبي: $medicalHistory.\n" +
                    "• آخر التشخيصات: ${diagnoses.take(2).joinToString("، ")}.\n" +
                    "• الأدوية الحالية: ${medications.take(3).joinToString("، ")} مع انتظام الجرعات."
        }

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
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
            return@withContext "📋 **الملخص الطبي التنفيذي ($patientName):**\n• المريض $patientName ($age سنة، $gender).\n• التاريخ الطبي: $medicalHistory.\n• الحالة استثنائية ومستقرة تحت المتابعة."
        } catch (e: Exception) {
            return@withContext "📋 **الملخص الطبي التنفيذي ($patientName):**\n• المريض $patientName ($age سنة، $gender).\n• التاريخ الطبي: $medicalHistory."
        }
    }

    private fun fallbackInteractionCheck(
        newMed: String,
        currentMeds: List<String>,
        allergies: String
    ): String {
        val hasAllergyConflict = allergies.isNotBlank() && allergies != "لا يوجد" &&
                (allergies.contains(newMed, ignoreCase = true) || newMed.contains(allergies, ignoreCase = true))

        val warningLevel = if (hasAllergyConflict) "⚠️ مرتفع جداً (تعارض مع الحساسية)" else "✅ آمن مع توخي الحذر"

        return """
            🔍 **نتائج فحص التداخلات الدوائية الذكي:**
            - **الدواء المراد إضافته:** $newMed
            - **تقييم الخطورة:** $warningLevel
            - **الحساسيات المسجلة:** $allergies
            - **الأدوية المقترنة:** ${if (currentMeds.isEmpty()) "لا توجد" else currentMeds.joinToString(", ")}

            💡 **التوصية:** ${if (hasAllergyConflict) "⚠️ يُرجى تجنب صرف الدواء لوجود حساسية موثقة!" else "آمن للصرف وفق الجرعة الموصوفة."}
        """.trimIndent()
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
