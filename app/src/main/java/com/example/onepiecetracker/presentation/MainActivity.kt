/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.onepiecetracker.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.example.onepiecetracker.R
import com.example.onepiecetracker.presentation.theme.OnePieceTrackerTheme
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.onepiecetracker.presentation.classes.Chapter

var BASE_URL = "https://onepiecechapters.com"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG_HERE", "oncreate")

        setContent {
            val (chapters, setChapters) = remember { mutableStateOf(ArrayList<Chapter>()) }
            getOnePieceChapters(setValues = setChapters)
            WearApp(chapters)
        }
    }
}
@Composable
private fun getOnePieceChapters(setValues: (ArrayList<Chapter>) -> Unit) {
    Thread {
        val builder = StringBuilder()
        try {
            val doc = Jsoup.connect("$BASE_URL/mangas/5/one-piece").get()

            val entries = doc.select("div.col-span-2").select("a")

            val currentChapters = ArrayList<Chapter>();
            for (entry in entries) {
                val divs = entry.select("div")

                var chapterLink = entry.attr("href")
                var chapterNumber = divs[0].text().replace("One Piece Chapter ", "")
                var chapterName = divs[1].text()
                currentChapters.add(Chapter(chapterNumber, chapterName, chapterLink))
          }

            Log.d("TAG_HERE", currentChapters.toString());
            setValues(currentChapters);
        } catch (e: IOException) {
            Log.d("TAG_HERE",e.toString())
            builder.append("Error : ").append(e.message).append("\n")
        }
    }.start()
}

@Composable
fun WearApp(chapters: ArrayList<Chapter>) {
    val contentModifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    val listState = rememberScalingLazyListState()

    OnePieceTrackerTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            state = listState
        )  {
            item { Header() }
            for (chapter in chapters) {
                item { ChapterRow(contentModifier, chapter = chapter) }
            }
        }
    }
}

@Composable
fun Header() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Left,
        color = MaterialTheme.colors.primary,
        text = "One Piece Chapters"
    )
}

@Composable
fun ChapterRow(
    modifier: Modifier = Modifier,
    chapter: Chapter) {
    val context = LocalContext.current

    Chip(
        modifier = modifier,
        onClick = {
            startActivity(
                context,
                Intent(Intent.ACTION_VIEW, Uri.parse("$BASE_URL${chapter.link}")),
                null
                )
        },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontSize = 12.sp,
                color = MaterialTheme.colors.background,
                text = "${chapter.toString()}"
            )
        }
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(ArrayList<Chapter>())
}