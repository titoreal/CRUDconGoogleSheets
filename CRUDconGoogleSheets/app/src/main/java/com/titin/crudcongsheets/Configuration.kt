package com.titin.crudcongsheets

import java.util.regex.Pattern;

object Configuration {
    private const val BASE_WEB_APP_URL =
        "ESCRIBE AQUI LA URL DE TU PROPIA IMPLEMENTACION"

    const val ADD_USER_URL = BASE_WEB_APP_URL
    const val LIST_USER_URL = "$BASE_WEB_APP_URL?action=readAll"

    private const val DRIVE_VIEW_PATTERN = "/d/([^/]+)/view"
    private const val DRIVE_ID_PATTERN = "id=([^&]+)"

    object Keys {
        const val ID = "uId"
        const val NAME = "uName"
        const val APELLIDO = "uApellido"
        const val PHONE = "uPhone"
        const val IMAGE = "uImage"
        const val ACTION = "action"
        const val USERS = "records"
    }

    fun extractDirectImageLink(originalUrl: String?): String {
        if (originalUrl.isNullOrEmpty()) return ""

        val patterns = listOf(DRIVE_VIEW_PATTERN, DRIVE_ID_PATTERN)

        patterns.forEach { pattern ->
            Pattern.compile(pattern).matcher(originalUrl).let { matcher ->
                if (matcher.find()) {
                    return "https://drive.google.com/uc?export=view&id=${matcher.group(1)}"
                }
            }
        }
        return originalUrl
    }
}
