package com.example.breaktime

import com.example.breaktime.domain.BreakTimeValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreakTimeValidationTests {

    // Test 31: Empty input
    @Test
    fun test31_emptyInput() {
        val emptyHours = BreakTimeValidator.validateWorkInput("", "0")
        assertTrue(emptyHours is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work hours cannot be empty.", (emptyHours as BreakTimeValidator.ValidationResult.Invalid).message)

        val emptyMinutes = BreakTimeValidator.validateWorkInput("8", "")
        assertTrue(emptyMinutes is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work minutes cannot be empty.", (emptyMinutes as BreakTimeValidator.ValidationResult.Invalid).message)

        val emptyBreakHours = BreakTimeValidator.validateBreakInput("", "30")
        assertTrue(emptyBreakHours is BreakTimeValidator.ValidationResult.Invalid)

        val emptyBreakMinutes = BreakTimeValidator.validateBreakInput("0", "")
        assertTrue(emptyBreakMinutes is BreakTimeValidator.ValidationResult.Invalid)
    }

    // Test 32: Negative input
    @Test
    fun test32_negativeInput() {
        val negHours = BreakTimeValidator.validateWorkInput("-1", "0")
        assertTrue(negHours is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work hours cannot be negative.", (negHours as BreakTimeValidator.ValidationResult.Invalid).message)

        val negMinutes = BreakTimeValidator.validateWorkInput("8", "-15")
        assertTrue(negMinutes is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work minutes cannot be negative.", (negMinutes as BreakTimeValidator.ValidationResult.Invalid).message)

        val negBreakHours = BreakTimeValidator.validateBreakInput("-2", "0")
        assertTrue(negBreakHours is BreakTimeValidator.ValidationResult.Invalid)

        val negBreakMinutes = BreakTimeValidator.validateBreakInput("0", "-30")
        assertTrue(negBreakMinutes is BreakTimeValidator.ValidationResult.Invalid)
    }

    // Test 33: Decimal input
    @Test
    fun test33_decimalInput() {
        val decHours = BreakTimeValidator.validateWorkInput("8.5", "0")
        assertTrue(decHours is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work hours must be a whole non-negative number.", (decHours as BreakTimeValidator.ValidationResult.Invalid).message)

        val decMinutes = BreakTimeValidator.validateWorkInput("8", "30.5")
        assertTrue(decMinutes is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work minutes must be a whole non-negative number.", (decMinutes as BreakTimeValidator.ValidationResult.Invalid).message)
    }

    // Test 34: Non-numeric input
    @Test
    fun test34_nonNumericInput() {
        val textHours = BreakTimeValidator.validateWorkInput("eight", "0")
        assertTrue(textHours is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work hours must be a whole non-negative number.", (textHours as BreakTimeValidator.ValidationResult.Invalid).message)

        val textMinutes = BreakTimeValidator.validateWorkInput("8", "fifteen")
        assertTrue(textMinutes is BreakTimeValidator.ValidationResult.Invalid)
        assertEquals("Work minutes must be a whole non-negative number.", (textMinutes as BreakTimeValidator.ValidationResult.Invalid).message)
    }
}
