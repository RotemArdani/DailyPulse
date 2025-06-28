package com.colman.dailypulse.features.posts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.colman.dailypulse.models.posts.Post

@Composable
fun PostCard(
    post: Post,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    val transformedImageUrl = post.imageUrl?.let {
        val parts = it.split("/upload/")
        if (parts.size == 2) {
            "${parts[0]}/upload/c_pad,b_gray,w_800,h_800/${parts[1]}"
        } else it
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.authorName?: "Unknown", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(text = post.description)
            Spacer(Modifier.height(8.dp))

            post.imageUrl?.let {
                AsyncImage(
                    model = transformedImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like"
                    )
                }
                Text(text = "${post.likedByUserIds.size}")
            }
        }
    }
}