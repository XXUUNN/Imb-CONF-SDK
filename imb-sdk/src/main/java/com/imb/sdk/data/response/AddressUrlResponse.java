package com.imb.sdk.data.response;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author - gongxun;
 * created on 2019/4/25-10:27;
 * description - 服务端返回的通讯录数据
 */
public class AddressUrlResponse {

    /**
     * tel : 8001
     * orientation : 1
     * map : 1
     * esip_rule :
     * video : 1
     * message : 1
     * user_status : [{"uid":"1","st":"logout"}]
     * monitor : 1
     * mediaserver_info : 192.168.2.200:8081
     * heartbeat_timelen : 120
     * user_info : [{"did":"164","st":"logout","tel":"8070","uid":"94","name":"黄进"},{"did":"48","st":"logout","tel":"8012","uid":"36","name":"邓继武"},{"did":"50","st":"logout","tel":"8013","uid":"37","name":"刘猛"},{"did":"152","st":"logout","tel":"8064","uid":"88","name":"金国庆"},{"did":"142","st":"logout","tel":"8059","uid":"83","name":"王涛"},{"did":"170","st":"logout","tel":"8073","uid":"97","name":"朱刚"},{"did":"52","st":"logout","tel":"8014","uid":"38","name":"陈子楷"},{"did":"144","st":"logout","tel":"8060","uid":"84","name":"吴琦"},{"did":"54","st":"logout","tel":"8015","uid":"39","name":"缪彬威"},{"did":"150","st":"logout","tel":"8063","uid":"87","name":"陈莹雪"},{"did":"102","st":"logout","tel":"8039","uid":"63","name":"章晓明"},{"did":"56","st":"logout","tel":"8016","uid":"40","name":"商克亮"},{"did":"58","st":"logout","tel":"8017","uid":"41","name":"占立晨"},{"did":"148","st":"logout","tel":"8062","uid":"86","name":"冯依琳"},{"did":"92","st":"logout","tel":"8034","uid":"58","name":"胡智慧"},{"did":"60","st":"logout","tel":"8018","uid":"42","name":"齐甜甜"},{"did":"156","st":"logout","tel":"8066","uid":"90","name":"余天标"},{"did":"62","st":"logout","tel":"8019","uid":"43","name":"严斌"},{"did":"130","st":"logout","tel":"8053","uid":"77","name":"陈新辉"},{"did":"112","st":"logout","tel":"8044","uid":"68","name":"郑美英"},{"did":"154","st":"logout","tel":"8065","uid":"89","name":"魏晓娟"},{"did":"64","st":"logout","tel":"8020","uid":"44","name":"张学乾"},{"did":"176","st":"logout","tel":"8076","uid":"100","name":"焦俊杰"},{"did":"66","st":"logout","tel":"8021","uid":"45","name":"董然"},{"did":"136","st":"logout","tel":"8056","uid":"80","name":"许文波"},{"did":"104","st":"logout","tel":"8040","uid":"64","name":"郑小军"},{"did":"162","st":"logout","tel":"8069","uid":"93","name":"岳晗"},{"did":"68","st":"logout","tel":"8022","uid":"46","name":"赵超阳"},{"did":"70","st":"logout","tel":"8023","uid":"47","name":"赵灵刚"},{"did":"94","st":"logout","tel":"8035","uid":"59","name":"叶福君"},{"did":"168","st":"logout","tel":"8072","uid":"96","name":"许叶佳"},{"did":"72","st":"logout","tel":"8024","uid":"48","name":"马李鑫"},{"did":"74","st":"logout","tel":"8025","uid":"49","name":"孔令辉"},{"did":"114","st":"logout","tel":"8045","uid":"69","name":"余鹏琴"},{"did":"76","st":"logout","tel":"8026","uid":"50","name":"严凯"},{"did":"78","st":"logout","tel":"8027","uid":"51","name":"龚循"},{"did":"106","st":"logout","tel":"8041","uid":"65","name":"王怡闻"},{"did":"80","st":"logout","tel":"8028","uid":"52","name":"丁杭梁"},{"did":"96","st":"logout","tel":"8036","uid":"60","name":"方志刚"},{"did":"82","st":"logout","tel":"8029","uid":"53","name":"余纯阳"},{"did":"128","st":"logout","tel":"8052","uid":"76","name":"何长飞"},{"did":"32","st":"logout","tel":"8004","uid":"28","name":"钱海良"},{"did":"84","st":"logout","tel":"8030","uid":"54","name":"苏铮俊"},{"did":"86","st":"logout","tel":"8031","uid":"55","name":"刘希哲"},{"did":"134","st":"logout","tel":"8055","uid":"79","name":"林涛"},{"did":"116","st":"logout","tel":"8046","uid":"70","name":"程勇华"},{"did":"160","st":"logout","tel":"8068","uid":"92","name":"何旭初"},{"did":"88","st":"logout","tel":"8032","uid":"56","name":"虞景皓"},{"did":"90","st":"logout","tel":"8033","uid":"57","name":"杨德胜"},{"did":"140","st":"logout","tel":"8058","uid":"82","name":"袁帅"},{"did":"108","st":"logout","tel":"8042","uid":"66","name":"汪银霞"},{"did":"166","st":"logout","tel":"8071","uid":"95","name":"孙鹏"},{"did":"28","st":"logout","tel":"8002","uid":"26","name":"吴燕"},{"did":"30","st":"logout","tel":"8003","uid":"27","name":"刘秋华"},{"did":"98","st":"logout","tel":"8037","uid":"61","name":"关红星"},{"did":"172","st":"logout","tel":"8074","uid":"98","name":"秦康雯"},{"did":"24","st":"logout","tel":"8000","uid":"24","name":"总经理"},{"did":"146","st":"logout","tel":"8061","uid":"85","name":"施卓敏"},{"did":"34","st":"logout","tel":"8005","uid":"29","name":"宋怡婷"},{"did":"118","st":"logout","tel":"8047","uid":"71","name":"徐凤清"},{"did":"174","st":"logout","tel":"8075","uid":"99","name":"宁宇光"},{"did":"36","st":"logout","tel":"8006","uid":"30","name":"吴琰栋"},{"did":"38","st":"logout","tel":"8007","uid":"31","name":"钟佳琪"},{"did":"126","st":"logout","tel":"8051","uid":"75","name":"周斌"},{"did":"110","st":"logout","tel":"8043","uid":"67","name":"叶素华"},{"did":"40","st":"logout","tel":"8008","uid":"32","name":"张金龙"},{"did":"124","st":"logout","tel":"8050","uid":"74","name":"刘月平"},{"did":"42","st":"logout","tel":"8009","uid":"33","name":"徐博"},{"did":"132","st":"logout","tel":"8054","uid":"78","name":"卢聪杰"},{"did":"100","st":"logout","tel":"8038","uid":"62","name":"毛小妹"},{"did":"158","st":"logout","tel":"8067","uid":"91","name":"徐洁"},{"did":"44","st":"logout","tel":"8010","uid":"34","name":"毛增慧"},{"did":"122","st":"logout","tel":"8049","uid":"73","name":"王挺"},{"did":"46","st":"logout","tel":"8011","uid":"35","name":"詹俊波"},{"did":"138","st":"logout","tel":"8057","uid":"81","name":"丁静涛"},{"did":"120","st":"logout","tel":"8048","uid":"72","name":"毛积法"}]
     * location_rate : 60
     * organization_info : [{"did":"117","oid":"14","parent_oid":"5","user_list":"69/70","name":"人事财务部"},{"did":"119","oid":"15","parent_oid":"5","user_list":"71","name":"工程测试部"},{"did":"121","oid":"16","parent_oid":"5","user_list":"72","name":"办公室"},{"did":"143","oid":"17","parent_oid":"6","user_list":"75/76/83/77/78/82/79/80/81/73/74","name":"销售公司"},{"did":"149","oid":"18","parent_oid":"6","user_list":"85/84/86","name":"财务部"},{"did":"157","oid":"19","parent_oid":"6","user_list":"87/88/90/89","name":"保障部"},{"did":"161","oid":"20","parent_oid":"6","user_list":"91/92","name":"营销中心"},{"did":"177","oid":"21","parent_oid":"6","user_list":"100/97/98/99","name":"系统调试及维护部"},{"did":"167","oid":"22","parent_oid":"20","user_list":"93/94/95","name":"销售部"},{"did":"95","oid":"4","parent_oid":"1","user_list":"59","name":"美播云"},{"did":"169","oid":"23","parent_oid":"20","user_list":"96","name":"市场部"},{"did":"1","oid":"1","parent_oid":"0","user_list":"","name":"杭州迈可行通信股份有限公司"},{"did":"29","oid":"2","parent_oid":"1","user_list":"24/26","name":"总经办"},{"did":"33","oid":"3","parent_oid":"1","user_list":"28/27","name":"研究院"},{"did":"5","oid":"5","parent_oid":"1","user_list":"","name":"通宽广"},{"did":"6","oid":"6","parent_oid":"1","user_list":"","name":"迈可行"},{"did":"35","oid":"7","parent_oid":"3","user_list":"29","name":"版本管理部"},{"did":"49","oid":"8","parent_oid":"3","user_list":"36/30/31/32/33/34/35","name":"测试中心"},{"did":"69","oid":"9","parent_oid":"3","user_list":"44/37/38/39/40/41/46/42/43/45","name":"融合平台研发中心"},{"did":"87","oid":"10","parent_oid":"3","user_list":"52/53/54/47/55/48/49/50/51","name":"美播云研发中心"},{"did":"93","oid":"11","parent_oid":"3","user_list":"56/57/58","name":"指挥装备研发中心"},{"did":"99","oid":"12","parent_oid":"5","user_list":"60/61","name":"研发中心"},{"did":"113","oid":"13","parent_oid":"5","user_list":"68/62/63/64/65/66/67","name":"生产管理部"}]
     * picture : 1
     * group_info : [{"vgcs_tel":"889955","did":"261","bc_tel":"","gid":"11","user_list":"2/3/4/5/6/7/8/9/10/41/24","group_type":"0","name":"组呼"}]
     * map_port : 8899
     * del_did :
     * vlc : 1
     * monitor_mtel : 999
     * name : 吴婷
     * vrecord : 1
     * bc_info : [{"phone_model":"WaterproofK1","key_value":"111","extra_action":"","release":"android.intent.action.PTT.up","config_type":"1","action":"","press":"android.intent.action.PTT.down","phone_brand":"1111"}]
     * group_ddbstatus : [{"gid":"11"}]
     * ddb_flg : 1
     * oid : 33
     */

