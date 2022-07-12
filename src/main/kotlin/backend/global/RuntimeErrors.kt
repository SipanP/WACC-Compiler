package backend.global

import LANGUAGE
import backend.Language
import backend.ProgramState
import backend.addressingmodes.*
import backend.enums.Condition
import backend.enums.Register
import backend.instruction.*

class RuntimeErrors(private val globalVals: ProgramState.GlobalVals) {

    private val errors: HashMap<ErrorType, List<Instruction>> = LinkedHashMap()

    /**
     * Static objects used to generate the labels for error code blocks.
     */
    companion object {
        val checkArrayBoundsLabel = GeneralLabel("p_check_array_bounds")
        val divideZeroCheckLabel = GeneralLabel("p_check_divide_by_zero")
        val exitLabel = GeneralLabel("exit")
        val nullReferenceLabel = GeneralLabel("p_check_null_pointer")
        val throwOverflowErrorLabel = GeneralLabel("p_throw_overflow_error")
        val throwRuntimeErrorLabel = GeneralLabel("p_throw_runtime_error")
    }

    /**
     * Types of errors and the output message they return.
     */
    enum class ErrorType(val msg: String) {
        RUNTIME_ERROR("RUNTIME ERROR"), // This should never be printed as a message
        ARRAY_OUT_OF_BOUNDS("ARRAY OUT OF BOUNDS ERROR"), // This should never be printed as a message
        NULL_REFERENCE("NullReferenceError: dereference a null reference\\n\\0"),
        DIVIDE_BY_ZERO("DivideByZeroError: divide or modulo by zero\\n\\0"),
        LARGE_ARRAY_INDEX_OUT_OF_BOUNDS("ArrayIndexOutOfBoundsError: index too large\\n\\0"),
        NEGATIVE_ARRAY_INDEX_OUT_OF_BOUNDS("ArrayIndexOutOfBoundsError: negative index\\n\\0"),
        OVERFLOW_ERROR("OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0");

        override fun toString(): String {
            return msg
        }
    }

