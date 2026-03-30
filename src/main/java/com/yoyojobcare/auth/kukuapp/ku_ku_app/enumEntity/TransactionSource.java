package com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity;

public enum TransactionSource {

    PURCHASE, // Screenshot 1: Recharge Diamonds
    EXCHANGE_DIAMONDS, // Screenshot 5: Diamonds to Gold
    EXCHANGE_GOLD, // If gold can be exchanged to diamonds
    TASK_REWARD, // Screenshot 2, 3, 4: Task rewards
    DAILY_LOGIN, // Screenshot 4: Gold can be gained by signing in.
    GIFT_SENT, // For deducting when sending gifts
    GIFT_RECEIVED, // For crediting when receiving gifts
    ADMIN_CREDIT,
    ADMIN_DEBIT,
    LEVEL_REWARD,
    EVENT_REWARD,
    ROOM_HOSTING,
    PENALTY

}
