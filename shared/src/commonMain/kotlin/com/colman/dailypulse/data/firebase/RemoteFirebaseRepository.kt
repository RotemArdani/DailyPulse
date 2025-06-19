package com.colman.dailypulse.data.firebase

import co.touchlab.kermit.Logger
import com.colman.dailypulse.models.Habit
import com.colman.dailypulse.models.Habits
import com.colman.dailypulse.models.Post
import com.colman.dailypulse.models.Posts
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

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
        docRef.set(post.copy(id = docRef.id))
    }

    override suspend fun updateHabit(habit: Habit) {
        val userId = auth.currentUser?.uid ?: return
        val habitId = habit.id ?: return // Don't proceed if ID is missing

        val habitRef = firestore
            .collection("users")
            .document(userId)
            .collection("habits").document(habitId)

        habitRef.set(habit) // Overwrites the document with the new post data
    }


    override suspend fun deleteHabit(habit: Habit) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.id.toString())
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

        return Posts(items = postsList)
    }

    override suspend fun signInAnonymously() {
        if (Firebase.auth.currentUser == null) {
            Firebase.auth.signInAnonymously();
        }
    }

    override suspend fun getHabitDetails(habitId: String): Habit? {
        TODO("Not yet implemented")
    }
}