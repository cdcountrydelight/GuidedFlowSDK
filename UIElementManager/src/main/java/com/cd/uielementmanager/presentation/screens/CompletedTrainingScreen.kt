package com.cd.uielementmanager.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cd.uielementmanager.R
import com.cd.uielementmanager.presentation.beans.ButtonHandlerBean
import com.cd.uielementmanager.presentation.composables.ErrorAlertDialog
import com.cd.uielementmanager.presentation.composables.LoadingSection
import com.cd.uielementmanager.presentation.composables.SpacerHeight12s
import com.cd.uielementmanager.presentation.composables.SpacerHeight16s
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.getErrorMessage
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random


@Composable
fun CompletedTrainingScreen(
    viewModel: UIElementViewModel,
    calculatedScore: Double,
    onStartNextFlow: () -> Unit,
    onBackButton: () -> Unit,
    onGoToHome: () -> Unit
) {
    val context = LocalContext.current
    Scaffold {
        Box(Modifier.padding(it)) {
            HandleTrainingCompletedStateFlow(
                viewModel,
                calculatedScore,
                onStartNextFlow,
                onBackButton,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.completeTraining(viewModel.selectedFlowId ?: 0, context)
    }
}


@Composable
private fun HandleTrainingCompletedStateFlow(
    viewModel: UIElementViewModel,
    calculatedScore: Double?,
    onStartNextFlow: () -> Unit,
    onBackRequested: () -> Unit
) {
    val flowDetailsStateFlow = viewModel.completeTrainingStateFlow.collectAsStateWithLifecycle()

    var isResponseHandled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    when (val response = flowDetailsStateFlow.value) {
        is DataUiResponseStatus.Loading -> {
            isResponseHandled = false
            LoadingSection()
        }

        is DataUiResponseStatus.Success -> {
            TrainingCompletedSection(
                viewModel,
                response.data.flowName,
                calculatedScore,
                onGoToHome={},
                onStartNextFlow = onStartNextFlow
            )
        }

        is DataUiResponseStatus.Failure -> {
            if (!isResponseHandled) {
                ErrorAlertDialog(
                    errorMessage = context.getErrorMessage(
                        response.errorMessage,
                        response.errorCode
                    ),
                    negativeButton = ButtonHandlerBean(
                        buttonText = stringResource(R.string.ok),
                        onButtonClicked = {
                            isResponseHandled = true
                            onBackRequested()
                        }
                    )
                )
            }
        }

        else -> {}
    }
}


@Composable
private fun TrainingCompletedSection(
    viewModel: UIElementViewModel,
    flowName: String?,
    calculatedScore: Double?,
    onGoToHome: () -> Unit,
    onStartNextFlow: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(animatedRotation)
                .scale(1.5f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(600)) + scaleIn(
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SpacerHeight12s()
                HeaderSection(calculatedScore)
                GlassmorphismCard(flowName)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Primary button with gradient
                    Button(
                        onClick = {
                            //viewModel.resetCompleteTraining()
                            onGoToHome()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.go_to_home),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Secondary button with outline
                    TextButton(
                        onClick = {
                        //    viewModel.resetCompleteTraining()
                            onStartNextFlow()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.start_next_flow),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Confetti animation
        if (showContent && calculatedScore != null && calculatedScore >= 70) {
            ConfettiAnimation()
        }
    }
}


@Composable
private fun HeaderSection(calculatedScore: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedScoreDisplay(calculatedScore)
        SpacerHeight16s()
        calculatedScore?.let {
            GradeIndicator(it)
        }
    }
}

@Composable
private fun AnimatedScoreDisplay(calculatedScore: Double?) {
    val size = 100.dp
    val strokeWidth = 8.dp
    val fontSize = 34.sp

    if (calculatedScore == null) {
        // Show check icon for null score
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Background circle
            val primaryColor = MaterialTheme.colorScheme.primary
            Canvas(
                modifier = Modifier.size(size)
            ) {
                drawCircle(
                    color = primaryColor.copy(alpha = 0.1f),
                    radius = this.size.minDimension / 2
                )
                drawCircle(
                    color = primaryColor,
                    radius = (this.size.minDimension * 0.9f) / 2,
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }

            // Check icon - draw manually
            Canvas(
                modifier = Modifier.size(size * 0.5f)
            ) {
                val checkPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(this@Canvas.size.width * 0.2f, this@Canvas.size.height * 0.5f)
                    lineTo(this@Canvas.size.width * 0.4f, this@Canvas.size.height * 0.7f)
                    lineTo(this@Canvas.size.width * 0.8f, this@Canvas.size.height * 0.3f)
                }
                drawPath(
                    path = checkPath,
                    color = primaryColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }
    } else {
        // Show animated score
        var animatedScore by remember { mutableFloatStateOf(0f) }
        val targetScore = calculatedScore.toFloat()
        val animatedProgress by animateFloatAsState(
            targetValue = if (animatedScore > 0) targetScore / 100f else 0f,
            animationSpec = tween(1500, easing = FastOutSlowInEasing),
            label = "progress"
        )

        LaunchedEffect(calculatedScore) {
            val animationDuration = 1500L
            val steps = 30
            val stepDuration = animationDuration / steps

            for (i in 1..steps) {
                delay(stepDuration)
                animatedScore = (targetScore * i) / steps
            }
        }

        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            // Background circle with gradient
            Canvas(
                modifier = Modifier.size(size)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    radius = this.size.minDimension / 2
                )
            }

            // Progress ring
            Canvas(
                modifier = Modifier.size(size * 0.9f)
            ) {
                val strokeWidthPx = strokeWidth.toPx()
                val radius = (this.size.minDimension - strokeWidthPx) / 2
                val startAngle = -90f

                // Background ring
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.1f),
                    radius = radius,
                    style = Stroke(strokeWidthPx, cap = StrokeCap.Round)
                )

                // Progress ring
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF8BC34A),
                            Color(0xFF4CAF50)
                        )
                    ),
                    startAngle = startAngle,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(strokeWidthPx, cap = StrokeCap.Round)
                )
            }

            // Score text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${animatedScore.toInt()}",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun GradeIndicator(score: Double) {
    val (gradeText, gradeColor) = when {
        score >= 90 -> Pair(
            "Excellent!",
            Color(0xFF4CAF50)
        )

        score >= 80 -> Pair(
            "Great Job!",
            Color(0xFF8BC34A)
        )

        score >= 70 -> Pair(
            "Good Work!",
            Color(0xFFFFC107)
        )

        else -> Pair(
            "Completed!",
            MaterialTheme.colorScheme.primary
        )
    }


    Box(
        modifier = Modifier
            .background(
                color = gradeColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = gradeText,
            color = gradeColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun GlassmorphismCard(flowName: String?) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Congratulations! 🎉",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = buildAnnotatedString {
                    append("You have successfully completed training for ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(flowName ?: stringResource(R.string.untitled_flow))
                    }
                    append(". You can revisit this training anytime from the flow list")
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ConfettiAnimation() {
    // Create 4 waves of particles
    val particleWaves = remember {
        List(4) { waveIndex ->
            List(40) {
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = -0.1f - (waveIndex * 0.4f), // Stagger the 4 waves
                    color = listOf(
                        Color(0xFFE91E63),
                        Color(0xFF9C27B0),
                        Color(0xFF2196F3),
                        Color(0xFF4CAF50),
                        Color(0xFFFFC107),
                        Color(0xFFFF5722)
                    ).random(),
                    size = Random.nextInt(8, 17).toFloat(),
                    velocity = Random.nextFloat() * 0.8f + 0.4f,
                    angle = Random.nextInt(-30, 31).toFloat(),
                    cycleCount = 0 // Track how many times particle has fallen
                )
            }
        }.flatten()
    }

    val animatedParticles = remember { mutableStateOf(particleWaves) }
    var isAnimationActive by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (isAnimationActive) {
            delay(16) // 60 FPS
            animatedParticles.value = animatedParticles.value.map { particle ->
                val newY = particle.y + particle.velocity * 0.018f
                val newX = particle.x + sin(particle.angle * Math.PI / 180).toFloat() * 0.01f

                // Check if particle has gone off screen
                if (newY > 1.2f) {
                    // Stop animation if particle has completed 1 cycle (4 waves = 1 full cycle)
                    if (particle.cycleCount >= 0) {
                        particle.copy(y = 2.0f) // Move off screen permanently
                    } else {
                        particle.copy(y = newY, x = newX)
                    }
                } else {
                    particle.copy(y = newY, x = newX)
                }
            }

            // Check if all particles are off screen
            if (animatedParticles.value.all { it.y > 1.2f }) {
                isAnimationActive = false
            }
        }
    }

    if (isAnimationActive) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            animatedParticles.value.forEach { particle ->
                if (particle.y >= -0.1f && particle.y < 1.2f) {
                    drawConfetti(particle)
                }
            }
        }
    }
}

private fun DrawScope.drawConfetti(particle: ConfettiParticle) {
    val x = particle.x * size.width
    val y = particle.y * size.height

    rotate(particle.angle, pivot = Offset(x, y)) {
        drawRect(
            color = particle.color,
            topLeft = Offset(x - particle.size / 2, y - particle.size / 2),
            size = Size(particle.size, particle.size * 2),
            alpha = (1f - particle.y).coerceIn(0f, 1f)
        )
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val velocity: Float,
    val angle: Float,
    val cycleCount: Int = 0
)