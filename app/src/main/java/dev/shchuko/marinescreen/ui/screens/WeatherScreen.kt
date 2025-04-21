package dev.shchuko.marinescreen.ui.screens

import android.graphics.Mesh
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.theme.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.theme.VicoTheme
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.draw.ChartDrawContext
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import com.patrykandpatrick.vico.core.scroll.Scroll
import com.patrykandpatrick.vico.core.zoom.Zoom
import dev.shchuko.marinescreen.R
import dev.shchuko.marinescreen.domain.model.PreciseTime
import dev.shchuko.marinescreen.domain.model.PreciseTimeStatus
import dev.shchuko.marinescreen.domain.model.StationMeasurements
import dev.shchuko.marinescreen.ui.tv.TvFocusableTextButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeatherScreen(
    time: PreciseTime,
    firstNtpSyncDone: Boolean,
    measurements: StationMeasurements,
    onSettingsClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val windGraphModelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(measurements, firstNtpSyncDone) {
        updateGraphModel(windGraphModelProducer, time.time, measurements)
    }

    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    ) {
                        MyTextClock(time)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Absolute.Right,
                        ) {
                            Text(measurements.stationName ?: stringResource(R.string.display_name_default), style = TextStyle(fontSize = 25.textDp))
                            TvFocusableTextButton(onClick = onSettingsClick) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.button_settings)
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(28.dp),
                contentPadding = PaddingValues(2.dp, 2.dp, 2.dp, 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        "github.com/shchuko/marine-screen",
                        style = TextStyle(fontSize = 16.textDp)
                    )
                    Text("Updated ${measurements.lastUpdatedAt?.let { updatedAt -> time.time.minus(updatedAt).inWholeMinutes} ?: "--"} min ago", style = TextStyle(fontSize = 16.textDp))
                }
            }
        }
    ) { paddingValues ->
        BoxWithConstraints {
            val boxWidth = maxWidth

            val fontSize = 50.textDp
            val fontSize2 = 25.textDp
            val fontSize3 = 23.textDp
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val columnWidth = boxWidth / 3
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(columnWidth)
                            .padding(start = 30.dp)
                    ) {
                        Text(text = "Wind ${measurements.current?.windSpeedKts.toWindString()} kts", style = TextStyle(fontSize = fontSize))
                        Text(text = "(${measurements.current?.windSpeedMps.toWindString()} m/s)", style = TextStyle(fontSize = fontSize2))
                    }
                    Column(
                        modifier = Modifier
                            .width(columnWidth),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = measurements.current?.windDirectionDeg.toWindDirectionString(), style = TextStyle(fontSize = fontSize))
                        Text(text = "(${measurements.current?.windDirectionDeg.toWindString()}\u00B0)", style = TextStyle(fontSize = fontSize2))
                    }
                    Column(
                        modifier = Modifier
                            .width(columnWidth)
                            .padding(end = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "Gust ${measurements.current?.windGustKts.toWindString()} kts", style = TextStyle(fontSize = fontSize))
                        Text(text = "(${measurements.current?.windGustMps.toWindString()} m/s)", style = TextStyle(fontSize = fontSize2))
                    }
                }

                WindHistoryChart(
                    modelProducer = windGraphModelProducer,
                    hoursDepth = 6,
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(7f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                )

                Row(
                    horizontalArrangement = Arrangement.Absolute.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)
                        .fillMaxHeight()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Temp: ${measurements.current?.temperatureC.toTemperatureString()}", style = TextStyle(fontSize = fontSize3))
                    Text(text = "Rh: ${measurements.current?.humidityPercent.toHumidityString()}", style = TextStyle(fontSize = fontSize3))
                    Text(text = "Feels like ${null.toTemperatureString()}", style = TextStyle(fontSize = fontSize3))
                }
            }
        }
    }
}

fun Double?.toWindString(): String = this?.roundToInt()?.toString() ?: "--"

fun Int?.toWindString(): String = this?.toString() ?: "--"

