package com.microsys.poc.jni.entity;

import java.util.ArrayList;
import java.util.List;

public class AddressInfo {


    /**
     * tel : 88995500
     * orientation : 1
     * map : 1
     * esip_rule :
     * video : 1
     * message : 1
     * location_rate : 30
     * user_status : []
     * monitor : 1
     * heartbeat_timelen : 120
     * user_info : [{"did":"263","st":"logout","tel":"88995501","uid":"2","name":"88995501"},{"did":"10","st":"logout","tel":"88995509","uid":"10","name":"88995509"},{"did":"6","st":"logout","tel":"88995505","uid":"6","name":"88995505"},{"did":"7","st":"logout","tel":"88995506","uid":"7","name":"88995506"},{"did":"233","st":"login","tel":"88996106","uid":"24","name":"poc操作员"},{"did":"214","st":"logout","tel":"88995507","uid":"8","name":"88995507"},{"did":"9","st":"logout","tel":"88995508","uid":"9","name":"88995508"},{"did":"5","st":"logout","tel":"88995504","uid":"5","name":"88995504"},{"did":"248","st":"logout","tel":"88995503","uid":"4","name":"88995503"},{"did":"265","st":"login","tel":"889950","uid":"41","name":"889950"},{"did":"97","st":"logout","tel":"88995502","uid":"3","name":"88995502"}]
     * bc_info : [{"phone_model":"WaterproofK1","key_value":"111","extra_action":"","release":"android.intent.action.PTT.up","config_type":"1","action":"","press":"android.intent.action.PTT.down","phone_brand":"1111"}]
     * picture : 1
     * group_info : [{"vgcs_tel":"889955","did":"261","bc_tel":"","gid":"11","user_list":"2/3/4/5/6/7/8/9/10/41/24","group_type":"0","name":"组呼"}]
     * map_port : 8899
     * del_did :
     * vlc : 0
     * monitor_mtel : 88996001
     * name : 88995500
     * vrecord : 1
     * group_ddbstatus : [{"gid":"11"}]
     * ddb_flg : 1
     */

    private String tel;
    private String orientation;
    private int map;
    private String esip_rule;
    private int video;
    private String message;
    private String location_rate;
    private String monitor;
    private String heartbeat_timelen;
    private String picture;
    private String map_port;
    private String del_did;
    private int vlc;
    private String monitor_mtel;
    private String name;
    private String vrecord;
    private String ddb_flg;
    private String mediaserver_info;
    private List<UserStatusBean> user_status;
    private List<UserInfoBean> user_info;
    private List<OrganizationInfoBean> organization_info;
    private List<BcInfoBean> bc_info;
    private List<GroupInfoBean> group_info;
    private List<GroupDdbstatusBean> group_ddbstatus;

    public String getMediaserver_info() {
        return mediaserver_info;
    }

    public void setMediaserver_info(String mediaserver_info) {
        this.mediaserver_info = mediaserver_info;
    }

    public List<OrganizationInfoBean> getOrganization_info() {
        return organization_info;
    }

