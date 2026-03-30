package com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity;

public enum CurrencyType {

    DIAMONDS("💎"),
    GOLD("🥇");

    private final String icon;

    CurrencyType(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

}
