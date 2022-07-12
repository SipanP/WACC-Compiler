package backend.addressingmodes

import LANGUAGE
import backend.Language

class ImmediateBoolOperand(val boolVal: Boolean) : AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "#${if (boolVal) 1 else 0}"
            Language.X86_64 -> "$${if (boolVal) 1 else 0}"
        }
    }
}