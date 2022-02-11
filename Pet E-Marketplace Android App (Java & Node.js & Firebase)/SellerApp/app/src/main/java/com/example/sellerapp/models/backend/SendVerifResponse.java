package com.example.sellerapp.models.backend;

import java.util.List;

public class SendVerifResponse {
    private List<SentMsgDetail> Sent;

    public SendVerifResponse(List<SentMsgDetail> sent) {
        Sent = sent;
    }

    public List<SentMsgDetail> getSent() {
        return Sent;
    }
}
