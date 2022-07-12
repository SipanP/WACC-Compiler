package backend.enums

import LANGUAGE
import backend.Language


enum class Register {

    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12,
    SP, // Stack Pointer
    LR, // Link register
    PC, // Program Counter

    NONE;

    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> name.lowercase()
            Language.X86_64 -> {
                "%" +
                        when (this) {
                            R0 -> "rax"
                            R1 -> "rdi"
                            R2 -> "rsi"
                            R3 -> "rdx"
                            R4 -> "rdi"
                            R5 -> "rsi"
                            R6 -> "rdx"
                            R7 -> "rcx"
                            R8 -> "r8"
                            R9 -> "r9"
                            R10 -> "r12"
                            R11 -> "r13"
                            R12 -> "r14"
                            SP -> "rsp"
                            LR -> "rbp"
                            PC -> "rbp"
                            NONE -> ""
                        }
            }
        }
    }

    fun to8Byte(): String {
        return "%" +
                when (this) {
                    R0 -> "al"
                    R1 -> "dil"
                    R2 -> "sil"
                    R3 -> "dl"
                    R4 -> "dil"
                    R5 -> "sil"
                    R6 -> "dl"
                    R7 -> "cl"
                    R8 -> "r8b"
                    R9 -> "r9b"
                    R10 -> "r12b"
                    R11 -> "r13b"
                    R12 -> "r14b"
                    SP -> "spl"
                    LR -> "bpl"
                    PC -> "bpl"
                    NONE -> ""
                }
    }

    fun to16Byte(): String {
        return "%" +
                when (this) {
                    R0 -> "ax"
                    R1 -> "di"
                    R2 -> "si"
                    R3 -> "dx"
                    R4 -> "di"
                    R5 -> "si"
                    R6 -> "dx"
                    R7 -> "cx"
                    R8 -> "r8w"
                    R9 -> "r9w"
                    R10 -> "r12w"
                    R11 -> "r13w"
                    R12 -> "r14w"
                    SP -> "sp"
                    LR -> "bp"
                    PC -> "bp"
                    NONE -> ""
                }
    }

    fun to32Byte(): String {
        return "%" +
                when (this) {
                    R0 -> "eax"
                    R1 -> "edi"
                    R2 -> "esi"
                    R3 -> "edx"
                    R4 -> "edi"
                    R5 -> "esi"
                    R6 -> "edx"
                    R7 -> "ecx"
                    R8 -> "r8d"
                    R9 -> "r9d"
                    R10 -> "r12d"
                    R11 -> "r13d"
                    R12 -> "r14d"
                    SP -> "esp"
                    LR -> "ebp"
                    PC -> "ebp"
                    NONE -> ""
                }
    }
}