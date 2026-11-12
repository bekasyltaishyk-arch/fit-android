# Созданные и изменённые файлы

## Kotlin
- `app/src/main/java/com/example/fit/MainActivity.kt` — запуск Compose и ViewModel.
- `app/src/main/java/com/example/fit/data/model/Models.kt` — сущности и статистика.
- `app/src/main/java/com/example/fit/data/local/WorkoutCatalog.kt` — 12 тренировок.
- `app/src/main/java/com/example/fit/data/repository/FitRepository.kt` — интерфейс, Firebase и обработка ошибок.
- `app/src/main/java/com/example/fit/ui/viewmodel/FitViewModel.kt` — состояние и бизнес-логика.
- `app/src/main/java/com/example/fit/ui/navigation/FitApp.kt` — маршруты, вкладки и настройки.
- `app/src/main/java/com/example/fit/ui/screens/AuthScreen.kt` — вход, регистрация, восстановление и общая разметка.
- `app/src/main/java/com/example/fit/ui/screens/Screens.kt` — главная, каталог, подробности, сессия, прогресс, профиль.
- `app/src/main/java/com/example/fit/ui/components/Components.kt` — карточки и графики.
- `app/src/main/java/com/example/fit/ui/theme/Theme.kt` — FIT Dark.
- `app/src/main/java/com/example/fit/utils/Validation.kt` — проверка ввода.
- `app/src/test/java/com/example/fit/FitTests.kt` — тесты и FakeRepository.

## Конфигурация и ресурсы
- `build.gradle.kts`, `app/build.gradle.kts` — зависимости Firebase, Navigation и ViewModel.
- `gradle.properties` — ограничение параллелизма и компиляция Kotlin в процессе Gradle.
- `app/src/main/AndroidManifest.xml` — интернет, клавиатура и отключение резервного копирования.
- `app/src/main/res/values/themes.xml`, `strings.xml` — стартовая тема и название.
- `app/src/main/res/drawable/ic_launcher_background.xml`, `ic_launcher_foreground.xml` — знак FIT.
- `firestore.rules`, `firebase.json` — схема доступа и публикация правил.
- `build-fit.ps1` — сборка с кэшем и временными файлами на D:.

## Документация
- `README.md` — назначение, архитектура, экраны, ER-связи, запуск, телефон и экзамен.
- `README_FIREBASE.md` — подключение настоящего Firebase.
- `BUILD_STATUS.md` — проверенные результаты и ограничения.
- `FILES.md` — этот список.

`google-services.json` не создавался. Существующие кэши и проекты не удалялись.

## Тұрақты ID және қауіпсіздік тесттері
- `app/src/main/java/com/example/fit/data/model/StoredIds.kt` — тілге тәуелсіз ID және ескі мәндерді тану.
- `app/src/main/java/com/example/fit/utils/KazakhLocalization.kt` — ID үшін қазақша атаулар.
- `app/src/test/java/com/example/fit/KazakhLocalizationTest.kt` — ID, бұрынғы мәндер және локализация тесттері.
- `firestore.rules` — тек тұрақты ID-лерді қабылдайтын қауіпсіздік ережелері.
- `firebase.emulator.json`, `test-firestore.ps1` — жергілікті тест ортасы.
- `tests/firestore/package.json`, `package-lock.json`, `rules.test.cjs` — қайталанатын 16 қауіпсіздік тесті.
- `README_STABLE_IDS.md` — үйлесімділік, жариялау және тест нұсқаулығы.

## Премиум UI жаңартуы
- `ui/components/Components.kt` — ортақ карточкалар, батырмалар, статистика, графиктер және сурет ресурстарының сәйкестігі.
- `ui/theme/Theme.kt`, `Type.kt` — палитра, қаріптер және біркелкі пішіндер.
- `ui/screens/Screens.kt`, `AuthScreen.kt`, `SettingsScreen.kt` — барлық негізгі экрандардың жаңа орналасуы.
- `ui/screens/PremiumPreviews.kt` — 4 экран өлшемі/қаріп Preview конфигурациясы.
- `ui/navigation/FitApp.kt` — навигацияның көрінісі және жеңіл ауысулар.
- `res/values/strings.xml` — қазақша жаңа presentation мәтіндері.
- `res/drawable-nodpi/workout_{chest,back,legs,arms,shoulders,abs,cardio,full_body}.webp` — 8 жергілікті сурет.
- `res/drawable/workout_fallback.xml` — векторлық fallback.
- `app/src/test/java/com/example/fit/WorkoutArtworkTest.kt` — суреттер сәйкестігінің unit-тесттері.
- `app/src/androidTest/java/com/example/fit/PremiumScreensTest.kt` — 12 Compose экран тесті.
- `ASSETS.md`, `README_PREMIUM.md`, `docs/fit-artwork-contact.jpg` және `docs/premium-*-check.json` — дереккөздер мен тексеру есептері.
