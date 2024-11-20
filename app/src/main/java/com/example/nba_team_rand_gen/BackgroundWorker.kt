import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class BackgroundWorker(private val context: Context) {

    fun executeExtractPLayer(teamId: Int, playerId: Int) {
        val extractUrl = "http://10.40.142.162/extract-player.php"

        // Launching a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("BackgroundWorker", "Sending request to: $extractUrl")
                Log.d("BackgroundWorker", "Sending teamId: $teamId, playerId: $playerId")
                val result = performRequest(extractUrl, teamId, playerId)

                Log.d("BackgroundWorker", "Response: $result")
                // Switching to Main thread to show result
                withContext(Dispatchers.Main) {
                    showDialog("Player Stats", result)
                }
            } catch (e: Exception) {
                Log.e("BackgroundWorker", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    showDialog("Error", e.message ?: "Unknown error")
                }
            }
        }
    }

    fun executeNumTeams (teamId: Int, callback: (Int) -> Unit) {
        val extractUrl = "http://10.40.142.162/number-of-players.php"
        var nr = 0

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("BackgroundWorker", "Sending request to: $extractUrl")
                Log.d("BackgroundWorker", "Sending teamId: $teamId")
                val result = performRequest(extractUrl, teamId, 0).toIntOrNull()
                // Switching to Main thread to show result
                withContext(Dispatchers.Main) {
                    if (result != null) {
                        callback(result)
                    }
                    else showDialog("Error", "Team players not found")
                }
            } catch (e: Exception) {
                Log.e("BackgroundWorker", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    showDialog("Error", e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun performRequest(urlString: String, teamId: Int, playerId: Int): String {
        val url = URL(urlString)
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.doOutput = true
        httpURLConnection.doInput = true
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        // Write data to output stream
        val outputStream = httpURLConnection.outputStream
        val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))

        val postData = URLEncoder.encode("team_id", "UTF-8") + "=" + URLEncoder.encode(teamId.toString(), "UTF-8") + "&" +
                URLEncoder.encode("player_id", "UTF-8") + "=" + URLEncoder.encode(playerId.toString(), "UTF-8")
        Log.d("BackgroundWorker", "Sending postData: $postData")

        bufferedWriter.write(postData)
        bufferedWriter.flush()
        bufferedWriter.close()
        outputStream.close()

        // Check the response code to verify connection
        val responseCode = httpURLConnection.responseCode
        Log.d("BackgroundWorker", "Response Code: $responseCode")

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read response only if the connection was successful
            val inputStream = httpURLConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "iso-8859-1"))
            val result = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                result.append(line)
            }

            bufferedReader.close()
            inputStream.close()
            result.toString()
        } else {
            "Error: Unable to connect, Response Code: $responseCode"
        }.also {
            httpURLConnection.disconnect()
        }
    }


    private fun showDialog(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.show()
    }
}