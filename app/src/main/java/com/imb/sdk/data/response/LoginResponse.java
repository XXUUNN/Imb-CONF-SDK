package com.imb.sdk.data.response;

public class LoginResponse extends BaseResponse {

    /**
     * status : 200
     * msg : success
     * data : {"token":"","user_id":""}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token :
         * user_id :
         */

        private String token;
        private String user_id;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
