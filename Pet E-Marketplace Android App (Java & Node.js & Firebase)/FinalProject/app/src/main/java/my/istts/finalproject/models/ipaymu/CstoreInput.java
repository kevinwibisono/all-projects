package my.istts.finalproject.models.ipaymu;

public class CstoreInput {
    private String key;
    private int quantity, price; //amount untuk transfer, cstore & qris    price untuk va
    private String product;
    private String unotify;
    private String pay_method;

    public CstoreInput(String key, int quantity, int price, String product, String unotify, String pay_method) {
        this.key = key;
        this.quantity = quantity;
        this.price = price;
        this.product = product;
        this.unotify = unotify;
        this.pay_method = pay_method;
    }

    public String getKey() {
        return key;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getProduct() {
        return product;
    }

    public String getUnotify() {
        return unotify;
    }

    public String getPay_method() {
        return pay_method;
    }
}
