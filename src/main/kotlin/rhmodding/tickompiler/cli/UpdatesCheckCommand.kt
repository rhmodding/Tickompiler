package rhmodding.tickompiler.cli

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import picocli.CommandLine
import rhmodding.tickompiler.Tickompiler
import rhmodding.tickompiler.Tickompiler.GITHUB
import rhmodding.tickompiler.util.Version
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@CommandLine.Command(name = "updates", description = ["Check GitHub for an update to Tickompiler."],
        mixinStandardHelpOptions = true)
class UpdatesCheckCommand : Runnable {

    override fun run() {
        println("Checking for updates...")
        val path = URL("https://api.github.com/repos/${GITHUB.replace("https://github.com/", "")}/releases/latest")
        val con = path.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("Accept", "application/vnd.github.v3+json")
        if (con.responseCode != 200) {
            println("Failed to get version info: got non-200 response code (${con.responseCode} ${con.responseMessage})")
        } else {
            val inputStream = con.inputStream
            val bufferedReader = inputStream.bufferedReader()
            val content = bufferedReader.readText()
            bufferedReader.close()
            con.disconnect()
            val release = Gson().fromJson<Release>(content)
            val releaseVersion: Version? = Version.fromStringOrNull(release.tag_name ?: "")
            if (releaseVersion == null) {
                println("Failed to get version info: release version is null? (${release.tag_name})")
            } else {
                if (releaseVersion > Tickompiler.VERSION) {
                    println("A new ${if (release.prerelease) "PRE-RELEASE " else ""}version is available: $releaseVersion")
                    val publishDate: LocalDateTime? = try {
                        ZonedDateTime.parse(release.published_at, DateTimeFormatter.ISO_DATE_TIME)?.withZoneSameInstant(ZoneId.systemDefault())?.toLocalDateTime()
                    } catch (ignored: Exception) { null }
                    println("Published on ${publishDate?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss")) ?: release.published_at}")
                    println(release.html_url)
                } else {
                    println("No new version found.")
                }
            }
        }
    }

    @Suppress("PropertyName")
    class Release {
        var html_url: String? = ""
        var tag_name: String? = ""
        var name: String? = ""
        var published_at: String? = ""
        var prerelease: Boolean = false
    }

}