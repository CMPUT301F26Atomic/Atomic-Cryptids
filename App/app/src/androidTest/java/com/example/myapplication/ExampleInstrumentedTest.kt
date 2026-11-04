package com.example.myapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4


import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.atomiccryptids.campsiteapp.MainActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule


import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.myapplication", appContext.packageName)
    }
}
// //AI tests for autosign in. Tests passed
@RunWith(AndroidJUnit4::class)
class AutoSignInTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()
    private fun waitForSignIn() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            FirebaseAuth.getInstance().currentUser != null
        }
    }
    @Test
    fun signsInAutomaticallyFromSignedOutState() {
        FirebaseAuth.getInstance().signOut()
        composeRule.activityRule.scenario.recreate()
        waitForSignIn()
        val user = FirebaseAuth.getInstance().currentUser
        assertNotNull(user)
        assertTrue(user!!.isAnonymous)
    }
    @Test
    fun noUsernameOrPasswordPromptIsShown() {
        waitForSignIn()
        composeRule.onNodeWithText("Username").assertDoesNotExist()
        composeRule.onNodeWithText("Password").assertDoesNotExist()
    }
    @Test
    fun sameDeviceGetsSameUserAfterRestart() {
        waitForSignIn()
        val firstUid = FirebaseAuth.getInstance().currentUser?.uid
        composeRule.activityRule.scenario.recreate()
        waitForSignIn()
        assertEquals(firstUid, FirebaseAuth.getInstance().currentUser?.uid)
    }
}