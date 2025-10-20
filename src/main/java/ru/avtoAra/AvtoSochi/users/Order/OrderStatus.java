package ru.avtoAra.AvtoSochi.users.Order;

public enum OrderStatus {
    PLACED("Оформлен"),
    IN_TRANSIT("В пути"),
    AT_PICKUP_POINT("В пункте выдачи"),
    DELIVERED("Товар у покупателя"),
    RETURN_REQUESTED("Оформлен возврат"),
    Canceled("Отменён"),
    RETURN_COMPLETED("Возврат выполнен");




    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canCancel() {
        return this == PLACED || this == IN_TRANSIT;
    }

    public boolean canReturn() {
        return this == AT_PICKUP_POINT || this == DELIVERED;
    }

    public boolean isReturnRequested() {
        return this == RETURN_REQUESTED || this == RETURN_COMPLETED;
    }

}
