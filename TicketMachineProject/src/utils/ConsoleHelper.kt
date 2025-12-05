package utils

object ConsoleHelper {

    fun readLineTrimmed(prompt: String): String {
        print(prompt)
        return readLine()?.trim() ?: ""
    }

    fun readDouble(prompt: String): Double? {
        print(prompt)
        return readLine()?.trim()?.toDoubleOrNull()
    }

    fun readInt(prompt: String): Int? {
        print(prompt)
        return readLine()?.trim()?.toIntOrNull()
    }
}
