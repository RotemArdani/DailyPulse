package com.colman.dailypulse.data.firebase

import co.touchlab.kermit.Logger
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts
import com.colman.dailypulse.models.users.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock

class RemoteFirebaseRepository: FirebaseRepository {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private var log = Logger.withTag("Firebase")

    override suspend fun createHabit(habit: Habit) {
        val userId = auth.currentUser?.uid ?: return

        val habitsCollection = firestore
            .collection("users")
            .document(userId)
            .collection("habits")

        val docRef = habitsCollection.add(habit)
        docRef.set(habit.copy(id = docRef.id))
    }

    override suspend fun createPost(post: Post) {
        val userId = auth.currentUser?.uid ?: return

        val postsCollection = firestore
            .collection("posts")

        val postWithUserId = post.copy(createdByUserId = userId)

        val docRef = postsCollection.add(postWithUserId)
        docRef.set(postWithUserId.copy(id = docRef.id))
    }

    override suspend fun likePost(postId: String) {
        val userId = auth.currentUser?.uid ?: return

        val postRef = firestore
            .collection("posts")
            .document(postId)

        val post = postRef.get().data<Post>()

        val updatedLikes = if (userId in post.likedByUserIds) {
            post.likedByUserIds - userId
        } else {
            post.likedByUserIds + userId
        }

        postRef.set((post.copy(likedByUserIds = updatedLikes)))
    }

    override suspend fun deletePost(postId: String) {
        firestore.collection("posts").document(postId).delete()
    }

    override suspend fun signUpUser(email: String, password: String, name: String) {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val user = authResult.user ?: return

            val userData = User(user.uid, name, email)

            firestore.collection("users")
                .document(user.uid)
                .set(userData)
    }



    override suspend fun signInUser(email: String, password: String): User? {
            val authResult = Firebase.auth.signInWithEmailAndPassword(email, password)
            val user = authResult.user ?: return null

            val doc = Firebase.firestore
                .collection("users")
                .document(user.uid)
                .get()

            val data = doc.data<User>()

            return data;
    }

        override suspend fun updateHabit(habit: Habit) {
        val userId = auth.currentUser?.uid ?: return
        val habitId = habit.id ?: return

        val habitRef = firestore
            .collection("users")
            .document(userId)
            .collection("habits").document(habitId)

        habitRef.set(habit)
    }

    override suspend fun habitDone(habitId: String) {
        val userId = auth.currentUser?.uid ?: return

        val habitRef = firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)

        val habit = habitRef.get().data<Habit>()

        habitRef.set((habit.copy(totalCount = habit.totalCount?.plus(1), lastModified = Clock.System.now())))
    }

    override suspend fun getHabitDetails(habitId: String): Habit? {
        val userId = auth.currentUser?.uid ?: return null

        val querySnapshot = firestore.collection("users")
            .document(userId)
            .collection("habits").document(habitId).get()

        val data = querySnapshot.data<Habit>()

        return data;
    }


    override suspend fun deleteHabit(habitId: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
            .delete()
    }

    override suspend fun getHabits(): Habits {
        val userId = auth.currentUser?.uid ?: ""

        val querySnapshot = firestore.collection("users")
            .document(userId)
            .collection("habits").get()

        val habitList = querySnapshot.documents.map { documentSnapshot ->
                documentSnapshot.data<Habit>()
        }

        return Habits(items = habitList)
    }

    override suspend fun getPosts(): Posts {
        val querySnapshot = firestore.collection("posts").get()

        val postsList = querySnapshot.documents.map { documentSnapshot ->
            documentSnapshot.data<Post>()
        }

        val postsWithAuthorName = postsList.map { post ->
            val userDoc = firestore.collection("users").document(post.createdByUserId).get()
            val user = userDoc.data<User>()
            val userName = user.name ?: "Unknown"
            post.copy(authorName = userName)
        }

        val sortedPosts = postsWithAuthorName.sortedByDescending { it.createdAt }

        return Posts(items = sortedPosts)
    }

    override suspend fun signInAnonymously() {
        if (Firebase.auth.currentUser == null) {
            Firebase.auth.signInAnonymously();
        }
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }
}