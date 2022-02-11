package my.istts.finalproject.models.ipaymu;

public class PaymentInput {
    private String key;
    private int amount, price; //amount untuk transfer, cstore & qris    price untuk va
    private String notifyUrl;
    private String notify_url;
    private String name;
    private String phone;
    private String email;

    //untuk indomaret/alfamart
    private String sessionID;
    private String channel;

    public PaymentInput(String key, int amount, int price, String notifyUrl, String notify_url, String name, String phone, String email, String channel, String sessionID) {
        this.key = key;
        this.amount = amount;
        this.price = price;
        this.notifyUrl = notifyUrl;
        this.notify_url = notify_url;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.channel = channel;
        this.sessionID = sessionID;
    }

    public String getKey() {
        return key;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getChannel() {
        return channel;
    }

    public String getSessionID() {
        return sessionID;
    }
}