fun Int?.toHumidityString(): String = when {
    this == null -> "--"
    else -> "$this"
} + "%"

fun Double?.toTemperatureString(): String = when {
    this == null -> "--"
    this > 0 -> "+${this.roundToInt()}"
    this < 0 -> "-${this.absoluteValue.roundToInt()}"
    else -> "0"
} + "\u00B0C"
@Composable
fun WindHistoryChart(
    modelProducer: CartesianChartModelProducer,
    hoursDepth: Int,
    modifier: Modifier = Modifier,
) {
    val windLineColor = Color(0xffa485e0)
    val gustLineColor = Color(0x81A485E0)

    class DefaultColors(
        val cartesianLayerColors: List<Long>,
        val elevationOverlayColor: Long,
        val lineColor: Long,
        val textColor: Long,
    )

    val Dark = DefaultColors(
        cartesianLayerColors = listOf(0xffcacaca, 0xffa8a8a8, 0xff888888),
        elevationOverlayColor = 0xffffffff,
        lineColor = 0xff555555,
        textColor = 0xffffffff,
    )

    val Light: DefaultColors =
        DefaultColors(
            cartesianLayerColors = listOf(0xff787878, 0xff5a5a5a, 0xff383838),
            elevationOverlayColor = 0x00000000,
            lineColor = 0x47000000,
            textColor = 0xde000000,
        )

    fun fromDefaultColors1(defaultColors: DefaultColors) =
        VicoTheme(
            defaultColors.cartesianLayerColors.map(::Color),
            Color(defaultColors.elevationOverlayColor),
            Color(defaultColors.lineColor),
            Color(defaultColors.textColor),
        )

    ProvideVicoTheme(fromDefaultColors1(Dark)) {
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lines = rememberLineSpec(
                        DynamicShaders.color(windLineColor),
                        backgroundShader = null
                    ).wrapWithList(),
                    axisValueOverrider = AxisValueOverrider.fixed(
                        minX = -hoursDepth * 3600f,
                        maxX = 0f,
                        minY = 0f,
                    ),
                ),
                rememberLineCartesianLayer(
                    lines = rememberLineSpec(
                        shader = DynamicShaders.color(gustLineColor),
                        backgroundShader = null
                    ).wrapWithList()
                ),

                startAxis = rememberStartAxis(
                    axis = rememberAxisLineComponent(color = Color(0xff555555)),
                    tick = rememberAxisTickComponent(
                        color = Color(0xff555555),
                        dynamicShader = null
                    ),
                    guideline = rememberAxisGuidelineComponent(color = Color(0xff555555)),
                    title = "knots",
                    itemPlacer = AxisItemPlacer.Vertical.step({ 5f }, shiftTopLines = true),
                    titleComponent = rememberTextComponent(
                        color = Color.Black,
                        background = rememberShapeComponent(Shapes.pillShape, windLineColor),
                        padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                        margins = dimensionsOf(end = 4.dp),
                        typeface = Typeface.MONOSPACE,
                    ),
                ),
                bottomAxis = rememberBottomAxis(
                    axis = rememberAxisLineComponent(color = Color(0xff555555)),
                    tick = rememberAxisTickComponent(
                        color = Color(0xff555555),
                        dynamicShader = null
                    ),
                    guideline = null,
                    itemPlacer = remember { SameDistanceItemPlacer(6) },
                    valueFormatter = { x, _, _ ->
                        val hours = -(x / 3600).roundToInt()
                        if (hours == 1) "$hours hour ago" else "$hours hours ago"
                    },
                    title = "wind speed",
                    titleComponent = rememberTextComponent(
                        color = Color.Black,
                        background = rememberShapeComponent(Shapes.pillShape, windLineColor),
                        padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                        margins = dimensionsOf(end = 4.dp),
                        typeface = Typeface.MONOSPACE,
                    ),
                ),
            ),
            modelProducer = modelProducer,
            modifier = modifier.padding(end = 16.dp),
            scrollState = rememberVicoScrollState(
                initialScroll = Scroll.Absolute.End,
            ),
            zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
            runInitialAnimation = false,
            diffAnimationSpec = null
        )
    }
}

