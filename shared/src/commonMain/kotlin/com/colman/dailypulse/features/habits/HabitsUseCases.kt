package com.colman.dailypulse.features.habits

import com.colman.dailypulse.domian.habits.CreateHabit
import com.colman.dailypulse.domian.habits.DeleteHabit
import com.colman.dailypulse.domian.habits.GetHabitDetails
import com.colman.dailypulse.domian.habits.GetHabits
import com.colman.dailypulse.domian.habits.OnHabitDone
import com.colman.dailypulse.domian.habits.UpdateHabit
import com.colman.dailypulse.domian.posts.CreatePost

class HabitsUseCases(
    val getHabits: GetHabits,
    val createHabit: CreateHabit,
    val updateHabit: UpdateHabit,
    val onHabitDone: OnHabitDone,
    val onDeleteHabit: DeleteHabit,
    val getHabitDetails: GetHabitDetails,
    val createPost: CreatePost
)