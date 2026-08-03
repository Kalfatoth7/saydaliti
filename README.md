# نظام مركز الرحمة الطبي | Al-Rahma Health OS

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-teal.svg)](https://m3.material.io)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**نظام مركز الرحمة الطبي (Al-Rahma Health OS)** هو تطبيق Android احترافي متكامل مخصص لإدارة المراكز الطبية والمستشفيات بمختلف أقسامها (المرضى، الصيدليات، الاستقبال، العمليات، المعامل، المالية، والموارد البشرية) بآلية عمل آمنة وموثوقة توافق أحدث معايير البرمجة والأمان.

---

## 🏗️ البنية البرمجية والنظام (Clean Architecture + MVVM)

تم تصميم المشروع وفقًا لمعايير **Clean Architecture** مع نمط **MVVM (Model-View-ViewModel)** لتأمين فصل تام بين طبقة البيانات، المنطق التجاري، وواجهات المستخدم:

```
app/src/main/java/com/example/
├── data/                      # طبقة البيانات (Data Layer)
│   ├── local/                 # Room Database, DAOs, Demo Seed Engine
│   ├── models/                # Room Entities, Data Transfer Objects & UserRoles
│   └── repository/            # HealthOsRepository (Single Source of Truth)
├── security/                  # طبقة الأمان والحماية (Security Layer)
│   ├── SecurityManager.kt     # RBAC System, AES-256 Encryption, SHA-256 Hashing, Input Sanitizer
│   ├── SecurityLogger.kt      # Sensitive PII Log Masking
│   └── DataValidator.kt       # Medical & Financial Data Validation Engine
├── ui/                        # طبقة العرض (UI Layer)
│   ├── MainViewModel.kt       # StateFlow Management & Reactive UI State
│   ├── components/            # Reusable UI Components, Modals & Navigation Rails
│   ├── screens/               # Compose Screens (Dashboard, Patients, Inventory, Financials, etc.)
│   └── theme/                 # Material Design 3 Color Schemes, Typography & Shapes
└── MainActivity.kt            # App Main Entry Point
```

---

## 🛡️ تحسينات الأمان والحماية (Security Best Practices)

1. **التحكم بالصلاحيات حسب الأدوار (Role-Based Access Control - RBAC):**
   - مصفوفة أمان مدمجة تتضمن أدوار النظام: `SUPER_ADMIN`, `OWNER`, `MANAGER`, `DOCTOR`, `PHARMACIST`, `NURSE`, `LAB_TECH`, `RADIOLOGY_TECH`, `ACCOUNTANT`, `RECEPTIONIST`.
   - منع المنح غير المصرح به للعمليات الحساسة (مثل إعادة تعيين المالية، تعديل ملفات المرضى، نقل الأدوية).

2. **تشفير البيانات الحساسة (AES-256-GCM Encryption):**
   - حماية السجلات الطبية وبيانات التواصل عبر وحدة `SecurityManager.encryptData()`.

3. **التوقيع الرقمي لمنع التلاعب (Audit Log Signatures):**
   - توليد تجزئة SHA-256 لسجلات عمليات النظام (Audit Logs) لمنع تعديلها أو التلاعب بها.

4. **حماية بيانات المرضى الشخصية (PII Masking):**
   - إخفاء وتعمية أرقام الهواتف والهويات الوطنية في السجلات العامة والطباعة عبر `maskPhoneNumber` و `maskNationalId`.

5. **تطهير المدخلات (Input Sanitization):**
   - إزالة الرموز والحروف الخاصة والـ Script tags لتجنب هجمات Injection و XSS.

---

## 🗄️ قاعدة البيانات وتطوير Room (Database Architecture)

- **الكيانات والداول (Entities & DAOs):**
  - دعم كيان `PatientProfile` المخصص لملفات المرضى والتاريخ المرضي والبيانات الشاملة.
  - فهرسة المفاتيح الثانوية `@Index` على الحقول الأكثر استعلامًا (`pharmacyId`, `phone`, `nationalId`, `patientId`) لتسريع الأداء وتفادي المسح الكامل للجداول (Full Table Scans).
  - إدارة المعاملات التزاملية وحذف الحسابات المعزولة.

---

## 🧪 الاختبارات والجودة (Testing & QA)

تضمن وحدات الاختبار المضافة بالكتيبة التأكد من عمل الوظائف البرمجية بكفاءة عالية على الـ JVM المحلي دون الحاجة لمحاكي:

- **اختبارات الأمان والصلاحيات:** `SecurityManagerUnitTest.kt`
- **اختبارات أداء واجهة البيانات:** `ExampleRobolectricTest.kt`

### أمر تشغيل الاختبارات عبر Gradle:
```bash
./gradlew :app:testDebugUnitTest
```

---

## 🌟 أبرز المميزات (Key Features)

- **شاشة القيادة والتحكم المركزية (Command Center & Dashboard):**
  - متابعة فورية للإحصائيات الحيوية، الإيرادات، ونسب إشغال الأسرة والعيادات.
  - تنبيهات ذكية وسجلات تدقيق كاملة (Audit Logs).

- **إدارة المرضى والاستقبال (Patients & Reception Queue):**
  - سجل طبي إلكتروني موحد لكل مريض يشمل التاريخ المرضي والفحوصات.
  - تنظيم قائمة الانتظار، حجز وتنسيق المواعيد والزيارات.

- **شبكة الصيدليات والمخزون الذكي (Pharmacy Network & Smart Inventory):**
  - متابعة المخزون الدوائي، وتواريخ الصلاحية، والتنقل بين الصيدليات (المحرك الرئيسي، العيادات الخارجية، الطوارئ).
  - استخدام ذكاء اصطناعي مدمج للتحليل التنبؤي وتفادي نقص الأدوية.

- **العمليات والتحاليل والأشعة (Surgeries, Labs & Radiology):**
  - جدول إدارة العمليات الجراحية وتتبع الحالات الميدانية.
  - متابعة نتائج المختبرات والأشعة السينية والطبقية بحالة فورية.

- **المراسلات والتواصل الفوري (Internal Messaging System):**
  - نظام دردشة داخلي آمن بين الأطباء، الصيدلية، والكادر الإداري.
  - إرسال تعميمات وإسناد مهام ومتابعتها.

- **المالية والموارد البشرية (Financials & HR):**
  - إدارة المؤشرات المالية ومتابعة رواتب وحضور الكادر الطبي والإداري.

---

## 🛠️ التقنيات المستخدمة (Tech Stack)

- **اللغة الأساسية:** Kotlin 2.x
- **واجهة المستخدم:** Jetpack Compose + Material Design 3 (M3)
- **إدارة التوجيه:** Jetpack Navigation Compose (Type-safe Navigation)
- **إدارة الحالة والبيانات:** Kotlin Coroutines, StateFlow, ViewModel
- **قاعدة البيانات:** Room Database / Embedded Enterprise Memory Engine
- **الذكاء الاصطناعي:** Gemini API Client Integrations
- **التوافقية والإنتاج:** Android SDK 35/36 (Target SDK 35)

---

## 🚀 كيفية التشغيل وبناء المشروع (Build & Run Instructions)

### المتطلبات الأساسية (Prerequisites):
- **Android Studio** Ladybug (2024.2+) أو أحدث.
- **JDK:** Java 17 / Java 21.
- **Android SDK:** API Level 35 أو 36.

### الخطوات:
1. قم باستنساخ المستودع:
   ```bash
   git clone https://github.com/username/alrahma-health-os.git
   cd alrahma-health-os
   ```
2. افتح المشروع في **Android Studio**.
3. قم بضبط المفاتيح البيئية في ملف `.env` (إذا كنت تستخدم ميزات Gemini AI):
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
4. قم بمزامنة المشروع وبنائه عبر Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🛡️ الأمان والخصوصية (Security & Compliance)

- تشفير وحماية البيانات الطبية بالكامل.
- تطبيق مبادئ `usesCleartextTraffic="false"` وتأمين الاتصالات الشبكية عبر SSL/TLS.
- تقييد الصلاحيات ورفض تتبع الموقع أو الكاميرا بدون إذن صريح من المستخدم.
- استبعاد مفاتيح API وسجلات البناء الحساسة من التحكم بالإصدار عبر `.gitignore`.

---

## 📜 الترخيص (License)

هذا المشروع مرخص بموجب رخصة **MIT** - راجع ملف [LICENSE](LICENSE) للمزيد من التفاصيل.