    /**
     * Iterates through the hashmap and translates all errors that are in the hashmap.
     */
    fun translate(): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        for (v in errors.values) {
            instructions.addAll(v)
        }
        return instructions
    }

    /**
     * Generate instructions for throwing runtime errors.
     */
    fun addThrowRuntimeError() {
        if (!errors.containsKey(ErrorType.RUNTIME_ERROR)) {
            if (LANGUAGE == Language.ARM) {
                errors[ErrorType.RUNTIME_ERROR] = listOf(
                    throwRuntimeErrorLabel,
                    BranchInstruction(
                        Condition.AL,
                        GeneralLabel(CallFunc.PRINT_STRING.toString()),
                        true
                    ),
                    // Exit code is -1
                    MoveInstruction(Condition.AL, Register.R0, ImmediateIntOperand(-1)),
                    BranchInstruction(Condition.AL, exitLabel, true)
                )
            } else {
                errors[ErrorType.RUNTIME_ERROR] = listOf(
                    throwRuntimeErrorLabel,
                    BranchInstruction(
                        Condition.AL,
                        GeneralLabel(CallFunc.PRINT_STRING.toString()),
                        true
                    ),
                    // Exit code is -1 to %rdi
                    MoveInstruction(Condition.AL, Register.R1, ImmediateIntOperand(-1)),
                    BranchInstruction(Condition.AL, exitLabel, true)
                )
            }
            globalVals.library.addCode(CallFunc.PRINT_STRING)
        }
    }

    /**
     * Generate instructions for throwing overflow errors.
     */
    fun addOverflowError() {
        if (!errors.containsKey(ErrorType.OVERFLOW_ERROR)) {
            val errorMsg =
                globalVals.dataDirective.addStringLabel(ErrorType.OVERFLOW_ERROR.toString())
            errors[ErrorType.OVERFLOW_ERROR] = listOf(
                throwOverflowErrorLabel,
                LoadInstruction(Condition.AL, ImmediateLabel(errorMsg), Register.R0),
                BranchInstruction(Condition.AL, throwRuntimeErrorLabel, true),
            )
        }
        addThrowRuntimeError()
    }

    /**
     * Generate instructions for null reference errors.
     */
    fun addNullReferenceCheck() {
        if (!errors.containsKey(ErrorType.NULL_REFERENCE)) {
            val errorMsgLabel =
                globalVals.dataDirective.addStringLabel(ErrorType.NULL_REFERENCE.toString())
            errors[ErrorType.NULL_REFERENCE] = listOf(
                nullReferenceLabel,
                PushInstruction(Register.LR),
                CompareInstruction(Register.R0, ImmediateIntOperand(0)),
                LoadInstruction(Condition.EQ, ImmediateLabel(errorMsgLabel), Register.R0),
                BranchInstruction(Condition.EQ, throwRuntimeErrorLabel, true),
                PopInstruction(Register.PC)
            )
        }
        addThrowRuntimeError()
    }

    /**
     * Generate instructions for divide by zero errors.
     */
    fun addDivideByZeroCheck() {
        if (!errors.containsKey(ErrorType.DIVIDE_BY_ZERO)) {
            val errorMsgLabel =
                globalVals.dataDirective.addStringLabel(ErrorType.DIVIDE_BY_ZERO.toString())
            errors[ErrorType.DIVIDE_BY_ZERO] = when (LANGUAGE) {
                Language.ARM -> listOf(
                    divideZeroCheckLabel,
                    PushInstruction(Register.LR),
                    CompareInstruction(Register.R1, ImmediateIntOperand(0)),
                    LoadInstruction(Condition.EQ, ImmediateLabel(errorMsgLabel), Register.R0),
                    BranchInstruction(Condition.EQ, throwRuntimeErrorLabel, true),
                    PopInstruction(Register.PC)
                )
                Language.X86_64 -> {
                    listOf(
                        divideZeroCheckLabel,
                        PushInstruction(Register.LR),
                        MoveInstruction(Condition.AL, Register.LR, RegisterMode(Register.SP)),
                        CompareInstruction(Register.R1, ImmediateIntOperand(0)),
                        LoadInstruction(Condition.AL, ImmediateLabel(errorMsgLabel), Register.R12),
                        CMoveInstruction(Condition.EQ, Register.R12, Register.R0),
                        BranchInstruction(Condition.EQ, throwRuntimeErrorLabel, false),
                        EndInstruction()
                    )
                }
            }
        }
        addThrowRuntimeError()
    }


    /**
     * Generate instructions for array bound errors.
     */
    fun addArrayBoundsCheck() {
        if (!errors.containsKey(ErrorType.ARRAY_OUT_OF_BOUNDS)) {
            val negativeMsgLabel =
                globalVals.dataDirective.addStringLabel(ErrorType.NEGATIVE_ARRAY_INDEX_OUT_OF_BOUNDS.toString())
            val tooLargeMsgLabel =
                globalVals.dataDirective.addStringLabel(ErrorType.LARGE_ARRAY_INDEX_OUT_OF_BOUNDS.toString())
            errors[ErrorType.DIVIDE_BY_ZERO] = when (LANGUAGE) {
                Language.ARM -> listOf(
                    checkArrayBoundsLabel,
                    PushInstruction(Register.LR),
                    CompareInstruction(Register.R0, ImmediateIntOperand(0)),
                    LoadInstruction(Condition.LT, ImmediateLabel(negativeMsgLabel), Register.R0),
                    BranchInstruction(Condition.LT, throwRuntimeErrorLabel, true),
                    LoadInstruction(Condition.AL, RegisterMode(Register.R1), Register.R1),
                    CompareInstruction(Register.R0, RegisterOperand(Register.R1)),
                    LoadInstruction(Condition.CS, ImmediateLabel(tooLargeMsgLabel), Register.R0),
                    BranchInstruction(Condition.CS, throwRuntimeErrorLabel, true),
                    PopInstruction(Register.PC)
                )
                Language.X86_64 -> listOf(
                    checkArrayBoundsLabel,
                    PushInstruction(Register.LR),
                    MoveInstruction(Condition.AL, Register.LR, RegisterMode(Register.SP)),
                    CompareInstruction(Register.R0, ImmediateIntOperand(0)),
                    LoadInstruction(Condition.AL, ImmediateLabel(negativeMsgLabel), Register.R12),
                    CMoveInstruction(Condition.LT, Register.R12, Register.R0),
                    BranchInstruction(Condition.LT, throwRuntimeErrorLabel, false),
                    LoadInstruction(
                        Condition.AL,
                        RegisterModeWithOffset(Register.R1, 0),
                        Register.R12
                    ),
                    CompareInstruction(Register.R0, RegisterOperand(Register.R12)),
                    LoadInstruction(Condition.AL, ImmediateLabel(tooLargeMsgLabel), Register.R12),
                    CMoveInstruction(Condition.CS, Register.R12, Register.R0),
                    BranchInstruction(Condition.CS, throwRuntimeErrorLabel, false),
                    EndInstruction()
                )
            }
            addThrowRuntimeError()
        }
    }
}