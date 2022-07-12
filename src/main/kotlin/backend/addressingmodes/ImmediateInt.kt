package backend.addressingmodes

import LANGUAGE
import backend.Language

class ImmediateInt(val num: Int) : AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "=$num"
            Language.X86_64 -> "$$num"
        }
    }
}