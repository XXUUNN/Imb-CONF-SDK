package com.imb.sdk.data.response;

public class CheckVersionResponse extends BaseResponse {


    /**
     * code : 200
     * data : {"id":"1","appName":"center","type":"android","version":"1.0.0.1","url":"www","fileKey":null,"status":1,"content":"test","isDelete":null,"updateTime":"2019-07-11 19:59:03","createTime":null}
     * msg : null
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
         * id : 1
         * appName : center
         * type : android
         * version : 1.0.0.1
         * url : www
         * fileKey : null
         * status : 1
         * content : test
         * isDelete : null
         * updateTime : 2019-07-11 19:59:03
         * createTime : null
         */

        private String id;
        private String appName;
        private String type;
        private String version;
        private String url;
        private Object fileKey;
        private int status;
        private String content;
        private Object isDelete;
        private String updateTime;
        private Object createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Object getFileKey() {
            return fileKey;
        }

        public void setFileKey(Object fileKey) {
            this.fileKey = fileKey;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Object getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(Object isDelete) {
            this.isDelete = isDelete;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public Object getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Object createTime) {
            this.createTime = createTime;
        }
    }
}
