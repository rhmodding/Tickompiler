package chrislo27.tickompiler.output


object Outputter {

    fun toHexBytes(array: ByteArray, group4: Boolean): String {
        val sb = StringBuilder()
        var count = 0
        val grouping = if (group4) 4 else 1
        array.forEach {
            sb.append(String.format("%02x", it))
            if (++count >= grouping) {
                count = 0
                sb.append(" ")
            }
        }

        return sb.toString().toUpperCase()
    }

}