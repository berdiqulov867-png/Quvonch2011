package com.example

import com.example.data.model.UserRole
import com.example.util.PasswordSecurity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun passwordSecurity_hashingAndVerification_worksCorrectly() {
        val password = "Admin@2026!"
        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(password, salt)

        // Verifying correct password
        assertTrue(PasswordSecurity.verifyPassword(password, salt, hash))

        // Verifying wrong password fails
        assertFalse(PasswordSecurity.verifyPassword("WrongPassword123", salt, hash))

        // Unique salts produce different hashes
        val anotherSalt = PasswordSecurity.generateSalt()
        val anotherHash = PasswordSecurity.hashPassword(password, anotherSalt)
        assertNotEquals(hash, anotherHash)
    }

    @Test
    fun userRoles_displayNamesInUzbek_areAccurate() {
        assertEquals("Administrator", UserRole.ADMIN.displayNameUz)
        assertEquals("O'qituvchi", UserRole.TEACHER.displayNameUz)
        assertEquals("O'quvchi", UserRole.STUDENT.displayNameUz)
        assertEquals("Ota-ona", UserRole.PARENT.displayNameUz)
    }

    @Test
    fun gradeCalculations_100pointSystem_averagesMatch() {
        val scores = listOf(92, 88, 95, 90)
        val average = scores.average()
        assertEquals(91.25, average, 0.01)
    }
}