    @JSONField(name = "tel")
    public String tel;
    @JSONField(name ="orientation")
    public int orientation;
    @JSONField(name ="map")
    public int map;
    @JSONField(name ="esip_rule")
    public String esipRule;
    @JSONField(name ="video")
    public int video;
    @JSONField(name ="message")
    public int message;
    @JSONField(name ="monitor")
    public String monitor;
//    @JSONField(name ="mediaserver_info")
//    public String mediaserverInfo;
    @JSONField(name ="heartbeat_timelen")
    public String heartbeatTimelen;
    @JSONField(name ="location_rate")
    public String locationRate;
    @JSONField(name ="picture")
    public int picture;
    @JSONField(name ="map_port")
    public String mapPort;
    @JSONField(name ="del_did")
    public String delDid;
    @JSONField(name ="vlc")
    public int vlc;
    @JSONField(name ="monitor_mtel")
    public String monitorMtel;
    @JSONField(name ="name")
    public String name;
    @JSONField(name ="vrecord")
    public int vrecord;
    @JSONField(name ="ddb_flg")
    public String ddbFlg;
    @JSONField(name ="user_info")
    public List<UserInfo> userInfo;
    @JSONField(name ="organization_info")
    public List<OrganizationInfo> organizationInfo;
    @JSONField(name ="group_info")
    public List<GroupInfo> groupInfo;
    @JSONField(name ="bc_info")
    public List<BcInfo> bcInfo;
    @JSONField(name ="group_ddbstatus")
    public List<GroupDdbstatus> groupDdbstatus;
    @JSONField(name = "uid")
    public long userId;
    @JSONField(name = "display_photo")
    public String headshot;

