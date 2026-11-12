# FIT жобасын қалпына келтіру

GitHub-та FIT-ті қайта жинауға қажетті бастапқы код, 12 жаттығу, суреттер, Gradle Wrapper, тәуелділік нұсқалары, Firestore ережелері және тесттер бар. APK, Android SDK, кэштер, жеке Firebase конфигурациясы және қолтаңба кілттері репозиторийге кірмейді.

## Ноутбукты тазаламас бұрын

- [FIT репозиторийін](https://github.com/bekasyltaishyk-arch/fit-android) ашып, `main` тармағындағы кодты тексеріңіз. **Code → Download ZIP** арқылы қосымша көшірмені флешкаға сақтауға болады.
- Firebase-ке кіретін Google аккаунтыңызға қолжетімділікті тексеріңіз. Қаласаңыз, жергілікті `app/google-services.json` файлын жеке резервтік орынға көшіріңіз.
- Телефондағы debug қолданбаны жоймай жаңарту маңызды болса, бұрынғы қолтаңба кілтін бөлек сақтаңыз. Әдеттегі Windows жолы — `%USERPROFILE%\.android\debug.keystore`. Егер басқа кілт қолданылса, сол кілт қажет. Кілттерді ашық GitHub-қа салмаңыз.
- GitHub-та Firestore құжаттары мен Firebase пайдаланушыларының көшірмесі жоқ. Бұрынғы деректерді көру үшін сол Firebase жобасы мен FIT аккаунтын қолданыңыз. Firebase жобасын жоймаңыз.
- Орын босату үшін алдымен `app/build`, `build`, жоба ішіндегі `.gradle` және `tests/firestore/node_modules` сияқты қайта жасалатын қалталарды қараңыз. Бүкіл жоба қалтасын жоймас бұрын жарияланбаған өзгерістер мен жеке файлдардың көшірмесін тексеріңіз.

## Android Studio-да ашу

1. Android Studio-ны орнатыңыз. **SDK Manager** ішінен Android SDK Platform 36 орнатыңыз. **Gradle JDK** үшін Android Studio-ның кірістірілген JDK-сын немесе JDK 17+ таңдаңыз.
2. **Get from VCS / Clone Repository** арқылы мына мекенжайды енгізіңіз:

   ```text
   https://github.com/bekasyltaishyk-arch/fit-android.git
   ```

   Немесе GitHub-тан ZIP жүктеп, архивті шығарып, **Open** арқылы `settings.gradle.kts` файлы бар түбір қалтаны ашыңыз.
3. Gradle Sync аяқталғанша күтіңіз. Бірінші рет Gradle және кітапханаларды жүктеуге интернет керек.
4. Android Studio-дан **Run ▶** басыңыз немесе Windows PowerShell-де жоба түбірінен орындаңыз:

   ```powershell
   .\gradlew.bat assembleDebug testDebugUnitTest
   ```

   macOS/Linux: `./gradlew assembleDebug testDebugUnitTest`.

APK: `app/build/outputs/apk/debug/app-debug.apk`. Firebase файлын қоспай да жинап, каталогты қарай аласыз.

## Бұрынғы Firebase-ті қайтару

[Firebase Console](https://console.firebase.google.com/) → бұрынғы FIT жобасы → **Project settings → General → Your apps** → `com.example.fit` Android қолданбасы → **Download google-services.json**. Файлды жүктелген жобаның `app/google-services.json` жолына қойып, қайта жинаңыз. Жаңа Firebase жобасы бұрынғы аккаунттар мен тарихты көшірмейді. [Firebase ресми нұсқаулығы](https://firebase.google.com/docs/android/google-services-plugin-and-file).

Телефонға бұрынғы қолданбаның үстінен орнату үшін APK қолтаңбасы сәйкес болуы керек. Сәйкес болмаса, деректер қажет кезде қолданбаны бірден жоймаңыз: алдымен бұрынғы кілтті тауып, сол кілтпен жинаңыз. Қолданбаны жою телефондағы жергілікті деректерді өшіреді.
