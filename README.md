# تطبيق نور IPTV (Noor IPTV Player)

مشغّل IPTV وسائط متكامل وعالي الأداء لنظام Android، مصمم خصيصاً للهواتف والأجهزة اللوحية وأجهزة التلفاز الذكية (Android TV).

---

## 1. المعمارية والتقنيات المستخدمة

- **الواجهة:** Jetpack Compose + Compose UI + Material Design 3، مع دعم كامل للـ D-pad وAndroid TV بدون أي اعتماد على شاشات اللمس.
- **مشغل الوسائط:** AndroidX Media3 / ExoPlayer (v1.5.1) مع دعم:
  - بروتوكولات وتنسيقات: HLS (`.m3u8`), MPEG-TS (`.ts`), MP4, MKV.
  - تعدد المسارات الصوتية (Audio Tracks Selection).
  - اختيار ملفات الترجمة (Subtitles / CC).
  - تبديل نسب العرض (Fit, Fill, Zoom, 16:9, 4:3).
  - وضع صورة داخل صورة (Picture-in-Picture).
  - إيماءات التمرير لضبط الصوت والسطوع والتقديم/التأخير.
  - إعادة الاتصال التلقائية عند انقطاع البث (Auto-reconnect with exponential backoff).
- **التخزين المحلي (Local Cache):** Room Database لسرعة استجابة فائقة لقوائم القنوات الضخمة (20,000+ عنصر) مع فهارس بحث سريعة.
- **الشبكة وتحليل القوائم:**
  - `XtreamApiService` (Retrofit + Moshi) للاتصال بـ Xtream Codes Player API.
  - `M3uParser`: محلل تدفقي فائق السرعة عبر `BufferedReader` لمعالجة ملفات وقوائم M3U/M3U8 الضخمة دون استهلاك الذاكرة العشوائية (Zero-OOM streaming parser).
- **نظام التفعيل (Activation):** معرّف جهاز فريد (Hardware Device ID) مع فترة تجريبية مجانية لمدة 7 أيام ودعم أكواد التفعيل وتكامل Supabase.

---

## 2. نظام التصميم (Design System)

- **الألوان:**
  - الخلفية الرئيسية: `#0b0f1a` (Dark Navy Deep Background)
  - الأسطح والحاويات: `#0e1422` (Surface)
  - البطاقات: `#1b273f` (Card Surface)
  - اللون التمييزي الذهبي: `#E5A93C` (Gold Primary) مع درجات `#F6C860` و`#A67C1E`
  - نصوص عالية التباين: `#F1F3F7` و`#9AA6B8` متوافقة مع معايير الوصول (Accessibility Contrast Ratio ≥ 4.5:1).
- **الخطوط والأبعاد:**
  - شبكة قياس 8dp وزوايا دائرية 16dp - 24dp.
  - إطار ذهبي بارز ومتحرك (Scale 1.05x) عند التركيز بالريموت لأجهزة Android TV.

---

## 3. إعداد متغيرات Supabase للتفعيل (اختياري)

في حال ربط التفعيل بسيرفر Supabase السحابي:
1. أنشئ جدولاً باسم `devices` و`activations` في Supabase.
2. أضف المفاتيح في لوحة الـ Secrets في AI Studio:
   - `SUPABASE_URL=https://your-project.supabase.co`
   - `SUPABASE_ANON_KEY=your-anon-key`

---

## 4. البناء والاختبار (Build & Testing)

- **فحص الكود والبناء:**
  ```bash
  gradle assembleDebug
  ```
- **تشغيل اختبارات الوحدة:**
  ```bash
  gradle :app:testDebugUnitTest
  ```

---

## 5. قائمة المخاطر والتحسينات المستقبلية

1. **التعامل مع السيرفرات غير المستقرة:**
   - تم تطبيق LoadControl مخصص بـ Buffer يبدأ من ثانية واحدة لسرعة تقليب القنوات (Zapping time < 1.5s).
   - تحسين قادم: إضافة خيار تبديل تلقائي إلى رابط سيرفر احتياطي (Fallback server) في حال توقف السيرفر الرئيسي.
2. **تسجيل البث (Catch-up / PVR):**
   - تم تجهيز واجهة EPG بدعم علامات Catch-up. يمكن في الإصدار القادم إضافة تسجيل البث محلياً في الذاكرة التخزينية.
3. **مشغل خارجي (External Player Intent):**
   - إمكانية فتح البث عبر VLC أو MX Player بضغطة زر لمن يرغب في استخدام كودك خارجي.
