package com.rasya0020.valrank.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rasya0020.valrank.navigation.Screen
import com.rasya0020.valrank.R
import com.rasya0020.valrank.ui.theme.ValRankTheme
import kotlin.math.ceil

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    ValRankTheme{
        MainScreen(rememberNavController())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.About.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier){
    var rankSekarang by rememberSaveable { mutableStateOf("") }
    var rankTujuan by rememberSaveable { mutableStateOf("") }
    var rankSekarangError by rememberSaveable { mutableStateOf(false) }
    var rankTujuanError by rememberSaveable { mutableStateOf(false) }

    val radioOptions = listOf(
        stringResource(id = R.string.tim_jago),
        stringResource(id = R.string.tim_bapuk)
    )
    var kondisiTim by rememberSaveable { mutableStateOf(radioOptions[0]) }
    var hasilKondisiTim by rememberSaveable { mutableStateOf("") }

    var totalRR by rememberSaveable { mutableIntStateOf(0) }
    var butuhWin by rememberSaveable { mutableIntStateOf(0) }
    var isSubmitted by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier= modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.valo_banner),
            contentDescription = "Valorant Banner",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = stringResource(id = R.string.rank_intro),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rankSekarang,
            onValueChange = { rankSekarang = it; rankSekarangError = false },
            label = { Text(text = stringResource(R.string.rank_sekarang)) },
            trailingIcon = { IconPicker(rankSekarangError) },
            supportingText = { ErrorHint(rankSekarangError) },
            isError = rankSekarangError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rankTujuan,
            onValueChange = { rankTujuan = it; rankTujuanError = false },
            label = { Text(text = stringResource(R.string.rank_tujuan)) },
            trailingIcon = { IconPicker(rankTujuanError) },
            supportingText = { ErrorHint(rankTujuanError) },
            isError = rankTujuanError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .padding(top = 6.dp)
        ) {
            radioOptions.forEach { text ->
                TeamOption(
                    label = text,
                    isSelected = kondisiTim == text,
                    modifier = Modifier
                        .selectable(
                            selected = kondisiTim == text,
                            onClick = { kondisiTim = text },
                            role = Role.RadioButton
                        )
                        .weight(1f)
                        .padding(16.dp)
                )
            }
        }
        Button(
            onClick = {
                rankSekarangError = rankSekarang.isEmpty()
                rankTujuanError = rankTujuan.isEmpty()

                if (!rankSekarangError && !rankTujuanError) {
                    val gainPerWin = if (kondisiTim == radioOptions[0]) 25 else 15

                    val listRank = listOf(
                        "Iron", "Bronze", "Silver", "Gold", "Platinum",
                        "Diamond", "Ascendant", "Immortal", "Radiant"
                    )

                    val indexSekarang = listRank.indexOfFirst { it.equals(rankSekarang.trim(), ignoreCase = true) }
                    val indexTujuan = listRank.indexOfFirst { it.equals(rankTujuan.trim(), ignoreCase = true) }

                    if (indexSekarang != -1 && indexTujuan != -1 && indexTujuan > indexSekarang) {
                        val selisihRank = indexTujuan - indexSekarang
                        totalRR = selisihRank * 300
                    } else {
                        totalRR = 0
                    }

                    hasilKondisiTim = kondisiTim
                    butuhWin = ceil(totalRR.toDouble() / gainPerWin).toInt()
                    isSubmitted = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.hitung_estimasi))
        }

        if (isSubmitted){
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.estimasi_judul),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.total_rr_needed, totalRR),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.total_win_needed, butuhWin),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.status_tim_label, hasilKondisiTim),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            val shareMsg = stringResource(
                R.string.share_message,
                rankTujuan,
                butuhWin,
                totalRR,
                kondisiTim
            )
            Button(
                onClick = { shareData(context, shareMsg) },
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp).fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.bagikan_target))
            }
        }
    }
}

@Composable
fun TeamOption(label: String, isSelected: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Progres Rank"))
}

@Composable
fun IconPicker(isError: Boolean) {
    if (isError) Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError) Text(text = stringResource(R.string.input_invalid), color = MaterialTheme.colorScheme.error)
}