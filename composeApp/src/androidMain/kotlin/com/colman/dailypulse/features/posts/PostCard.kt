import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.colman.dailypulse.models.posts.Post
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun PostCard(
    post: Post,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    currentUserId: String,
    ) {
    val transformedImageUrl = post.imageUrl?.let {
        val parts = it.split("/upload/")
        if (parts.size == 2) {
            "${parts[0]}/upload/c_pad,b_gray,w_800,h_800/${parts[1]}"
        } else it
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.authorName ?: "Unknown",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatRelativeTime(post.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(11.dp))

            post.imageUrl?.let {
                AsyncImage(
                    model = transformedImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${post.likedByUserIds.size} likes",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(Modifier.weight(1f))


                if (post.createdByUserId == currentUserId) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }

            }
        }
    }
}

fun formatRelativeTime(createdAt: Instant): String {
    val now = Clock.System.now()
    val diff = now - createdAt

    return when {
        diff < 1.minutes -> "just now"
        diff < 1.hours -> "${diff.inWholeMinutes} minutes ago"
        diff < 24.hours -> "${diff.inWholeHours} hours ago"
        diff < 7.days -> "${diff.inWholeDays} days ago"
        else -> {
            val dateTime = createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            "${dateTime.date.dayOfMonth}/${dateTime.date.monthNumber}/${dateTime.date.year}"
        }
    }
}