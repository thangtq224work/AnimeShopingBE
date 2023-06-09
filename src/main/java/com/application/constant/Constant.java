package com.application.constant;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static class DateFormat {
        public static final String FORMAT_DATE = "dd/MM/yyyy hh:mm:ss";
    }

    public static class PropertyStatus {
        public static final Byte ACTIVE = 1;
        public static final Byte NON_ACTIVE = 0;

    }

    public static class Status {
        public static final Boolean ACTIVE = true;
        public static final Boolean NON_ACTIVE = false;
    }
    public static class AccountRole{
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
        public static final String CLIENT = "CLIENT";
        public static List<String> getRoles(){
            return List.of(ADMIN,USER,CLIENT);
        }
//        public static final Long USER_ID = 2l;
//        public static final Long ADMIN_ID = 1l;
//        public static final Long CLIENT_ID = 3l;
    }

}
