package backend.addressingmodes

import LANGUAGE
import backend.Language

class ImmediateCharOperand(val char: Char) : AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> {
                val charStr: String = when (char) {
                    0.toChar() -> "0"
                    12.toChar() -> "\\f"
                    '\b' -> "\\b"
                    '\t' -> "\\t"
                    '\n' -> "\\n"
                    '\r' -> "\\r"
                    '\"' -> "\\\""
                    '\'' -> "\\\'"
                    '\\' -> "\\\\"
                    else -> char.toString()
                }
                "#'${charStr}'"
            }
            Language.X86_64 -> {
                "$${char.code}"
            }
        }
    }
}