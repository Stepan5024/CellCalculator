package bokarev.st.stretchceilingcalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.TypeCategory
import bokarev.st.stretchceilingcalculator.entities.TypeOfWork
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGoToClients: Button = findViewById(R.id.btnGoToClients)
        val btnGoToPrices: Button = findViewById(R.id.btnGoToPrices)

        try {
            val client = getClientFromPreviousActivity()
            val previousActivity = intent.getStringExtra("PreviousActivity")
            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName}"
            )
        } catch (exp: RuntimeException) {

        }

        btnGoToClients.setOnClickListener {

            val intent = Intent(this, Clients::class.java).also {
                it.putExtra("ClientEntity", setNullClient())
                it.putExtra("PreviousActivity", "StartActivity")

            }
            startActivity(intent)

        }

        btnGoToPrices.setOnClickListener {
            val intent = Intent(this, Calculation::class.java).also {
                it.putExtra("ClientEntity", setNullClient())
                it.putExtra("PreviousActivity", "StartActivity")
            }
            startActivity(intent)

        }

        val dao = CategoriesDataBase.getInstance(this).categoriesDao
        var listOfTypeCategory: List<TypeCategory> = arrayListOf()

        val job = GlobalScope.launch(Dispatchers.Default) {
            listOfTypeCategory = dao.getTypeCategory()
        }
        Log.d("mytag", "listOfTypeCategory 1 is size = ${listOfTypeCategory.size}")
        runBlocking {
            // waiting for the coroutine to finish it"s work
            job.join()
            Log.d("mytag", "listOfTypeCategory  2 is size = ${listOfTypeCategory.size}")

            if(listOfTypeCategory.size <= 1)  {
                val typeOfWork = listOf(
                    TypeOfWork(1, "Классическая система"),
                    TypeOfWork(2, "Система Kraab"),
                    TypeOfWork(3, "Система Slott"),
                    TypeOfWork(4, "Система Шток"),
                    TypeOfWork(5, "Система Flexy"),
                    TypeOfWork(6, "Парящие"),
                    TypeOfWork(7, "ПК-4 Светованна"),
                    TypeOfWork(8, "Тканевые потолки"),
                    TypeOfWork(9, "Работы по освещению"),
                    TypeOfWork(10, "Дополнительные работы"),
                    TypeOfWork(11, "Материалы классической системы"),
                    TypeOfWork(12, "Материалы системы Kraab"),
                    TypeOfWork(13, "Материалы системы Slott"),
                    TypeOfWork(14, "Материалы системы Шток"),
                    TypeOfWork(15, "Материалы системы Flexy"),
                    TypeOfWork(16, "Материалы парящие"),
                    TypeOfWork(17, "Материалы тканевые потолки"),
                    TypeOfWork(18, "Материалы по освещению"),
                    TypeOfWork(19, "Материалы полотна"),
                    TypeOfWork(20, "Дополнительные материалы"),

                    )

                val typeCategory = listOf(
                    TypeCategory(1, "Установка стенового профиля", "м.п.", 250, 1),
                    TypeCategory(2, "Установка потолочного профиля", "м.п.", 200, 1),
                    TypeCategory(3, "Установка полотна", "м2", 200, 1),
                    TypeCategory(4, "Установка маскировочной ленты", "м.п.", 80, 1),
                    TypeCategory(5, "Криволинейные участки", "м.п.", 400, 1),

                    TypeCategory(6, "Установка стенового профиля", "м.п.", 400, 2),
                    TypeCategory(7, "Установка потолочного профиля", "м.п.", 350, 2),
                    TypeCategory(8, "Установка полотна", "м2", 350, 2),
                    TypeCategory(9, "Запил + обработка внутреннего угла", "шт.", 700, 2),
                    TypeCategory(10, "Запил + обработка внешнего угла", "шт.", 1000, 2),
                    TypeCategory(11, "Криволинейные участки", "м.п.", 500, 2),
                    TypeCategory(12, "Обход сложных углов расстояние менее 25 см", "шт.", 3500, 2),

                    TypeCategory(13, "Установка профиля 40/80", "м.п.", 950, 3),
                    TypeCategory(14, "Установка полотна", "м.п.", 300, 3),
                    TypeCategory(15, "Запил + обработка углов", "м.п.", 800, 3),
                    TypeCategory(16, "Криволинейные участки", "м.п.", 1700, 3),
                    TypeCategory(17, "Установка профиля euroslott", "м.п.", 270, 3),
                    TypeCategory(18, "Запил + обработка внутреннего угла euroslott", "шт.", 250, 3),
                    TypeCategory(19, "Запил + обработка внешнего угла euroslott", "шт.", 250, 3),
                    TypeCategory(20, "Установка полотна в euroslott", "м2", 600, 3),
                    TypeCategory(21, "Установка световой линии", "м.п.", 900, 3),
                    TypeCategory(22, "Запил + обработка внутреннего угла св линии", "шт.", 1200, 3),
                    TypeCategory(23, "Запил + обработка внешнего угла св линии", "шт.", 1200, 3),
                    TypeCategory(24, "Установка полотна в световую линию", "м.п.", 350, 3),
                    TypeCategory(25, "Установка заглушек в световую линию", "шт.", 300, 3),
                    TypeCategory(26, "Запил перекрестия световой ниши Slott", "шт.", 2400, 3),

                    TypeCategory(27, "Установка профиля", "м.п.", 250, 4),
                    TypeCategory(28, "Обработка углов", "in.", 500, 4),
                    TypeCategory(29, "Установка полотна", "м2", 800, 4),
                    TypeCategory(30, "Обход сложных углов расстояние менее 25 см", "шт.", 2000, 4),

                    TypeCategory(31, "Установка световой линии 30/50мм", "м.п.", 550, 5),
                    TypeCategory(32, "Запил + обработка углов", "шт.", 450, 5),
                    TypeCategory(33, "Установка полотна", "м.п.", 400, 5),
                    TypeCategory(34, "Установка заглушек", "шт.", 300, 5),
                    TypeCategory(35, "Установка рассеивателя", "м.п.", 60, 5),
                    TypeCategory(36, "Запил углов рассеивателя", "шт.", 300, 5),
                    TypeCategory(37, "Запил перекрестия световой линии Flexy", "шт.", 900, 5),
                    TypeCategory(38, "Запил перекрестия рассеивателя", "шт.", 600, 5),

                    TypeCategory(39, "Установка профиля для ПВХ", "м.п.", 450, 6),
                    TypeCategory(40, "Запил + обработка углов", "шт.", 600, 6),
                    TypeCategory(41, "Установка полотна", "м2", 250, 6),
                    TypeCategory(42, "Установка профиля для ткани М1", "м.п.", 550, 6),
                    TypeCategory(43, "Запил + обработка углов", "шт.", 800, 6),
                    TypeCategory(44, "Установка полотна", "м2", 300, 6),

                    TypeCategory(45, "Установка профиля", "м.п.", 400, 7),
                    TypeCategory(46, "Обработка внутреннего угла", "шт.", 150, 7),
                    TypeCategory(47, "Обработка внешнего угла", "шт.", 250, 7),
                    TypeCategory(48, "Установка полотна", "м2", 200, 7),

                    TypeCategory(49, "Установка стенового профиля", "м.п.", 500, 8),
                    TypeCategory(50, "Установка потолочного профиля", "м.п.", 350, 8),
                    TypeCategory(51, "Установка полотна", "м2", 400, 8),

                    TypeCategory(52, "Установка встроенного светильника D<100мм", "шт.", 550, 9),
                    TypeCategory(53, "Установка накладного светильника D<100мм", "шт.", 600, 9),
                    TypeCategory(54, "Установка встроенного светильника D>100мм", "шт.", 600, 9),
                    TypeCategory(55, "Установка накладного светильника D>100мм", "шт.", 650, 9),
                    TypeCategory(56, "Установка не стандартных светильников", "шт.", 800, 9),
                    TypeCategory(57, "Установка закладной для люстры", "шт.", 500, 9),
                    TypeCategory(58, "Установка простой люстры", "шт.", 500, 9),
                    TypeCategory(59, "Установка сложной люстры", "шт.", 5000, 9),
                    TypeCategory(60, "Установка закладной для накладного трека", "м.п.", 500, 9),
                    TypeCategory(61, "Установка накладного трека", "м.п.", 300, 9),
                    TypeCategory(62, "Подключение накладного трека", "шт.", 300, 9),
                    TypeCategory(63, "Установка трека ТехноЛайт", "м.п.", 950, 9),
                    TypeCategory(64, "Установка заглушек в ТехноЛайт", "шт.", 300, 9),
                    TypeCategory(65, "Подключение питания к ТехноЛайт", "шт.", 400, 9),
                    TypeCategory(66, "Запил углов ТехноЛайт", "шт.", 850, 9),
                    TypeCategory(67, "Установка трека Инфинити", "м.п.", 500, 9),
                    TypeCategory(68, "Установка заглушек Инфинити", "шт.", 300, 9),
                    TypeCategory(69, "Подключение Инфинити", "шт.", 500, 9),
                    TypeCategory(70, "Запил углов Инфинити", "шт.", 800, 9),
                    TypeCategory(71, "Установка светодиодной ленты", "м.п.", 70, 9),
                    TypeCategory(72, "Пайка ленты/кабеля", "шт.", 200, 9),
                    TypeCategory(73, "Установка трансформатора", "шт.", 500, 9),
                    TypeCategory(74, "Установка пульта/диммера", "шт.", 250, 9),
                    TypeCategory(75, "Прокладка кабеля", "м.п.", 80, 9),

                    TypeCategory(76, "Установка скрытого карниза ПК-5 ПК-15 ПК-14 ПК-12", "м.п.", 800, 10),
                    TypeCategory(77, "Запил + обработка углов ПК-5 ПК-15 ПК-14 ПК-12", "шт.", 700, 10),
                    TypeCategory(78, "Установка заглушек торцевых ПК-14", "шт.", 300, 10),
                    TypeCategory(79, "Установка боковых заглушек ПК-14", "м.п.", 100, 10),
                    TypeCategory(80, "Карниз Слотт-Парсек", "м.п.", 2500, 10),
                    TypeCategory(81, "Запил + обработка углов Слотт-Парсек", "шт.", 750, 10),
                    TypeCategory(82, "Установка вентилятора", "шт.", 600, 10),
                    TypeCategory(83, "Установка магнитной решетки", "шт.", 700, 10),
                    TypeCategory(84, "Установка бруса/бокса", "м.п.", 500, 10),
                    TypeCategory(85, "Установка отбойника", "м.п.", 150, 10),
                    TypeCategory(86, "Установка разделителя", "м.п.", 250, 10),
                    TypeCategory(87, "Обвод трубы", "шт.", 500, 10),
                    TypeCategory(88, "Установка профиля на плитку", "м.п.", 150, 10),
                    TypeCategory(89, "Обход керамогранита", "м.п.", 650, 10),
                    TypeCategory(90, "Внешняя вклейка колец D<100", "шт.", 50, 10),
                    TypeCategory(91, "Внутренняя вклейка колец D>100", "шт.", 60, 10),
                    TypeCategory(92, "Оклейка стен защитной пленкой", "м.п.", 35, 10),
                    TypeCategory(93, "Сложная стыковка", "шт.", 200, 10),
                    TypeCategory(94, "Стыковка профилей между собой простая", "шт.", 200, 10),
                    TypeCategory(95, "Выезд бригады(ложный) по вине заказчика", "шт.", 200, 10),
                    TypeCategory(96, "Укладка пола картоном(оргалит)", "м2", 200, 10),
                    TypeCategory(97, "Расходные материалы", "шт.", 200, 10),
                    TypeCategory(98, "Работа на высоте свыше 3м 15% от стоимости", "%", 200, 10),
                    TypeCategory(99, "Работа на высоте свыше 5м 30% от стоимости", "%", 200, 10),

                    TypeCategory(
                        100,
                        "Профиль стеновой ал. Облегчённый с перфорацией 120гр/м",
                        "у.е.",
                        42,
                        11
                    ),
                    TypeCategory(101, "Профиль стеновой ал. Тяжелый с перфорацией 135гр/м", "у.е.", 70, 11),
                    TypeCategory(102, "Профиль потолочный ал. 2.5м.", "у.е.", 82, 11),
                    TypeCategory(103, "Профиль БФ 40 (брус ал.) гарпун/штапик 2м", "у.е.", 270, 11),
                    TypeCategory(104, "Профиль разделительный ал. 2.5м.", "у.е.", 181, 11),
                    TypeCategory(
                        105,
                        "Профиль алюминиевый для светодиодной ленты (комплект) прямой 2м",
                        "у.е.",
                        450,
                        11
                    ),
                    TypeCategory(
                        106,
                        "Профиль алюминиевый для светодиодной ленты (комплект) угловой 2м",
                        "у.е.",
                        550,
                        11
                    ),
                    TypeCategory(107, "Вставка TL-образная белая", "у.е.", 16, 11),
                    TypeCategory(108, "Вставка Т-обр. цветная", "у.е.", 19, 11),
                    TypeCategory(109, "Вставка F-обр.", "у.е.", 17, 11),

                    TypeCategory(110, "Профиль EuroKraab стеновой (2м)", "у.е.", 280, 12),
                    TypeCategory(111, "Профиль EuroKraab стеновой усиленный Strong (2м)", "у.е.", 300, 12),
                    TypeCategory(112, "Профиль AirKraab 2.0 вентиляционный 2м", "у.е.", 490, 12),
                    TypeCategory(113, "Профиль EuroKraab потолочный (2м)", "у.е.", 240, 12),
                    TypeCategory(114, "Бесщелевая система Kraab 4,0 (2 м)", "у.е.", 300, 12),

                    TypeCategory(115, "Профиль SLOTT 40 черный, белый 2м", "у.е.", 1170, 13),
                    TypeCategory(116, "Профиль SLOTT 80 черный, белый 2м", "у.е.", 1240, 13),
                    TypeCategory(117, "Профиль SLOTT 40 неокрашенный 2м", "у.е.", 970, 13),
                    TypeCategory(118, "Профиль SLOTT 80 неокрашенный 2м", "у.е.", 1020, 13),
                    TypeCategory(119, "Нишевые световые линии SLOTT 2,0 2м белый/чёрный", "у.е.", 2570, 13),
                    TypeCategory(120, "Нишевые световые линии SLOTT 2,0 2м чёрно-белый", "у.е.", 2890, 13),
                    TypeCategory(
                        121,
                        "Заглушка торцевая для световой линии SLOTT 2.0 белый/чёрный",
                        "у.е.",
                        620,
                        13
                    ),
                    TypeCategory(
                        122,
                        "Заглушка торцевая для световой линии SLOTT 2.0 чёрно-белая",
                        "у.е.",
                        670,
                        13
                    ),

                    TypeCategory(123, "Профиль теневой ЕвроФлекси стандарт чёрный 2м", "у.е.", 230, 15),
                    TypeCategory(124, "Профиль теневой ЕвроФлекси потолочный чёрный 2м", "у.е.", 195, 15),
                    TypeCategory(
                        125,
                        "Профиль разделительный теневой Флекси (Euro 04) 2м",
                        "у.е.",
                        360,
                        15
                    ),
                    TypeCategory(
                        126,
                        "Брус с подсветкой Флекси 4*4 BRUS 02 (ПФ0045) без рассеивателя (2м)",
                        "у.е.",
                        340,
                        15
                    ),
                    TypeCategory(
                        127,
                        "Профиль световые линии Flexy 30 мм без рассеивателя (2м) ПФ6838",
                        "у.е.",
                        355,
                        15
                    ),
                    TypeCategory(128, "Рассеиватель Флекси 30мм силикон для ПФ6838", "у.е.", 160, 15),
                    TypeCategory(
                        129,
                        "Профиль световые линии Flexy 50мм без рассеивателя",
                        "у.е.",
                        385,
                        15
                    ),
                    TypeCategory(130, "Рассеиватель Flexy 50мм", "у.е.", 200, 15),
                    TypeCategory(131, "Заглушка для линии Flexy 30мм/чёрный", "у.е.", 65, 15),

                    TypeCategory(
                        132,
                        "Профиль парящий Flexy без рассеивателя (ПФ8406) FLY02 2м неокрашенный",
                        "у.е.",
                        255,
                        16
                    ),
                    TypeCategory(
                        133,
                        "Профиль парящий Flexy без рассеивателя (ПФ8406) FLY02 2м белый/чёрный",
                        "у.е.",
                        295,
                        16
                    ),
                    TypeCategory(134, "Профиль LumFer BP02 mini черный 2м", "у.е.", 700, 16),
                    TypeCategory(135, "Профиль парящий ПК-6 2.5м.чёрный", "у.е.", 380, 16),
                    TypeCategory(136, "Профиль парящий Феникс (комплект) белый/чёрный", "у.е.", 2000, 16),
                    TypeCategory(137, "ПК15 (карниз двухрядный)", "у.е.", 650, 16),
                    TypeCategory(138, "ПК5/12(карниз трёхрядный/трёхрядный с гвоздиком)", "у.е.", 780, 16),

                    TypeCategory(139, "Профиль для ткани стеновой белый 2м", "у.е.", 60, 17),
                    TypeCategory(140, "Профиль для ткани стеновой чёрный 2м", "у.е.", 70, 17),
                    TypeCategory(141, "Профиль для ткани потолочный белый 2м", "у.е.", 60, 17),
                    TypeCategory(142, "Профиль для ткани потолочный чёрный 2м", "у.е.", 70, 17),
                    TypeCategory(143, "Профиль EuroSlott стеновой (старая версия) 2м", "у.е.", 340, 17),

                    TypeCategory(144, "ПЛАТФОРМА для монтажа люстры стандарт", "у.е.", 24, 18),
                    TypeCategory(145, "ПЛАТФОРМА для монтажа люстры усиленная ( фанера )", "у.е.", 120, 18),
                    TypeCategory(
                        146,
                        "ПЛАТФОРМА для монтажа т.с. универсальная 60-110мм Быстромонтаж",
                        "у.е.",
                        18,
                        18
                    ),
                    TypeCategory(147, "ПЛАТФОРМА для монтажа т.с. универсальная.60-120мм", "у.е.", 25, 18),
                    TypeCategory(148, "ПЛАТФОРМА для монтажа т.с. универсальная.60-120мм", "у.е.", 56, 18),
                    TypeCategory(149, "ПЛАТФОРМА для монтажа т.с. универсальная 165-225мм", "у.е.", 84, 18),
                    TypeCategory(
                        150,
                        "ПЛАТФОРМА для монтажа т.с. квадрат универсальная 50-90мм",
                        "у.е.",
                        31,
                        18
                    ),
                    TypeCategory(
                        151,
                        "ПЛАТФОРМА для монтажа т.с. квадрат универсальная 90-140мм",
                        "у.е.",
                        56,
                        18
                    ),
                    TypeCategory(152, "Лента светодиодная DumLait 11,6вт", "у.е.", 740, 18),
                    TypeCategory(153, "Трансформатор 300вт", "у.е.", 2240, 18),
                    TypeCategory(154, "Трансформатор 250вт", "у.е.", 1880, 18),
                    TypeCategory(155, "Трансформатор 200вт", "у.е.", 1760, 18),
                    TypeCategory(156, "Трансформатор 150вт", "у.е.", 1400, 18),
                    TypeCategory(157, "Трансформатор 100вт", "у.е.", 1100, 18),
                    TypeCategory(158, "Трансформатор 60вт", "у.е.", 690, 18),

                    TypeCategory(159, "Мат, сатин MSD (Classic) 340", "у.е.", 170, 19),
                    TypeCategory(160, "Мат, сатин MSD (Classic) 500", "у.е.", 240, 19),
                    TypeCategory(161, "Глянец MSD (Classic) 340", "у.е.", 270, 19),
                    TypeCategory(162, "Глянец MSD (Classic) 500", "у.е.", 340, 19),
                    TypeCategory(163, "Мат MSD (Premium) 340", "у.е.", 135, 19),
                    TypeCategory(164, "Мат MSD (Premium) 500", "у.е.", 170, 19),
                    TypeCategory(165, "Мат, сатин MSD (Premium) 320", "у.е.", 340, 19),
                    TypeCategory(166, "Мат, сатин MSD (Premium) 500", "у.е.", 440, 19),
                    TypeCategory(167, "Глянец MSD (Premium) 320", "у.е.", 380, 19),
                    TypeCategory(168, "Глянец MSD (Premium) 500", "у.е.", 520, 19),
                    TypeCategory(169, "Мат, сатин MSD (Evolution) 320", "у.е.", 145, 19),
                    TypeCategory(170, "Мат, сатин MSD (Evolution) 500", "у.е.", 185, 19),
                    TypeCategory(171, "Глянец MSD (Evolution) 340", "у.е.", 170, 19),
                    TypeCategory(172, "Глянец MSD (Evolution) 500", "у.е.", 210, 19),
                    TypeCategory(173, "ПВХ полотно в гарпуне менее 1кв.м", "шт.", 200, 19),
                    TypeCategory(174, "Доп угол.", "шт.", 40, 19),

                    TypeCategory(175, "Обвод трубы пластина 22, 27, 32мм", "у.е.", 24, 20),
                    TypeCategory(176, "Решётка вентиляционная с кольцом, 100мм", "у.е.", 42, 20),
                    TypeCategory(177, "Решётка вентиляционная с кольцом Белая 48мм", "у.е.", 21, 20),
                    TypeCategory(178, "Решётка вентиляционная с кольцом, 126мм", "у.е.", 56, 20),
                    TypeCategory(179, "Платформа крепления пожарного датчика/спота, 130мм", "у.е.", 17, 20),
                    TypeCategory(180, "ТЕРМОКОЛЬЦО 10-90мм", "у.е.", 7, 20),
                    TypeCategory(181, "ТЕРМОКОЛЬЦО 95-126мм", "у.е.", 12, 20),
                    TypeCategory(182, "ТЕРМОКОЛЬЦО 130-160мм", "у.е.", 17, 20),
                    TypeCategory(183, "ТЕРМОКОЛЬЦО 165-195мм", "у.е.", 19, 20),
                    TypeCategory(184, "ТЕРМОКОЛЬЦО 200-300мм", "у.е.", 36, 20),
                    TypeCategory(185, "ТЕРМОКВАДРАТ 50-105 (наружные размеры)", "у.е.", 20, 20),
                    TypeCategory(186, "ТЕРМОКВАДРАТ 110-250 (наружные размеры)", "у.е.", 35, 20),
                    TypeCategory(187, "Подвес прямой 0,9мм", "у.е.", 13, 20),
                    TypeCategory(188, "Подвес прямой 0,6мм", "у.е.", 10, 20),
                    TypeCategory(189, "Пластик листовой 3мм", "у.е.", 922, 20),
                    TypeCategory(190, "Пластик листовой 10мм", "у.е.", 3248, 20),
                    TypeCategory(191, "Алюминиевая композитная сендвич панель 3мм", "у.е.", 700, 20),
                    TypeCategory(192, "Уголок регулировочный FLEXY", "у.е.", 50, 20),
                    TypeCategory(193, "Уголок мебельный 100*125мм", "у.е.", 23, 20),
                    TypeCategory(194, "Уголок мебельный 150*200мм", "у.е.", 32, 20),
                    TypeCategory(195, "Уголок мебельный 250*300мм", "у.е.", 67, 20),
                    TypeCategory(196, "Труба Ал. 40*20*1.5 (2м)", "у.е.", 260, 20),
                    TypeCategory(197, "Труба Ал. 50*50*1.5 (2м)", "у.е.", 434, 20),
                    TypeCategory(198, "Труба Ал. 25*25*1.5 (2м)", "у.е.", 208, 20),
                    TypeCategory(199, "Труба Ал. 60*40*1.5 (2м)", "у.е.", 434, 20),
                    TypeCategory(200, "Саморез клоп (0,2кг)", "у.е.", 98, 20),
                    TypeCategory(201, "Брус 40*50 клееный 2м", "у.е.", 101, 20),
                    TypeCategory(202, "Провод ВВГ/НГ 2х1,5", "м.п.", 50, 20),
                    TypeCategory(203, "Ваго 3-х контактные", "шт.", 9, 20),
                    TypeCategory(204, "Гарпун FIX 2 (клиновой)", "м.п.", 17, 20),
                    TypeCategory(205, "Пленка укрывая 20м", "шт.", 420, 20),
                    TypeCategory(206, "КП/ПФ крючки для гардины+стопор в комплекте", "шт.", 150, 20),


                    )


                lifecycleScope.launch {
                    typeOfWork.forEach { dao.insertTypeOfWork(it) }
                    typeCategory.forEach { dao.insertTypeCategory(it) }
                    //dao.deleteClientById(12)
                    //dao.deleteClientById(13)
                    //dao.deleteClientById(14)
                    // dao.deleteEstimateByClientId(14)
                    //for (i in 0..12)  dao.deleteEstimate(estimate.get(i)) // удаляет по полю _id
                    //for (i in 5..11)  dao.deleteEstimate(estimate.get(i)) // ервый параметр НЕ включается в цикл. А последний параметр должен быть на 1 меньше чем _id в БД
                    //estimate.forEach { dao.insertEstimate(it) }
                    //clients.forEach { dao.insertClient(it) }

                    //  val typeOfWorkWithTypeCategory = dao.getTypeOfWorkWithTypeCategory(2)
                    //    val typeCategoryInEstimate = dao.getTypeCategoryInEstimate(2)
                    //    val clientsWithEstimate = dao.getClientWithEstimate(2)

                    /* val getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(1, 1)

                     val someList = arrayOf(getClientAndEstimate)
                     for (i in someList) {

                         Log.d("mytag", "listDB = ${i.joinToString(" || ")}")

                     }
         */
                }

            }
            Log.d("mytag", "Main Thread is Running")
        }


