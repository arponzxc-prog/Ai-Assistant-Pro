package com.arponzxc.novaai.research

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class ResearchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val initialQuery = intent?.getStringExtra(EXTRA_QUERY).orEmpty()
        val autoRun = intent?.getBooleanExtra(EXTRA_AUTO_RUN, false) == true

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF38BDF8),
                    secondary = Color(0xFF818CF8),
                    background = Color(0xFF040914),
                    surface = Color(0xFF0C1527),
                    onPrimary = Color(0xFF04111D),
                    onBackground = Color(0xFFF1F5F9),
                    onSurface = Color(0xFFF1F5F9)
                )
            ) {
                ResearchStudioScreen(
                    initialQuery = initialQuery,
                    autoRun = autoRun,
                    onBack = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_QUERY = "extra_research_query"
        const val EXTRA_AUTO_RUN = "extra_research_auto_run"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResearchStudioScreen(
    initialQuery: String,
    autoRun: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val engine = remember { ResearchEngine(context) }

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Explore, 1 = History
    var query by remember { mutableStateOf(initialQuery) }
    var selectedMode by remember { mutableStateOf(ResearchMode.DETAILED) }
    var isSearching by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ResearchResult?>(null) }
    var history by remember { mutableStateOf(engine.getHistory()) }

    fun executeSearch(q: String = query, mode: ResearchMode = selectedMode) {
        if (q.isBlank()) return
        scope.launch {
            isSearching = true
            activeTab = 0
            val res = engine.performResearch(q, mode.label)
            result = res
            history = engine.getHistory()
            isSearching = false
        }
    }

    LaunchedEffect(autoRun, initialQuery) {
        if (autoRun && initialQuery.isNotBlank()) {
            executeSearch(initialQuery, selectedMode)
        }
    }

    Surface(
        color = Color(0xFF040914),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("research_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Knowledge & Research",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Wikipedia • DuckDuckGo • Wikidata • Multi-Provider Synthesis",
                            fontSize = 11.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color(0xFF0C1527),
                    modifier = Modifier.padding(vertical = 8.dp).clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Research") },
                        icon = { Icon(Icons.Rounded.Public, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = {
                            history = engine.getHistory()
                            activeTab = 1
                        },
                        text = { Text("History (${history.size})") },
                        icon = { Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                if (activeTab == 0) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = { Text("Enter research topic or question…") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("research_query_input")
                            )
                            Button(
                                onClick = { executeSearch() },
                                enabled = !isSearching && query.isNotBlank(),
                                modifier = Modifier
                                    .height(54.dp)
                                    .testTag("research_search_button")
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.Black)
                                } else {
                                    Icon(Icons.Rounded.Search, contentDescription = "Run Research")
                                }
                            }
                        }

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResearchMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = selectedMode == mode,
                                    onClick = { selectedMode = mode },
                                    label = { Text(mode.label, fontSize = 12.sp) }
                                )
                            }
                        }

                        result?.let { res ->
                            if (!res.success) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = Color(0xFFFCA5A5))
                                            Text("Research Unavailable", fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Text(res.errorMessage, color = Color(0xFFFECACA), fontSize = 13.sp)
                                        if (res.providerDiagnostics.isNotEmpty()) {
                                            Text(
                                                text = res.providerDiagnostics.joinToString(" • "),
                                                color = Color(0xFFFCA5A5),
                                                fontSize = 11.sp
                                            )
                                        }
                                        OutlinedButton(onClick = { executeSearch() }) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            } else {
                                // AI Summary Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D36)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                                Text(
                                                    text = if (res.aiSynthesized) "Verified Multi-Source Synthesis" else "Verified Source Report",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF38BDF8)
                                                )
                                            }
                                            IconButton(onClick = {
                                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                cm.setPrimaryClip(ClipData.newPlainText("NOVA Research", res.summary))
                                                Toast.makeText(context, "Research report copied", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy report", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Text(
                                            text = res.summary,
                                            fontSize = 13.5.sp,
                                            lineHeight = 20.sp,
                                            color = Color(0xFFF1F5F9)
                                        )
                                        if (res.providerDiagnostics.isNotEmpty()) {
                                            Text(
                                                text = res.providerDiagnostics.joinToString(" | "),
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }

                                // Sources List
                                Text(
                                    text = "Verified Sources (${res.sources.size})",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                res.sources.forEachIndexed { idx, src ->
                                    val badgeColor = when (src.credibility) {
                                        SourceCredibility.HIGH -> Color(0xFF065F46)
                                        SourceCredibility.MEDIUM -> Color(0xFF1E3A8A)
                                        SourceCredibility.LOW -> Color(0xFF334155)
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1325)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                runCatching { uriHandler.openUri(src.url) }
                                                    .onFailure { Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show() }
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "${idx + 1}. ${src.title}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.5.sp,
                                                    color = Color.White,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(badgeColor)
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(src.credibility.label, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                            Text(src.snippet, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = "${src.providerName} • ${src.domain.ifBlank { src.url }}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF38BDF8)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    // Saved History Tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (history.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1527)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No saved research sessions yet. Run a search to store structured findings here.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = {
                                    engine.clearHistory()
                                    history = emptyList()
                                }) {
                                    Text("Clear All", color = Color(0xFFF87171))
                                }
                            }
                            history.forEach { item ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1527)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            query = item.query
                                            result = item
                                            activeTab = 0
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.query, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                            Text(
                                                text = "${item.mode} • ${item.sources.size} sources • ${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(item.timestamp))}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                        IconButton(onClick = {
                                            engine.deleteHistoryEntry(item.timestamp)
                                            history = engine.getHistory()
                                        }) {
                                            Icon(Icons.Rounded.Delete, contentDescription = "Delete history item", tint = Color(0xFFF87171))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
