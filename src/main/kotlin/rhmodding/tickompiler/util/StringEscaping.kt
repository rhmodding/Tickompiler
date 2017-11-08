package rhmodding.tickompiler.util

fun String.escape(): String {
	return replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
}

fun String.unescape(): String {
	return replace("\\n", "\n").replace("\\\\(.)".toRegex(), {it.groupValues[1]})
}