class SameDistanceItemPlacer(
    private val segmentsNumber: Int,
) : AxisItemPlacer.Horizontal by AxisItemPlacer.Horizontal.default() {
    override fun getLabelValues(
        context: ChartDrawContext,
        visibleXRange: ClosedFloatingPointRange<Float>,
        fullXRange: ClosedFloatingPointRange<Float>,
        maxLabelWidth: Float
    ): List<Float> {
        val segmentWidth = (context.chartValues.maxX - context.chartValues.minX) / segmentsNumber
        return (1 until segmentsNumber).map { segmentNumber ->
            context.chartValues.minX + segmentNumber * segmentWidth
        }
    }

    override fun getLineValues(
        context: ChartDrawContext,
        visibleXRange: ClosedFloatingPointRange<Float>,
        fullXRange: ClosedFloatingPointRange<Float>,
        maxLabelWidth: Float
    ): List<Float>? = null
}

private fun <T> T.wrapWithList() = listOf(this)


fun clearGraph(modelProducer: CartesianChartModelProducer) {
    modelProducer.tryRunTransaction {
        lineSeries {
            series(listOf(0, -10), listOf(10, 10))
        }
    }
}

fun updateGraphModel(
    modelProducer: CartesianChartModelProducer,
    now: Instant,
    measurements: StationMeasurements,
) {
    val wind = measurements.historical.mapNotNull {
        WindDataPoint(
            timestamp = it.timestamp,
            knots = it.windSpeedKts ?: return@mapNotNull null
        )
    }
    val gust = measurements.historical.mapNotNull {
        WindDataPoint(
            timestamp = it.timestamp,
            knots = it.windGustKts ?: return@mapNotNull null
        )
    }

    if (wind.isEmpty() && gust.isEmpty()) {
        clearGraph(modelProducer)
        return
    }

    modelProducer.tryRunTransaction {
        windLineSeries(
            now = now,
            wind = wind
        )

        windLineSeries(
            now = now,
            wind = gust
        )
    }
}

data class WindDataPoint(
    val timestamp: Instant,
    val knots: Double,
)
fun CartesianChartModelProducer.Transaction.windLineSeries(now: Instant, wind: List<WindDataPoint>) {
    lineSeries {
        var next = mutableListOf<WindDataPoint>()
        wind.forEach { point ->
            if (next.isEmpty() || point.timestamp - next.last().timestamp <= 4.minutes) {
                next += point
            } else {
                series(
                    x = next.map { (it.timestamp - now).inWholeSeconds },
                    y = next.map { it.knots },
                )
                next = mutableListOf()
            }
        }
        series(
            x = next.map { (it.timestamp - now).inWholeSeconds },
            y = next.map { it.knots },
        )
    }
}


@Composable
fun MyTextClock(time: PreciseTime) {
    val currentTime by remember(time.localTime) { mutableStateOf(time.localTime.toTimeString()) }
    Text(text = currentTime, style = TextStyle(fontSize = 25.textDp))
}

fun LocalDateTime.toTimeString(): String {
    return formatLocalDateTime(this)
//    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//    return sdf.format(toJavaLocalDateTime())
}

fun Int?.toWindDirectionString(): String {
    if (this == null) return "--"

    val directions = arrayOf(
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    )

    val index = ((this / 22.5) + 0.5).toInt() % 16
    return directions[index]
}

private fun formatLocalDateTime(dt: LocalDateTime): String {
    val day = dt.date.dayOfMonth.toString().padStart(2, '0')
    val month = dt.date.monthNumber.toString().padStart(2, '0')
    val year = dt.date.year
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')
    val second = dt.second.toString().padStart(2, '0')
    return "$day/$month/$year $hour:$minute:$second"
}


private fun Int.textDp(density: Density): TextUnit = with(density) {
    this@textDp.dp.toSp()
}

val Int.textDp: TextUnit
    @Composable get() =  this.textDp(density = LocalDensity.current)