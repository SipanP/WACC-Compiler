package backend.global

import LANGUAGE
import backend.Language
import backend.ProgramState
import backend.addressingmodes.*
import backend.enums.Condition
import backend.enums.Register
import backend.global.RuntimeErrors.Companion.throwRuntimeErrorLabel
import backend.instruction.*


/**
 * Library functions that can be branched to from the assembly code.
 */
enum class Funcs {
    FREE,
    PRINTF,
    SCANF,
    FFLUSH,
    MALLOC,
    PUTS,
    PUTCHAR;

    override fun toString(): String {
        return super.toString().lowercase()
    }
}

/**
 * The labels for the functions that set up and branch to library functions.
 */
enum class CallFunc {
    READ_INT,
    READ_CHAR,
    PRINT_INT,
    PRINT_BOOL,
    PRINT_STRING,
    PRINT_REFERENCE,
    PRINT_LN,
    FREE_ARRAY,
    FREE_PAIR;

    override fun toString(): String {
        return "p_${super.toString().lowercase()}"
    }
}


class Library(private val globalVals: ProgramState.GlobalVals) {
    private val calls: HashMap<CallFunc, List<Instruction>> = LinkedHashMap()


    /**
     * Add code for the specific callFunc into the 'calls' hashmap.
     * We only generate the block of code once for each function.
     */
    fun addCode(callFunc: CallFunc) {
        if (calls.containsKey(callFunc)) {
            return
        }
        val instructions = mutableListOf<Instruction>()
        val callLabel = GeneralLabel(callFunc.toString())
        instructions.add(callLabel)
        instructions.add(PushInstruction(Register.LR))
        // mov %rsp, %rbp
        if (LANGUAGE == Language.X86_64) {
            instructions.add(MoveInstruction(Condition.AL, Register.LR, RegisterMode(Register.SP)))
        }

        val body = when (callFunc) {
            CallFunc.READ_INT, CallFunc.READ_CHAR -> generateReadCall(callFunc)
            CallFunc.PRINT_INT -> generatePrintIntCall()
            CallFunc.PRINT_BOOL -> generatePrintBoolCall()
            CallFunc.PRINT_STRING -> generatePrintStringCall()
            CallFunc.PRINT_REFERENCE -> generatePrintReferenceCall()
            CallFunc.PRINT_LN -> generatePrintLnCall()
            CallFunc.FREE_PAIR -> generateFreePairCall()
            CallFunc.FREE_ARRAY -> generateFreeArrayCall()
        }
        instructions.addAll(body)
        instructions.add(EndInstruction())
        calls[callFunc] = instructions
    }

