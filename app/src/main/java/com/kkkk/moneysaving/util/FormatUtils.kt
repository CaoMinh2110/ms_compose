package com.kkkk.moneysaving.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.LocalCurrencySymbol
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun YearMonth.formatMonthYear(): String =
    this.format(
        DateTimeFormatter.ofPattern(
            stringResource(R.string.format_month_year),
            LocalConfiguration.current.locales[0]
        )
    )

@Composable
fun LocalDate.formatDateWithPrefix(): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val startOfLastWeek = today.minusWeeks(1).with(DayOfWeek.MONDAY)
    val endOfLastWeek = startOfLastWeek.plusDays(6)

    val dayMonthFormatter = DateTimeFormatter.ofPattern(
        stringResource(R.string.format_day_month),
        LocalConfiguration.current.locales[0]
    )
    val fullFormatter = DateTimeFormatter.ofPattern(
        stringResource(R.string.format_day),
        LocalConfiguration.current.locales[0]
    )

    return when {
        this == today ->
            "${stringResource(R.string.prefix_today)}, ${this.format(dayMonthFormatter)}"

        this == yesterday ->
            "${stringResource(R.string.prefix_yesterday)}, ${this.format(dayMonthFormatter)}"

        !this.isBefore(startOfLastWeek) && !this.isAfter(endOfLastWeek) ->
            "${stringResource(R.string.prefix_last_week)}, ${this.format(dayMonthFormatter)}"

        else -> this.format(fullFormatter)
    }
}

@Composable
fun YearMonth.formatMonthYearOrPrefix(): String {
    val now = YearMonth.now()
    return when (this) {
        now -> stringResource(R.string.prefix_this_month)
        now.minusMonths(1) -> stringResource(R.string.prefix_last_month)
        else -> this.format(
            DateTimeFormatter.ofPattern(
                stringResource(R.string.format_month_short_year),
                LocalConfiguration.current.locales[0]
            )
        )
    }
}

fun Long.formatShortAmount(): String {
    return when {
        this >= 1_000_000 -> "${(this / 1_000_000)}M"
        this >= 1_000 -> "${(this / 1_000)}K"
        else -> this.toString()
    }
}

@Composable
fun Long.formatDate(): String {
    val dt = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    return dt.format(DateTimeFormatter.ofPattern(stringResource(R.string.format_day)))
}

@Composable
fun Long.formatCurrencyAmount(): String {
    val format = NumberFormat.getInstance(Locale.GERMANY)
    val s = format.format(this)
    return "$s ${LocalCurrencySymbol.current}"
}