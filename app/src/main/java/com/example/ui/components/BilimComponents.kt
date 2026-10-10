package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.BlueGeographyContainer
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.GoldHistoryContainer
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusAbsentContainer
import com.example.ui.theme.StatusExcused
import com.example.ui.theme.StatusExcusedContainer
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusLateContainer
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.StatusPresentContainer
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun BilimCard(
    modifier: Modifier = Modifier,
    borderColor: Color = DarkNavyCardBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.5f))))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(accentColor.copy(alpha = 0.4f), DarkNavyCardBorder)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextMutedGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SubjectBadge(
    subjectId: String,
    modifier: Modifier = Modifier
) {
    val isTarix = subjectId.equals("tarix", ignoreCase = true)
    val color = if (isTarix) GoldHistory else BlueGeography
    val container = if (isTarix) GoldHistoryContainer else BlueGeographyContainer
    val title = if (isTarix) "Tarix" else "Geografiya"
    val icon = if (isTarix) Icons.Default.Book else Icons.Default.Public

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = container
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun GradeBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val (color, container) = when {
        score >= 86 -> Pair(StatusPresent, StatusPresentContainer) // A'lo
        score >= 71 -> Pair(BlueGeography, BlueGeographyContainer) // Yaxshi
        score >= 56 -> Pair(GoldHistory, GoldHistoryContainer) // Qoniqarli
        else -> Pair(StatusAbsent, StatusAbsentContainer) // Qoniqarsiz
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = container
    ) {
        Text(
            text = "$score ball",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AttendanceBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, color, container) = when (status.uppercase()) {
        "KELDI" -> Triple("Keldi", StatusPresent, StatusPresentContainer)
        "KECHIKDI" -> Triple("Kechikdi", StatusLate, StatusLateContainer)
        "SABABLI" -> Triple("Sababli", StatusExcused, StatusExcusedContainer)
        else -> Triple("Sababsiz", StatusAbsent, StatusAbsentContainer)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = container
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AppHeader(
    user: UserEntity?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(GoldHistory, BlueGeography)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.fullName?.take(1)?.uppercase() ?: "B",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavySurface
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = user?.fullName ?: "Foydalanuvchi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val roleTitle = when (user?.role) {
                    UserRole.ADMIN.name -> "Administrator"
                    UserRole.TEACHER.name -> "O'qituvchi (${if (user.assignedSubject == "tarix") "Tarix" else "Geografiya"})"
                    UserRole.STUDENT.name -> "${user.className} o'quvchisi"
                    UserRole.PARENT.name -> "Ota-ona kabineti"
                    else -> "Mehmon"
                }
                Text(
                    text = roleTitle,
                    fontSize = 12.sp,
                    color = TextMutedGray
                )
            }
        }

        IconButton(onClick = onLogout) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Chiqish",
                tint = TextMutedGray
            )
        }
    }
}
