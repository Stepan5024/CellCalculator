package bokarev.st.listapptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val list = arrayListOf("3", "8", "4", "1", "9")
        for ((index, element) in list.withIndex()){
            println("$index: $element")
        }
        list.add(1, "90")

        for ((index, element) in list.withIndex()){
            println("$index: $element")
        }

        var myLovelyCats = listOf( // Переменная Мои ненаглядные коты
            // Это неизменяемый список!!!
            "Мурзик", // первый кот в списке
            "Рыжик",
            "Барсик", // последний кот в списке
        )
        myLovelyCats = myLovelyCats + "Васька" - "Рыжик"
        println(myLovelyCats)

        myLovelyCats.forEach { element ->
            println(element)
        }

// сокращённый вариант с использованием ключевого слова it
        myLovelyCats.forEach { println(it) }
// или
        myLovelyCats.forEach(::println)

        //mutable - зменяемый список
        val myLovelyCats2 = mutableListOf( // Переменная Мои ненаглядные коты
            "Мурзик", // первый кот в списке
            "Рыжик",
            "Барсик", // последний кот в списке
        )

        myLovelyCats2.add("Васька")
        myLovelyCats2.remove("Рыжик")

        println(myLovelyCats2)

        listOf("Барсик", "Рыжик", "Васька").forEachIndexed { index, value
            ->
            println("В позиции $index содержится $value")
        }


       // takeIf() будет выбирать элементы, если выполняется условие (предикат).


// Не выбирать элементы, если список содержит Пушистика
        var cats = listOf("Рыжик", "Мурзик", "Барсик", "Васька")
        cats.takeIf {
            it.contains("Пушистик")
        }.apply {
            this?.forEach{
                println("$it")
            }
        }

        println(listOf(1, 2, 3, 4, 5).indexOf(4)) // 3



// Результат
      /*  0: 3
        1: 8
        2: 4
        3: 1
        4: 9*/
        /*Изменять данные можно только в изменяемых списках. Но если у вас есть в наличии неизменяемый список, то его можно сконвертировать в изменяемый через специальный метод toMutableList(). При этом будет создан новый список.
*/

        var mutableNames = listOf("Рыжик", "Мурзик", "Барсик", "Васька").toMutableList() // превращаем в изменяемый список
        mutableNames.add("Рыжик") // добавляем новое имя в конец списка
        println(mutableNames::class.java)
        println(mutableNames[3])


       // А можно сразу создать изменяемый список нужного типа через mutableListOf<T>(). Также есть перегруженная версия без указания нужного типа - mutableListOf().


        val mutableListNames: MutableList<String> =
            mutableListOf<String>("Барсик", "Мурзик", "Васька")
        mutableListNames.add("Рыжик") // добавляем в конец списка
        mutableListNames.removeAt(1) // удаляем второй элемент
        mutableListNames[0] = "Пушок" // заменяем первый элемент через присваивание
// mutableListNames.set(0, "Пушок") // другой вариант
        mutableListNames.add(1, "Begemoth") // вставляем во вторую позицию

// изменяемый список из разных типов
        val mutableListMixed = mutableListOf("Кот", "Собака", 5, 5.27, 'F')

        //Удалить элемент из изменяемого списка можно через removeIf(), указав лямбда-выражение в качестве условия.


         cats = mutableListOf("Мурзик", "Барсик", "Рыжик")
        cats.removeIf{it.contains("у")}
        println(cats)

// выводится: [Барсик, Рыжик]



    }
}