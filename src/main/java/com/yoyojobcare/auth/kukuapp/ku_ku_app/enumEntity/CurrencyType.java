package com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity;

public enum CurrencyType {

    DIAMONDS("diamonds"),
    GOLD("gold");

    private final String currencyTypeName;

    CurrencyType(String currencyTypeName) {
        this.currencyTypeName = currencyTypeName;
    }

    public String getIcon() {
        return currencyTypeName;
    }

}
