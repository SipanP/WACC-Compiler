package backend.addressingmodes

import LANGUAGE
import backend.Language
import backend.enums.Register

class RegisterMode(val reg: Register) : AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "[$reg]"
            Language.X86_64 -> reg.toString()
        }
    }
}

class RegisterModeWithOffset(val reg: Register, val offset: Int, val preIndex: Boolean = false) :
    AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "[$reg${if (offset != 0) ", #${offset}" else ""}]${if (preIndex) "!" else ""}"
            Language.X86_64 -> "$offset($reg)"
        }
    }
}