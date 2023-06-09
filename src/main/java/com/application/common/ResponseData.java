package com.application.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // thuoc tinh nao null se bo qua , khong duoc Serialization
public class ResponseData {
    private int code;
    private String message;
    private Object data;

    public static class Builder {
        private int code;
        private String message;
        private Object data;

        public Builder builderFromObject(ResponseData response) {
            this.code = response.code;
            this.message = response.message;
            this.data = response.data;
            return this;
        }
        public Builder builder(){
            return this;
        }
        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResponseData build() {
            return new ResponseData(this.code, this.message, this.data);
        }
    }
}
