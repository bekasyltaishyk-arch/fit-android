# FIT көрнекіліктерінің дереккөзі

Барлық сегіз сурет осы тапсырма үшін OpenAI **built-in image_gen** құралымен жасалды. Олар нақты фототүсірілім не интернеттен жүктелген сток фотолар емес: фотостильдегі AI иллюстрациялар. Нақты адамның анықтамалық суреті, үшінші тараптың логотипі немесе сыртқы фотоматериал қолданылған жоқ.

Қолдану шарттарының көзі: [OpenAI Terms of Use — Content](https://openai.com/policies/terms-of-use/#content). Осы шарттарда OpenAI мен пайдаланушы арасындағы Output құқықтары, заң рұқсат еткен дәрежеде, пайдаланушыға тиесілі екені көрсетілген. Бұлар сток сайттың CC лицензиясымен алынған суреттер емес. Сыртқы фотосуреттің авторын не лицензиясын ойдан көрсеткен жоқпыз.

Файлдар `app/src/main/res/drawable-nodpi/` ішінде, барлығы 960 × 640 пиксель, WebP сапасы 82. Density-ге байланысты артық масштабтау мен үлкен bitmap бөлуді болдырмау үшін `nodpi` таңдалды. Барлық сегіз файл бірге **266 868 байт** (шамамен 261 КиБ). Суреттер желісіз ашылады; `ContentScale.Crop` пропорцияны сақтайды. Мәтін үстіне қажет жерде қара градиент қойылған.

| Санат | Файл | Байт | Көрініс |
|---|---|---:|---|
| Кеуде | `workout_chest.webp` | 33380 | Қолды бүгіп-жазу |
| Арқа | `workout_back.webp` | 25484 | Қарама-қарсы қол мен аяқты созу |
| Аяқ | `workout_legs.webp` | 36608 | Отырып-тұру |
| Қол | `workout_arms.webp` | 44868 | Қабырғаға сүйеніп қолды бүгіп-жазу |
| Иық | `workout_shoulders.webp` | 34080 | Қолды екі жаққа созу |
| Іш бұлшықеттері | `workout_abs.webp` | 31502 | Шынтаққа сүйеніп тұру |
| Кардио | `workout_cardio.webp` | 30610 | Тізені көтеріп адымдау |
| Бүкіл дене | `workout_full_body.webp` | 30336 | Артқа адымдап отыру |

Бір санаттағы жаттығулар сол санаттың суретін бөліседі. Барлық 12 жаттығу сақталған. Белгісіз санат үшін жеке авторлық векторлық fallback: `app/src/main/res/drawable/workout_fallback.xml`.

Шолу: `docs/fit-artwork-contact.jpg` (қосымша экранының скриншоты емес, сегіз суреттің байланыс парағы).

## Генерация сұраулары

Кеуде үшін толық сұрау:

> Use case: photorealistic-natural. Asset: landscape offline fitness Android app card, 3:2. Premium editorial fitness photograph of an adult athlete performing a proper push-up in side profile, chest training, charcoal minimalist gym, black sportswear with small lime detail, soft dramatic sidelight, realistic athletic anatomy, complete hands and feet, no text, no logos, no watermark. Wide composition with clear exercise action centered and enough dark space near bottom for app overlay. Save the generated asset for project use.

Қалған жеті сурет үшін ортақ сұрау үлгісі:

> Use case: photorealistic-natural. Asset: landscape 3:2 photograph for premium FIT fitness Android app. [SUBJECT] Charcoal minimalist gym with softly blurred background, black modest sportswear with subtle lime detail, realistic athletic adult anatomy, soft cinematic side lighting, professional editorial sports photography, coherent dark gray and lime palette. Exercise is centered and complete in frame; allow dark lower negative space for app text. No text, no branding, no watermark. Single photo, not a collage.

SUBJECT мәндері:

- back: Adult woman performing bird-dog exercise on a mat, opposite arm and leg extended, neutral spine, side three-quarter view. Back and core stability training, no equipment needed.
- legs: Adult male athlete performing a bodyweight squat with arms forward, feet shoulder width, side three-quarter full-body view. Leg training.
- arms: Adult woman in a controlled inclined push-up against a wall, bent elbows, full arms visible, side view. Arms training.
- shoulders: Adult athlete standing with both arms extended laterally at shoulder height doing shoulder mobility circles, full upper body and hands visible. Shoulder training.
- abs: Adult woman holding a proper forearm plank on a mat, elbows under shoulders, straight body, full body side view. Abdominal training.
- cardio: Adult athlete doing a brisk high-knee march in place, one knee lifted, opposite arm forward, full body, realistic balanced stance. Low-impact cardio training.
- full_body: Adult athlete in reverse lunge position, front knee aligned with foot, torso upright, arms balanced, full body three-quarter view. Full-body home training.

Дайын суреттер визуалды тексерілді, содан кейін мазмұнын өзгертпей, өлшемі кішірейтіліп WebP форматына сақталды. Қосымшаға тек оңтайландырылған файлдар қосылды.