    /**
     * Translate the blocks of codes for the functions that had been added
     * to the hashmap.
     */
    fun translate(): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        for (v in calls.values) {
            instructions.addAll(v)
        }
        return instructions
    }


    /**
     * Generate instructions for the read call.
     */
    private fun generateReadCall(call: CallFunc): List<Instruction> {

        val stringType: String = when (call) {
            CallFunc.READ_INT -> {
                "%d\\0"
            }
            CallFunc.READ_CHAR -> {
                " %c\\0"
            }
            else -> {
                "UNKNOWN TYPE"
            }
        }

        val stringTypeLabel = globalVals.dataDirective.addStringLabel(stringType)

        return if (LANGUAGE == Language.ARM) {
            listOf(
                MoveInstruction(Condition.AL, Register.R1, RegisterOperand(Register.R0)),
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R0),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R0,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.SCANF.toString()), true)
            )
        } else {
            listOf(
                MoveInstruction(Condition.AL, Register.R2, RegisterOperand(Register.R0)),
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R1),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R1,
                    Register.R1,
                    ImmediateIntOperand(4)
                ),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.SCANF.toString()), true)
            )
        }
    }

    /**
     * Generate instructions for print integer call.
     */
    private fun generatePrintIntCall(): List<Instruction> {
        val stringTypeLabel = globalVals.dataDirective.addStringLabel("%d\\0")

        return printCallHelper(stringTypeLabel)
    }

    /**
     * Helper function for printing integers or references.
     */
    private fun printCallHelper(stringTypeLabel: String): List<Instruction> {
        return if (LANGUAGE == Language.ARM) {
            listOf(
                MoveInstruction(Condition.AL, Register.R1, RegisterOperand(Register.R0)),
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R0),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R0,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PRINTF.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.FFLUSH.toString()), true)
            )
        } else {
            listOf(
                MoveInstruction(Condition.AL, Register.R2, RegisterOperand(Register.R0)),
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R1),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R1,
                    Register.R1,
                    ImmediateIntOperand(4)
                ),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PRINTF.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0))
            )
        }
    }

    /**
     * Generate instructions for printing reference call.
     */
    private fun generatePrintReferenceCall(): List<Instruction> {
        val stringTypeLabel = globalVals.dataDirective.addStringLabel("%p\\0")

        return printCallHelper(stringTypeLabel)
    }

    /**
     * Generate instructions for printing a boolean call.
     */
    private fun generatePrintBoolCall(): List<Instruction> {
        val trueString = "true\\0"
        val falseString = "false\\0"
        val trueLabel = globalVals.dataDirective.addStringLabel(trueString)
        val falseLabel = globalVals.dataDirective.addStringLabel(falseString)

        return if (LANGUAGE == Language.ARM) {
            listOf(
                CompareInstruction(Register.R0, ImmediateIntOperand(0)),
                LoadInstruction(Condition.NE, ImmediateLabel(trueLabel), Register.R0),
                LoadInstruction(Condition.EQ, ImmediateLabel(falseLabel), Register.R0),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R0,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PRINTF.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.FFLUSH.toString()), true)
            )
        } else {
            listOf(
                CompareInstruction(Register.R0, ImmediateIntOperand(0)),
                MoveInstruction(Condition.AL, Register.R0, ImmediateLabel(trueLabel)),
                CMoveInstruction(Condition.NE, Register.R0, Register.R1),
                MoveInstruction(Condition.AL, Register.R0, ImmediateLabel(falseLabel)),
                CMoveInstruction(Condition.EQ, Register.R0, Register.R1),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R1,
                    Register.R1,
                    ImmediateIntOperand(4)
                ),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PRINTF.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0))
            )
        }
    }

    /**
     * Generate instructions for printing string call.
     */
    private fun generatePrintStringCall(): List<Instruction> {
        val stringTypeLabel = globalVals.dataDirective.addStringLabel("%.*s\\0")

        return if (LANGUAGE == Language.ARM) {
            listOf(
                LoadInstruction(Condition.AL, RegisterMode(Register.R0), Register.R1),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R2,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R0),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R0,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PRINTF.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.FFLUSH.toString()), true)
            )
        } else {
            listOf(
                LoadInstruction(Condition.AL, RegisterMode(Register.R0), Register.R2),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R3,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R1),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R1,
                    Register.R1,
                    ImmediateIntOperand(4)
                ),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PRINTF.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0))
            )
        }
    }

    /**
     * Generate instructions for printing a line call.
     */
    private fun generatePrintLnCall(): List<Instruction> {
        val stringTypeLabel = globalVals.dataDirective.addStringLabel("\\0")
        return if (LANGUAGE == Language.ARM) {
            listOf(
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R0),
                ArithmeticInstruction(
                    ArithmeticInstrType.ADD,
                    Register.R0,
                    Register.R0,
                    ImmediateIntOperand(4)
                ),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PUTS.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0)),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.FFLUSH.toString()), true)
            )
        } else {
            listOf(
                LoadInstruction(Condition.AL, ImmediateLabel(stringTypeLabel), Register.R0),
                BranchInstruction(Condition.AL, GeneralLabel(Funcs.PUTS.toString()), true),
                MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(0))
            )
        }
    }

    /**
     * Generate instructions for free pair call.
     */
    private fun generateFreePairCall(): List<Instruction> {
        val label =
            globalVals.dataDirective.addStringLabel(RuntimeErrors.ErrorType.NULL_REFERENCE.toString())

        val instructions = listOf(
            CompareInstruction(Register.R0, ImmediateIntOperand(0)),
            LoadInstruction(Condition.EQ, ImmediateLabel(label), Register.R0),
            BranchInstruction(Condition.EQ, throwRuntimeErrorLabel, false),
            PushInstruction(Register.R0),
            LoadInstruction(Condition.AL, RegisterMode(Register.R0), Register.R0),
            BranchInstruction(Condition.AL, GeneralLabel(Funcs.FREE.toString()), true),
            LoadInstruction(Condition.AL, RegisterMode(Register.SP), Register.R0),
            LoadInstruction(Condition.AL, RegisterModeWithOffset(Register.R0, 4), Register.R0),
            BranchInstruction(Condition.AL, GeneralLabel(Funcs.FREE.toString()), true),
            PopInstruction(Register.R0),
            BranchInstruction(Condition.AL, GeneralLabel(Funcs.FREE.toString()), true)
        )
        globalVals.runtimeErrors.addThrowRuntimeError()
        return instructions
    }

    /**
     * Generate instructions for free array call.
     */
    private fun generateFreeArrayCall(): List<Instruction> {
        val errorMessage = RuntimeErrors.ErrorType.NULL_REFERENCE.toString()
        val errorLabel = globalVals.dataDirective.addStringLabel(errorMessage)

        return listOf(
            CompareInstruction(Register.R0, ImmediateIntOperand(0)),
            LoadInstruction(Condition.EQ, ImmediateLabel(errorLabel), Register.R0),
            BranchInstruction(Condition.AL, GeneralLabel(Funcs.FREE.toString()), true)
        )
    }


}