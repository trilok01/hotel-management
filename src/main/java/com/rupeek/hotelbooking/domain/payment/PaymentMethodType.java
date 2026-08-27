package com.rupeek.hotelbooking.domain.payment;

public final class PaymentMethodType {

    public static final PaymentMethodType CARD = new PaymentMethodType("CARD");
    public static final PaymentMethodType UPI = new PaymentMethodType("UPI");
    public static final PaymentMethodType WALLET = new PaymentMethodType("WALLET");

    private final String code;

    public PaymentMethodType(String code) {
        this.code = code.trim().toUpperCase();
    }

    public static PaymentMethodType of(String code) {
        return new PaymentMethodType(code);
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentMethodType)) return false;
        return code.equals(((PaymentMethodType) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