/*
        val estimate = listOf(

            Estimate(1, 1, 1, 3, "17.06.2022", "17.06.2022"),
            Estimate(2, 1, 2, 0, "17.06.2022", "17.06.2022"),
            Estimate(3, 1, 3, 0, "17.06.2022", "17.06.2022"),
            Estimate(4, 1, 4, 2, "17.06.2022", "17.06.2022"),
            Estimate(5, 1, 5, 0, "17.06.2022", "17.06.2022"),
            Estimate(6, 1, 6, 0, "17.06.2022", "17.06.2022"),
            Estimate(7, 2, 1, 1, "17.06.2022", "17.06.2022"),
            Estimate(8, 2, 2, 0, "17.06.2022", "17.06.2022"),
            Estimate(9, 2, 3, 4, "17.06.2022", "17.06.2022"),
            Estimate(10, 2, 4, 0, "17.06.2022", "17.06.2022"),
            Estimate(11, 2, 5, 0, "17.06.2022", "17.06.2022"),
            Estimate(12, 2, 6, 0, "17.06.2022", "17.06.2022"),
            Estimate(13, 3, 1, 1, "17.06.2022", "17.06.2022"),
            Estimate(14, 3, 2, 0, "17.06.2022", "17.06.2022"),
            Estimate(15, 3, 3, 0, "17.06.2022", "17.06.2022"),
            Estimate(16, 3, 4, 1, "17.06.2022", "17.06.2022"),
            Estimate(17, 3, 5, 0, "17.06.2022", "17.06.2022"),
            Estimate(18, 3, 6, 0, "17.06.2022", "17.06.2022"),
        )*/
/*
        val clients = listOf(

            Client(
                0,
                "Ваня",
                "Москва",
                "89774968939",
                IsNew = true,
                false,
                IsArchive = false,
                "17.06.2022",
                "17.06.2022"
            ),
            Client(
                0,
                "Петя",
                "Москва",
                "89774968939",
                IsNew = true,
                false,
                IsArchive = false,
                "17.06.2022",
                "17.06.2022"
            ),
            Client(
                0,
                "Шура",
                "Москва",
                "89774968939",
                IsNew = false,
                true,
                IsArchive = false,
                "17.06.2022",
                "17.06.2022"
            ),
        )
*/


    }

    private fun setNullClient(): Client {

        return Client(
            0, "", "", "", IsNew = false, IsPurchase = false, IsArchive = false,
            DateOfCreation = "",
            DateOfEditing = ""
        )

    }

    private fun getClientFromPreviousActivity(): Client {

        return intent.getSerializableExtra("ClientEntity") as Client
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart() called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("MainActivity", "onRestart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "onDestroy() called")
    }
}