package com.example.paymentsrecording.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** 分类图标名 -> Material 图标 */
object CategoryIcons {
    fun fromName(name: String): ImageVector = when (name) {
        "Restaurant" -> Icons.Outlined.Restaurant
        "DirectionsBus" -> Icons.Outlined.DirectionsBus
        "ShoppingBag" -> Icons.Outlined.ShoppingBag
        "SportsEsports" -> Icons.Outlined.SportsEsports
        "Home" -> Icons.Outlined.Home
        "LocalHospital" -> Icons.Outlined.LocalHospital
        "School" -> Icons.Outlined.School
        "PhoneIphone" -> Icons.Outlined.PhoneIphone
        "Flight" -> Icons.Outlined.Flight
        "Devices" -> Icons.Outlined.Devices
        "ShoppingBasket" -> Icons.Outlined.ShoppingBasket
        "Work" -> Icons.Outlined.Work
        "CardGiftcard" -> Icons.Outlined.CardGiftcard
        "TrendingUp" -> Icons.Outlined.TrendingUp
        "Replay" -> Icons.Outlined.Replay
        "MoreHoriz" -> Icons.Outlined.MoreHoriz
        else -> Icons.Outlined.Category
    }
}

fun Long.toColor(): Color = Color(this)
