class AuthValidationTest {

    @Test
    fun `isPhoneValid returns true for valid Russian phone number`() {
        assertTrue(isPhoneValid("+79991234567"))
    }

    @Test
    fun `isPhoneValid returns false for invalid phone number`() {
        assertFalse(isPhoneValid("1234567890"))
    }

    @Test
    fun `isEmailValid returns true for valid emails`() {
        assertTrue(isEmailValid("example@example.com"))
        assertTrue(isEmailValid("user.name+tag+sorting@example.com"))
    }

    @Test
    fun `isEmailValid returns false for invalid emails`() {
        assertFalse(isEmailValid("example.com")) // Нет @
        assertFalse(isEmailValid("example@.com")) // Нет домена
        assertFalse(isEmailValid("@example.com")) // Нет имени пользователя
    }
}
