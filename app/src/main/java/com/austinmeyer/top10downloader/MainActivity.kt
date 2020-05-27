package com.austinmeyer.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
            """.trimIndent()
    }
}

//class Car (val cylinders: Int, val transmission: String, val model: String) {
//
//
//
//}

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val downloadData by lazy {  DownloadData(this, xmlListView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val modelsUserHates = listOf<String>("Honda", "Toyota")
//
//        val goodCars = listOf<Car>(
//            Car(8, "Auto", "Mustang"),
//            Car(8,"Manual","Challenger"),
//            Car(4, "Auto", "Honda"),
//            Car(6, "Auto", "GTR")
//        ).filter { !modelsUserHates.contains(it.model)  }
//        val result2 = listOf<String>(challenger, mustang).filter { it > 4 && it != 7 }
//
//        helloTxt.setOnClickListener(View.OnClickListener {  })

        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate Called")
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "onCreate: done")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"
            var propContext : Context  by Delegates.notNull()
            var propListView : ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplications = ParseApplications()
                parseApplications.parse(result)

                val feedAdapter = FeedAdapter(propContext, R.layout.list_record, parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }
            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()

                //=======================================================
                //All the code below gets replaced by the one line above
                //======================================================
//                val xmlResult = StringBuilder()
//
//                try {
//                    val url = URL(urlPath)
//                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//                    val response = connection.responseCode
//                    Log.d(TAG, "downloadXML: The response code was $response")
//
////                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
//////
//////                    val inputBuffer = CharArray(500)
//////                    var charsRead = 0
//////                    while (charsRead >= 0) {
//////                        charsRead = reader.read(inputBuffer)
//////                        if (charsRead > 0) {
//////                            xmlResult.append(String(inputBuffer, 0, charsRead))
//////                        }
//////                    }
//////                    reader.close()
//
//                    //val stream = connection.inputStream
//                    connection.inputStream.buffered().reader().use { xmlResult.append(it.readText()) }
//                    Log.d(TAG, "Recieved ${xmlResult.length} bytes")
//                    return xmlResult.toString()
//
////                } catch (e: MalformedURLException) {
////                    Log.e(TAG, "downloadXML: Invalid URL ${e.message}")
////                } catch (e: IOException) {
////                    Log.e(TAG, "downloadXML: IO Exception reading data: ${e.message}")
////                } catch (e: SecurityException) {
////                    e.printStackTrace()
////                    Log.e(TAG, "downloadXML: Security exception. Needs permissions? ${e.message}")
////                } catch (e: Exception) {
////                    Log.e(TAG, "Unkown error: ${e.message}")
////                }
//                } catch (e: Exception) {
//                    val errorMessage: String = when (e) {
//                         is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
//                         is IOException -> "downloadXML: IO Exception reading data: ${e.message}"
//                         is SecurityException ->  {e.printStackTrace()
//                             "downloadXML: Security Exception. Needs permission? ${e.message}"
//
//                         }
//                         else -> "Unknown error: ${e.message}"
//                     }
//                }
//
//                return "" // If it gets to here, there has been a pbromgle. Return and empty string
//            }
            }
        }
    }

}
