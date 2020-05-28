package com.austinmeyer.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
private const val STATE_CURRENT_LINK = "PendingOperation"
private const val STATE_FEEDLIMIT_STORED = "FeedLimit_State"
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var downloadData: DownloadData? = null


    private var feedUrl: String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10


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
        Log.d(TAG,"onCreate done")
    }

    private fun downloadUrl(feedUrl: String) {
        Log.d(TAG, "downloadUrl starting AsyncTask")
        downloadData = DownloadData(this, xmlListView)
        downloadData?.execute(feedUrl)
        Log.d(TAG, "downloadUrl done")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        downloadUrl(feedUrl.format(feedLimit))
        menuInflater.inflate(R.menu.feeds_menu, menu)
        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.mnuFree ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnuRefresh ->
                downloadUrl(feedUrl.format(feedLimit))
            R.id.mnu10, R.id.mnu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit unchanged")
                }
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
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
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        feedLimit = savedInstanceState.getInt(STATE_FEEDLIMIT_STORED.toString())
        Log.d(TAG, "$feedLimit")
        feedUrl = savedInstanceState.getString(STATE_CURRENT_LINK, "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml")
        downloadUrl(feedUrl.format(feedLimit))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_FEEDLIMIT_STORED, feedLimit)
        outState.putString(STATE_CURRENT_LINK, feedUrl)
    }
}
