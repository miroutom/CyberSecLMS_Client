package hse.diploma.cybersecplatform.mock

import hse.diploma.cybersecplatform.data.model.UserData

const val mockNewPassword = "verySecp2."

const val mockAvatarUrl = "https://example.com/avatar.jpg"

val mockNewUser =
    UserData(
        username = "lika",
        fullName = "lika s",
        email = "example.com",
        avatarUrl = mockAvatarUrl,
    )
