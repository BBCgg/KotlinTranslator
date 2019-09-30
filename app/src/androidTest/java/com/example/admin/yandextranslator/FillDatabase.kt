package com.example.admin.yandextranslator

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FillDatabase {
    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun addData(){
        for (i in 0..100){
            onView(withId(R.id.text_for_translate)).perform(clearText(),
                    typeText("example " + i.toString() ))

            onView(withId(R.id.translateBtn)).perform(click())
        }
    }

    @Test
    fun addNumbers(){
        for (i in 0..10){
            onView(withId(R.id.text_for_translate)).perform(
                    typeText(i.toString() ))

            onView(withId(R.id.translateBtn)).perform(click())
        }
    }
}