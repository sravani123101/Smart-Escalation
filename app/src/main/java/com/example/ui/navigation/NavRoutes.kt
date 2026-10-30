package com.example.ui.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val ADD_MEDICINE = "add_medicine"
    const val EDIT_MEDICINE = "edit_medicine/{medicineId}"
    const val MEDICINE_DETAILS = "medicine_details/{medicineId}"
    const val PHOTO_CONFIRMATION = "photo_confirmation/{reminderId}"
    const val CONFIRMATION_SUCCESS = "confirmation_success/{reminderId}/{hasPhoto}"
    const val TRUSTED_CONTACTS = "trusted_contacts"
    const val ESCALATION_HISTORY = "escalation_history"
    const val MEDICINE_HISTORY = "medicine_history"
    const val ANALYTICS = "analytics"
    const val STOCK_MANAGEMENT = "stock_management"
    const val SETTINGS = "settings"
    const val USER_PROFILE = "user_profile"
    const val PRIVACY_DATA = "privacy_data"

    fun editMedicineRoute(id: Long) = "edit_medicine/$id"
    fun medicineDetailsRoute(id: Long) = "medicine_details/$id"
    fun photoConfirmationRoute(id: Long) = "photo_confirmation/$id"
    fun confirmationSuccessRoute(id: Long, hasPhoto: Boolean) = "confirmation_success/$id/$hasPhoto"
}
