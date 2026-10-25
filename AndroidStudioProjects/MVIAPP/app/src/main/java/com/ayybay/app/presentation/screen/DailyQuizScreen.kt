package com.ayybay.app.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.presentation.mvi.QuizSession
import com.ayybay.app.presentation.util.localizeDigits
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.IncomeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizScreen(
    session: QuizSession?,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onStartQuiz: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "🧠  " + tr("Daily Quiz", "দৈনিক কুইজ"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = {
                    LanguageToggle(
                        modifier = Modifier.padding(end = 12.dp),
                        selectedColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        borderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                        dividerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                session == null -> NoQuiz(onStartQuiz)
                session.isFinished -> QuizResultView(session, onRetake = onStartQuiz, onDone = onBack)
                else -> QuestionView(session, onSelectAnswer, onNext)
            }
        }
    }
}

@Composable
private fun NoQuiz(onStartQuiz: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = tr("Ready for today's quiz?", "আজকের কুইজের জন্য প্রস্তুত?"), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onStartQuiz, shape = RoundedCornerShape(14.dp)) { Text(tr("Start quiz", "কুইজ শুরু করুন")) }
        }
    }
}

@Composable
private fun QuestionView(session: QuizSession, onSelectAnswer: (Int) -> Unit, onNext: () -> Unit) {
    val language = LocalAppLanguage.current
    val question = session.currentQuestion
    val selected = session.answers[session.currentIndex]
    val options = if (language == AppLanguage.EN) question.optionsEn else question.optionsBn
    val number = localizeDigits("${session.currentIndex + 1}", language)
    val total = localizeDigits("${session.questions.size}", language)

    Text(
        text = tr("Question $number of $total", "প্রশ্ন $number / $total"),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    LinearProgressIndicator(
        progress = { (session.currentIndex + 1) / session.questions.size.toFloat() },
        modifier = Modifier.fillMaxWidth()
    )

    Text(
        text = trOf(language, question.questionEn, question.questionBn),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    options.forEachIndexed { index, label ->
        OptionRow(
            label = label,
            state = when {
                selected == null -> OptionState.Idle
                index == question.correctIndex -> OptionState.Correct
                index == selected -> OptionState.Wrong
                else -> OptionState.Dimmed
            },
            onClick = { onSelectAnswer(index) },
            enabled = selected == null
        )
    }

    if (selected != null) {
        val isCorrect = selected == question.correctIndex
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = (if (isCorrect) IncomeGreen else ExpenseRed).copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isCorrect) tr("Correct! 🎉", "সঠিক! 🎉") else tr("Not quite", "ঠিক হয়নি"),
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) IncomeGreen else ExpenseRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = trOf(language, question.explanationEn, question.explanationBn), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text(if (session.isLastQuestion) tr("Finish", "শেষ করুন") else tr("Next question", "পরবর্তী প্রশ্ন"))
        }
    }
}

private enum class OptionState { Idle, Correct, Wrong, Dimmed }

@Composable
private fun OptionRow(label: String, state: OptionState, onClick: () -> Unit, enabled: Boolean) {
    val accent = when (state) {
        OptionState.Correct -> IncomeGreen
        OptionState.Wrong -> ExpenseRed
        else -> MaterialTheme.colorScheme.outline
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (state == OptionState.Correct || state == OptionState.Wrong) 2.dp else 1.dp, accent.copy(alpha = if (state == OptionState.Dimmed) 0.3f else 1f)),
        colors = CardDefaults.cardColors(
            containerColor = when (state) {
                OptionState.Correct -> IncomeGreen.copy(alpha = 0.1f)
                OptionState.Wrong -> ExpenseRed.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = if (state == OptionState.Dimmed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else Color.Unspecified
            )
            when (state) {
                OptionState.Correct -> Icon(Icons.Default.Check, contentDescription = null, tint = IncomeGreen)
                OptionState.Wrong -> Icon(Icons.Default.Close, contentDescription = null, tint = ExpenseRed)
                else -> Unit
            }
        }
    }
}

@Composable
private fun QuizResultView(session: QuizSession, onRetake: () -> Unit, onDone: () -> Unit) {
    val language = LocalAppLanguage.current
    val score = session.score
    val total = session.questions.size
    val ratio = score / total.toFloat()
    val emoji = when {
        ratio >= 0.8f -> "🏆"
        ratio >= 0.5f -> "👍"
        else -> "💪"
    }
    val message = when {
        ratio >= 0.8f -> tr("Excellent work!", "চমৎকার!")
        ratio >= 0.5f -> tr("Good effort. Review the lessons and try again.", "ভালো চেষ্টা। পাঠগুলো আবার দেখে নতুন করে চেষ্টা করুন।")
        else -> tr("Keep learning, you'll get there!", "শিখতে থাকুন, আপনি পারবেন!")
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localizeDigits("$score / $total", language),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, textAlign = TextAlign.Center)
        }
    }

    Button(onClick = onRetake, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Text(tr("Try again", "আবার চেষ্টা করুন"))
    }
    OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Text(tr("Done", "সম্পন্ন"))
    }
}
