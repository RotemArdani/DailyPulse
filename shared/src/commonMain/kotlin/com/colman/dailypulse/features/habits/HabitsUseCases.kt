package com.colman.dailypulse.features.habits

import com.colman.dailypulse.domian.habits.CreateHabit
import com.colman.dailypulse.domian.habits.GetHabits
import com.colman.dailypulse.domian.habits.OnHabitDone
import com.colman.dailypulse.domian.habits.UpdateHabit

class HabitsUseCases(
    val getHabits: GetHabits,
    val createHabit: CreateHabit,
    val updateHabit: UpdateHabit,
    val onHabitDone: OnHabitDone
)