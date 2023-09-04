import com.justai.jaicf.builder.Scenario

val bullsAndCowsScenario = Scenario {

    state("start") {
        activators {
            regex("/start")
        }

        action {
            // Генерируем тайные числа
            val secretNumber = generateSecretNumber()
            context.session["secretNumber"] = secretNumber
            reactions.say("Здравствуйте! Давайте сыграем в игру «Быки и коровы». Я задумаю 4-значное число с неповторяющимися цифрами. Угаданная цифра с верной позицией - это бык, а просто угаданная цифра с неверной позицией - это корова. Теперь попробуйте угадать!")
        }
    }

    state("guessNumber") {
        activators {
            regex("[0-9]{4}")
        }

        action {
            val secretNumber = context.session["secretNumber"] as String
            val userGuess = request.input

            // Проверяем угаданные числа
            val result = checkGuess(secretNumber, userGuess)

            if (result.bulls == 4) {
                reactions.say("Поздравляю! Вы угадали число $secretNumber. Игра окончена.")
                reactions.goBack()
            } else {
                reactions.say("Ваш результат: ${result.bulls} быка и ${result.cows} коровы. Попробуйте еще раз.")
            }
        }
    }

    fallback {
        reactions.sayRandom(
            "Пожалуйста, введите 4-значное число с неповторяющимися цифрами.",
            "Извините, я вас не понял, напишите 4-значное число"
        )
    }
}

data class GuessResult(val bulls: Int, val cows: Int)

fun generateSecretNumber(): String {
    val digits = (0..9).toList().shuffled()
    return digits.take(4).joinToString("")
}

fun checkGuess(secretNumber: String, userGuess: String): GuessResult {
    var bulls = 0
    var cows = 0

    for (i in secretNumber.indices) {
        if (secretNumber[i] == userGuess[i]) {
            bulls++
        } else if (secretNumber.contains(userGuess[i])) {
            cows++
        }
    }

    return GuessResult(bulls, cows)
}