    public void setOrganization_info(List<OrganizationInfoBean> organization_info) {
        this.organization_info = organization_info;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public String getEsip_rule() {
        return esip_rule;
    }

    public void setEsip_rule(String esip_rule) {
        this.esip_rule = esip_rule;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation_rate() {
        return location_rate;
    }

    public void setLocation_rate(String location_rate) {
        this.location_rate = location_rate;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getHeartbeat_timelen() {
        return heartbeat_timelen;
    }

    public void setHeartbeat_timelen(String heartbeat_timelen) {
        this.heartbeat_timelen = heartbeat_timelen;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMap_port() {
        return map_port;
    }

    public void setMap_port(String map_port) {
        this.map_port = map_port;
    }

    public String getDel_did() {
        return del_did;
    }

    public void setDel_did(String del_did) {
        this.del_did = del_did;
    }

    public int getVlc() {
        return vlc;
    }

    public void setVlc(int vlc) {
        this.vlc = vlc;
    }

    public String getMonitor_mtel() {
        return monitor_mtel;
    }

    public void setMonitor_mtel(String monitor_mtel) {
        this.monitor_mtel = monitor_mtel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVrecord() {
        return vrecord;
    }

    public void setVrecord(String vrecord) {
        this.vrecord = vrecord;
    }

    public String getDdb_flg() {
        return ddb_flg;
    }

    public void setDdb_flg(String ddb_flg) {
        this.ddb_flg = ddb_flg;
    }

    public List<UserStatusBean> getUser_status() {
        return user_status;
    }

    public void setUser_status(List<UserStatusBean> user_status) {
        this.user_status = user_status;
    }

    public List<UserInfoBean> getUser_info() {
        return user_info;
    }

    public void setUser_info(List<UserInfoBean> user_info) {
        this.user_info = user_info;
    }

    public List<BcInfoBean> getBc_info() {
        return bc_info;
    }

    public void setBc_info(List<BcInfoBean> bc_info) {
        this.bc_info = bc_info;
    }

    public List<GroupInfoBean> getGroup_info() {
        return group_info;
    }

    public void setGroup_info(List<GroupInfoBean> group_info) {
        this.group_info = group_info;
    }

    public List<GroupDdbstatusBean> getGroup_ddbstatus() {
        return group_ddbstatus;
    }

    public void setGroup_ddbstatus(List<GroupDdbstatusBean> group_ddbstatus) {
        this.group_ddbstatus = group_ddbstatus;
    }

    public static class UserInfoBean {
        /**
         * did : 263
         * st : logout
         * tel : 88995501
         * uid : 2
         * name : 88995501
         */

        private String did;
        private String st;
        private String tel;
        private String uid;
        private String name;

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getSt() {
            return st;
        }

        public void setSt(String st) {
            this.st = st;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class OrganizationInfoBean{


        private String name;
        private String did;
        private String oid;
        private String user_list;
        private String parent_oid;
        private List<User> userList;
        private List<Integer> uidList;

        public OrganizationInfoBean(String name, String did, String oid, String user_list, String parent_oid) {
            this.name = name;
            this.did = did;
            this.oid = oid;
            this.user_list = user_list;
            this.parent_oid = parent_oid;
            if(user_list != null){

                setUserList(user_list);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getUser_list() {
            return user_list;
        }

        public void setUser_list(String user_list) {
            this.user_list = user_list;
        }

        public String getParent_oid() {
            return parent_oid;
        }

        public void setParent_oid(String parent_oid) {
            this.parent_oid = parent_oid;
        }

        /*
         *
         *将string 转化为List
         */
        public void setUserList(String user_list){
            List<User> userList = new ArrayList<>();
            List<Integer> uidList = new ArrayList<>();
            if(!"".equals(user_list)){

                String[]  users =user_list.split("/");
                for(String uid : users){
// TODO: 2019/4/23 查询用户
//                    //获得用户对象
//                    User user = Constants.allUserMap.get(Integer.valueOf(uid));
//                    userList.add(user);
//                    uidList.add(user.getUid());
                }
            }
            this.userList = userList;
            this.uidList = uidList;
        }

        /**
         * 把111/11/22/44 这样类型的数据转换为list 方便使用
         * @return
         */
        public List<User> getUserList(){

            return  userList;
        }


        /**
         * 获得这个组织下面的所有人员的uid
         * @return
         */
        public List<Integer> getUidList(){
            return uidList;
        }


    }

    public static class UserStatusBean{
        /**
         *
         */
        private String uid;
        private String st;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSt() {
            return st;
        }

        public void setSt(String st) {
            this.st = st;
        }
    }
    public static class BcInfoBean {
        /**
         * phone_model : WaterproofK1
         * key_value : 111
         * extra_action :
         * release : android.intent.action.PTT.up
         * config_type : 1
         * action :
         * press : android.intent.action.PTT.down
         * phone_brand : 1111
         */

        private String phone_model;
        private String key_value;
        private String extra_action;
        private String release;
        private String config_type;
        private String action;
        private String press;
        private String phone_brand;

        public String getPhone_model() {
            return phone_model;
        }

        public void setPhone_model(String phone_model) {
            this.phone_model = phone_model;
        }

        public String getKey_value() {
            return key_value;
        }

        public void setKey_value(String key_value) {
            this.key_value = key_value;
        }

        public String getExtra_action() {
            return extra_action;
        }

        public void setExtra_action(String extra_action) {
            this.extra_action = extra_action;
        }

        public String getRelease() {
            return release;
        }

        public void setRelease(String release) {
            this.release = release;
        }

        public String getConfig_type() {
            return config_type;
        }

        public void setConfig_type(String config_type) {
            this.config_type = config_type;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getPress() {
            return press;
        }

        public void setPress(String press) {
            this.press = press;
        }

        public String getPhone_brand() {
            return phone_brand;
        }

        public void setPhone_brand(String phone_brand) {
            this.phone_brand = phone_brand;
        }
    }

    public static class GroupInfoBean {
        /**
         * vgcs_tel : 889955
         * did : 261
         * bc_tel :
         * gid : 11
         * user_list : 2/3/4/5/6/7/8/9/10/41/24
         * group_type : 0
         * name : 组呼
         */

        private String vgcs_tel;
        private String did;
        private String bc_tel;
        private String gid;
        private String user_list;
        private String group_type;
        private String name;

        public String getVgcs_tel() {
            return vgcs_tel;
        }

        public void setVgcs_tel(String vgcs_tel) {
            this.vgcs_tel = vgcs_tel;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getBc_tel() {
            return bc_tel;
        }

        public void setBc_tel(String bc_tel) {
            this.bc_tel = bc_tel;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getUser_list() {
            return user_list;
        }

        public void setUser_list(String user_list) {
            this.user_list = user_list;
        }

        public String getGroup_type() {
            return group_type;
        }

        public void setGroup_type(String group_type) {
            this.group_type = group_type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class GroupDdbstatusBean {
        /**
         * gid : 11
         */

        private String gid;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }
    }
}
