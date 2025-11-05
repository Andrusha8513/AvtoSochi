package ru.avtoAra.AvtoSochi.users.DTO;

public  class CartItemDto {

        private Long id;
        private int quantity;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

}
