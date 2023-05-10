package com.aizuda.easy.retry.common.core.model;

import lombok.Data;

import java.util.UUID;

/**
 * @author www.byteblogs.com
 * @date 2022-03-08
 * @since 2.0
 */
@Data
public class EasyRetryRequest {

   private String requestId;

   private Object[] args;

   public EasyRetryRequest(Object... args) {
      this.args = args;
      this.requestId = generateRequestId();
   }

   public EasyRetryRequest() {

   }

   public String generateRequestId() {
      return UUID.randomUUID().toString();
   }

}
