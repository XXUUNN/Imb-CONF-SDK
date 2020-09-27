package com.imb.sdk.data.response;

/**
 * @author Administrator
 */
public class ChangePwdResponse extends BaseResponse{

    /**
     * status : 200
     * msg : success
     * data : {"user_id":""}
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
         * user_id :
         */

        private String user_id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
