package com.example

import com.example.data.models.PatientProfile
import com.example.data.models.UserRole
import com.example.security.SecurityManager
import com.example.security.SystemPermission
import org.junit.Assert.*
import org.junit.Test

class SecurityManagerUnitTest {

    @Test
    fun testRbacPermissions() {
        // Super admin should have all permissions
        assertTrue(SecurityManager.hasPermission(UserRole.SUPER_ADMIN, SystemPermission.RESET_FINANCIALS))
        assertTrue(SecurityManager.hasPermission(UserRole.SUPER_ADMIN, SystemPermission.MANAGE_EMPLOYEES))

        // Pharmacist should have inventory dispense but not reset financials
        assertTrue(SecurityManager.hasPermission(UserRole.PHARMACIST, SystemPermission.DISPENSE_MEDICINE))
        assertFalse(SecurityManager.hasPermission(UserRole.PHARMACIST, SystemPermission.RESET_FINANCIALS))

        // Doctor should have medical record write permissions
        assertTrue(SecurityManager.hasPermission(UserRole.DOCTOR, SystemPermission.WRITE_MEDICAL_RECORD))
        assertFalse(SecurityManager.hasPermission(UserRole.DOCTOR, SystemPermission.MANAGE_FINANCIALS))
    }

    @Test
    fun testInputSanitization() {
        val rawInput = "<script>alert('xss');</script> John Doe ' OR 1=1 --"
        val sanitized = SecurityManager.sanitizeInput(rawInput)
        assertFalse(sanitized.contains("<"))
        assertFalse(sanitized.contains(">"))
        assertFalse(sanitized.contains("'"))
        // The implementation removes < > & ' " \
        assertEquals("scriptalert(xss);/script John Doe  OR 1=1 --", sanitized)
    }

    @Test
    fun testPiiMasking() {
        val phone = "0501234567"
        val maskedPhone = SecurityManager.maskPhoneNumber(phone)
        assertEquals("050****67", maskedPhone)

        val nid = "1098765432"
        val maskedNid = SecurityManager.maskNationalId(nid)
        assertEquals("10******32", maskedNid)
    }

    @Test
    fun testAuditHashSignature() {
        val logPayload = "User DOCTOR performed action: ADD_PATIENT at 2026-08-02"
        val hash1 = SecurityManager.hashAuditSignature(logPayload)
        val hash2 = SecurityManager.hashAuditSignature(logPayload)

        assertNotNull(hash1)
        assertEquals(64, hash1.length) // SHA-256 length hex
        assertEquals(hash1, hash2)
    }

    @Test
    fun testPatientProfileCreation() {
        val profile = PatientProfile(
            id = 1L,
            name = "أحمد محمود",
            phone = "0501122334",
            email = "ahmed@example.com",
            medicalHistory = "ضغط الدم العالي",
            age = 45,
            gender = "ذكر",
            bloodType = "O+"
        )

        assertEquals("أحمد محمود", profile.name)
        assertEquals("0501122334", profile.phone)
        assertEquals("ضغط الدم العالي", profile.medicalHistory)
    }
}
