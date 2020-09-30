package com.imb.sdk.data.response;

import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 */
public class LoginAccountInfo extends BaseResponse {


    /**
     * code : 200
     * data : {"id":"sdrtwer","accountId":"1","userName":"jack","userType":0,"nickName":"kkk","imageUrl":null,"mobile":null,"email":null,"instanceDetailVOS":[{"id":"ilNlfajUe","accountId":"a87iTjUd9","accountName":"SaaS","instName":"sadfas","instType":"live","description":"sad","serverHost":"192.168.1.143","serverPort":12,"username":null,"password":null,"capacity":1,"gpsHost":null,"gpsPort":null,"status":1,"meetingTime":"2019-07-04 10:29:19","expireType":0,"expirationTime":"2019-07-27"},{"id":"ilNlfajUe","accountId":"a87iTjUd9","accountName":"SaaS","instName":"sadfas","instType":"live","description":"sad","serverHost":"192.168.1.143","serverPort":12,"username":null,"password":null,"capacity":1,"gpsHost":null,"gpsPort":null,"status":1,"meetingTime":"2019-07-04 10:29:19","expireType":0,"expirationTime":"2019-07-27"}]}
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
         * id : sdrtwer
         * accountId : 1
         * fileName : "dfdfdf.jpg"
         * userName : jack
         * userType : 0
         * nickName : kkk
         * imageUrl : null
         * mobile : null
         * email : null
         * instanceDetailVOS : [{"id":"ilNlfajUe","accountId":"a87iTjUd9","accountName":"SaaS","instName":"sadfas","instType":"live","description":"sad","serverHost":"192.168.1.143","serverPort":12,"username":null,"password":null,"capacity":1,"gpsHost":null,"gpsPort":null,"status":1,"meetingTime":"2019-07-04 10:29:19","expireType":0,"expirationTime":"2019-07-27"},{"id":"ilNlfajUe","accountId":"a87iTjUd9","accountName":"SaaS","instName":"sadfas","instType":"live","description":"sad","serverHost":"192.168.1.143","serverPort":12,"username":null,"password":null,"capacity":1,"gpsHost":null,"gpsPort":null,"status":1,"meetingTime":"2019-07-04 10:29:19","expireType":0,"expirationTime":"2019-07-27"}]
         */

        private String id;
        private String accountId;
        private String userName;
        private int userType;
        private String nickName;
        private String fileName;
        private String mobile;
        private String email;

        /**
         * 最新的会议的
         */
        private String meetingSubject;

        /**
         * 未读的会议的个数
         */
        private int notifyCount;

        /**
         * 最新的会议的创建时间
         */
        private Date meetingTime;

        private List<InstanceDetailVOSBean> instanceDetailVOS;

        public String getMeetingSubject() {
            return meetingSubject;
        }

        public void setMeetingSubject(String meetingSubject) {
            this.meetingSubject = meetingSubject;
        }

        public int getNotifyCount() {
            return notifyCount;
        }

        public void setNotifyCount(int notifyCount) {
            this.notifyCount = notifyCount;
        }

        public Date getMeetingTime() {
            return meetingTime;
        }

        public void setMeetingTime(Date meetingTime) {
            this.meetingTime = meetingTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<InstanceDetailVOSBean> getInstanceDetailVOS() {
            return instanceDetailVOS;
        }

        public void setInstanceDetailVOS(List<InstanceDetailVOSBean> instanceDetailVOS) {
            this.instanceDetailVOS = instanceDetailVOS;
        }

        public static class InstanceDetailVOSBean {
            /**
             * id : ilNlfajUe
             * accountId : a87iTjUd9
             * accountName : SaaS
             * instName : sadfas
             * instType : live
             * description : sad
             * serverHost : 192.168.1.143
             * serverPort : 12
             * username : null
             * password : null
             * capacity : 1
             * gpsHost : null
             * gpsPort : null
             * status : 1
             * meetingTime : 2019-07-04 10:29:19
             * expireType : 0
             * expirationTime : 2019-07-27
             */

            private String id;
            private String accountId;
            private String accountName;
            private String instName;
            private String instType;
            private String description;
            private String serverHost;
            private int serverPort;
            private String username;
            private String password;
            private int capacity;
            private String gpsHost;
            private String gpsPort;
            private int status;
            private String createTime;
            private int expireType;
            private String expirationTime;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAccountId() {
                return accountId;
            }

            public void setAccountId(String accountId) {
                this.accountId = accountId;
            }

            public String getAccountName() {
                return accountName;
            }

            public void setAccountName(String accountName) {
                this.accountName = accountName;
            }

            public String getInstName() {
                return instName;
            }

            public void setInstName(String instName) {
                this.instName = instName;
            }

            public String getInstType() {
                return instType;
            }

            public void setInstType(String instType) {
                this.instType = instType;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getServerHost() {
                return serverHost;
            }

            public void setServerHost(String serverHost) {
                this.serverHost = serverHost;
            }

            public int getServerPort() {
                return serverPort;
            }

            public void setServerPort(int serverPort) {
                this.serverPort = serverPort;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public int getCapacity() {
                return capacity;
            }

            public void setCapacity(int capacity) {
                this.capacity = capacity;
            }

            public String getGpsHost() {
                return gpsHost;
            }

            public void setGpsHost(String gpsHost) {
                this.gpsHost = gpsHost;
            }

            public String getGpsPort() {
                return gpsPort;
            }

            public void setGpsPort(String gpsPort) {
                this.gpsPort = gpsPort;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public int getExpireType() {
                return expireType;
            }

            public void setExpireType(int expireType) {
                this.expireType = expireType;
            }

            public String getExpirationTime() {
                return expirationTime;
            }

            public void setExpirationTime(String expirationTime) {
                this.expirationTime = expirationTime;
            }
        }
    }
}
