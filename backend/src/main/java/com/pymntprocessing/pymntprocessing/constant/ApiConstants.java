package com.pymntprocessing.pymntprocessing.constant;

public class ApiConstants {
    public static final String API_BASE_PATH = "/api";

    /**
     * Version 1.0
     */
    public static class V1 {
        public static final String V1_API_BASE_PATH = API_BASE_PATH + "/v1";

        /**
         * Vendor API
         */
        public static class Vendor {
            public static final String VENDOR_PATH = V1_API_BASE_PATH + "/vendor";
        }

        /**
         * Payment Transaction API
         */
        public static class PaymentTransaction {
            public static final String PAYMENT_TRANSACTION_PATH = V1_API_BASE_PATH + "/transaction";
        }

        /**
         * Invoice API
         */
        public static class Invoice {
            public static final String INVOICE_PATH = V1_API_BASE_PATH + "/invoice";
        }

        /**
         * Invoice API
         */
        public static class Product {
            public static final String PRODUCT_PATH = V1_API_BASE_PATH + "/product";
        }
    }
}
