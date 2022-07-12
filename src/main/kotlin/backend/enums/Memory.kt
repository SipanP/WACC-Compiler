package backend.enums

/**
 * An enum for memory type sizes that can be associated with instruction
 */
enum class Memory {
    B,  // Unsigned Byte
    SB, // Signed Byte
    W, // Word (2 Bytes)
    L, // Double word (4 Bytes)
    Q; // 8 bytes

    fun getRegType(reg: Register): String {
        return when (this) {
            B, SB -> reg.to8Byte()
            W -> reg.to16Byte()
            L -> reg.to32Byte()
            Q -> reg.toString()
        }
    }
}