    /**
     * 服务端的通话模式
     * 0 点对点通话客户端直接对发数据
     * 1 所有数据由服务端转发 客户端只和服务端通信
     * 默认 1 所有经过服务器转发
     */
    public int mediaTransportMode = 1;


    public static class UserInfo {
        /**
         * did : 164
         * st : logout
         * tel : 8070
         * uid : 94
         * name : 黄进
         */

        @JSONField(name ="did")
        public long did;
        @JSONField(name ="st")
        public String st;
        @JSONField(name ="tel")
        public String tel;
        @JSONField(name ="uid")
        public long uid;
        @JSONField(name ="name")
        public String name;
        @JSONField(name = "display_photo")
        public String headshot;
    }

    public static class OrganizationInfo {
        /**
         * did : 117
         * oid : 14
         * parent_oid : 5
         * user_list : 69/70
         * name : 人事财务部
         */

        @JSONField(name ="did")
        public long did;
        @JSONField(name ="oid")
        public long oid;
        @JSONField(name ="parent_oid")
        public long parentOid;
        @JSONField(name ="user_list")
        public String userList;
        @JSONField(name ="name")
        public String name;
    }

    public static class GroupInfo {
        /**
         * vgcs_tel : 889955
         * did : 261
         * bc_tel :
         * gid : 11
         * user_list : 2/3/4/5/6/7/8/9/10/41/24
         * group_type : 0
         * name : 组呼
         */

        @JSONField(name ="vgcs_tel")
        public String vgcsTel;
        @JSONField(name ="did")
        public long did;
        @JSONField(name ="bc_tel")
        public String bcTel;
        @JSONField(name ="gid")
        public long gid;
        @JSONField(name ="user_list")
        public String userList;
        @JSONField(name ="group_type")
        public int groupType;
        @JSONField(name ="name")
        public String name;
        @JSONField(name = "create_user")
        public String createUser;
    }

    public static class BcInfo {
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

        @JSONField(name ="phone_model")
        public String phoneModel;
        @JSONField(name ="key_value")
        public String keyValue;
        @JSONField(name ="extra_action")
        public String extraAction;
        @JSONField(name ="release")
        public String release;
        @JSONField(name ="config_type")
        public String configType;
        @JSONField(name ="action")
        public String action;
        @JSONField(name ="press")
        public String press;
        @JSONField(name ="phone_brand")
        public String phoneBrand;
    }

    public static class GroupDdbstatus {
        /**
         * gid : 11
         */

        @JSONField(name ="gid")
        public long gid;
    }
}
