package minesweeper
import java.lang.IndexOutOfBoundsException
import kotlin.random.Random
import kotlin.system.exitProcess

fun createMinefield(size: Int): MutableList<MutableList<String>> {
    return  MutableList(size) { MutableList(size) {"."} }
}

fun addMines(amount: Int, field: MutableList<MutableList<String>>) {
    var minesLeft = amount
    while (minesLeft > 0) {
        val randomRow = Random.nextInt(0, field.size)
        val randomColumn = Random.nextInt(0, field.size)
        if (field[randomRow][randomColumn] == ".") {
            field[randomRow][randomColumn] = "X"
            minesLeft--
        }
    }
}

fun printWithFrame(field: MutableList<MutableList<String>>) {
    print(" │")
    for (i in 1..9) {
        print(i)
    }
    println("│")
    println("—│—————————│")
    for (i in 0 until 9){
        println("${i + 1}│" + field[i].joinToString("") + "│")
    }
    println("—│—————————│")
}


fun calculateMinesAround(field: MutableList<MutableList<String>>, size:Int): MutableList<MutableList<String>>  {
    for (i in 0 until size) {
        for (j in 0 until size) {
            var minesAround = 0
            for (k in i-1..i+1) {
                for (l in j-1..j+1) {
                    if (k == i && l == j) {
                        continue
                    }
                    try {
                        if (field[k][l] == "X") {
                            minesAround ++
                        }
                    } catch(e: IndexOutOfBoundsException) {
                        continue
                    }
                }
            }
            if (minesAround != 0 && field[i][j] != "X") {
                field[i][j] = minesAround.toString()
            }
        }
    }
    return field
}

fun markMine(usersField: MutableList<MutableList<String>>, x: Int, y: Int) {
    if (usersField[x][y] == ".") {
        usersField[x][y] = "*"
    } else {
        if (usersField[x][y] == "*") {
            usersField[x][y] = "."
        }
    }
    printWithFrame(usersField)
}


fun checkAllMarks(field: MutableList<MutableList<String>>, mines: Int, fieldWithMines: MutableList<MutableList<String>>): Boolean {
    var markedMines = 0
    var wrongMarks = 0
    for (i in 0 until 9) {
        for (j in 0 until 9) {
            if (field[i][j] == "*") {
                if (fieldWithMines[i][j] == "X") {
                    markedMines ++
                } else {
                    wrongMarks ++
                }
            }
        }
    }
    return wrongMarks == 0 && markedMines == mines
}

fun freeCell(fieldWithDigits: MutableList<MutableList<String>>, usersField: MutableList<MutableList<String>>, x: Int, y: Int) {
    if (fieldWithDigits[x][y] == ".") {
        usersField[x][y] = "/"
    } else if (fieldWithDigits[x][y].first().isDigit()) {
        usersField[x][y] = fieldWithDigits[x][y]
        return
    }
    for (i in x-1..x+1) {
        for (j in y-1..y+1) {
            if (i == x && j == y) {
                continue
            }
            try {
                if (usersField[i][j] == "/") {
                    continue
                } else {
                    if (fieldWithDigits[i][j].first().isDigit()) {
                        usersField[i][j] = fieldWithDigits[i][j]
                    }
                    if (fieldWithDigits[i][j] == ".") {
                        freeCell(fieldWithDigits, usersField, i, j)
                    }
                }
            } catch(e: IndexOutOfBoundsException) {
                continue
            }
        }
    }
}

fun checkAllFree(usersField: MutableList<MutableList<String>>, fieldWithDigits: MutableList<MutableList<String>>): Boolean {
    var answer = true
    for (x in 0 until usersField.size) {
        for (y in 0 until usersField.size) {
            if (fieldWithDigits[x][y] == ".") {
                if (usersField[x][y] != "/") {
                    answer = false
                }
            }

        }
    }
    return answer
}



fun main() {
    val fieldWithMines = createMinefield(9)
    println("How many mines do you want on the field?")
    val numberOfMines = readln().toInt()
    addMines(numberOfMines, fieldWithMines)
    val fieldWithDigits = calculateMinesAround(fieldWithMines, fieldWithMines.size)
    val usersField = MutableList(9){ MutableList(9) {"."} }
    printWithFrame(usersField)
    while (true) {
        println("Set/unset mine marks or claim a cell as free:")
        val answer = readln().split(" ")
        val y = answer[0].toInt() - 1
        val x = answer[1].toInt() - 1
        when (answer.last()) {
            "free" ->  {
                if (fieldWithDigits[x][y] == "X") {
                    printWithFrame(fieldWithDigits)
                    println("You stepped on a mine and failed!")
                    exitProcess(1)
                } else {
                    freeCell(fieldWithDigits, usersField, x, y)
                    printWithFrame(usersField)
                }
            }
            "mine" -> markMine(usersField, x, y)
        }
        if (checkAllMarks(usersField, numberOfMines, fieldWithMines) || checkAllFree(usersField, fieldWithDigits)) {
            println("Congratulations! You found all the mines!")
            break
        }
    }
}
