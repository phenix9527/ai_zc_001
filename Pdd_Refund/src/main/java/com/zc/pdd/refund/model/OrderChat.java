package com.zc.pdd.refund.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.zc.pdd.refund.enums.ChatStatus;

public record OrderChat(@JsonPropertyDescription("订单号") String orderId
        , @JsonPropertyDescription("用户Id") String userId
        , @JsonPropertyDescription("对话Id") String chatId
        , @JsonPropertyDescription("对话状态") ChatStatus status) {

}