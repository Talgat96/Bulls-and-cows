import com.justai.jaicf.builder.Scenario

val bullsAndCowsScenario = Scenario {

    state("start") {
        action {
            // Генерация тайного числа
            val secretNumber = generateSecretNumber()
            context.session["secretNumber"] = secretNumber
            reactions.say("Я задумал 4-значное число с неповторяющимися цифрами. Попробуйте угадать!")
        }
    }

    state("guessNumber") {
        activators {
            regex("[0-9]{4}")
        }

        action {
            val secretNumber = context.session["secretNumber"] as String
            val userGuess = request.input

            // Проверка угаданного числа
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
        reactions.say("Пожалуйста, введите 4-значное число с неповторяющимися цифрамихмырь.")
    }
}

// Генерация тайного числа
fun generateSecretNumber(): String {
    val digits = (0..9).toList().shuffled()
    return digits.take(4).joinToString("")
}

// Проверка угаданного числа
data class GuessResult(val bulls: Int, val cows: Int)

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
