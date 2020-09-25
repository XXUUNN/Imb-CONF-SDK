package com.microsys.poc.jni.entity;


import com.microsys.poc.jni.entity.type.CallBackInfoType;

/**
 * 所有回调参数列表
 * @author Qiudq
 *
 */
public class CallBackInfo {
	public GroupEditResult getTempGroup() {
		return tempGroup;
	}
	private AddressBook addressbook;
	private SelfInfo selfInfo;
	private SipMsg sipMsg;
	private TbcpMsg tbcpMsg;
	private TextMsg txtMsg;
	private UserState userState;
	private Resolution resulution;
	private boolean isTwoLoading;
	private LocalPower local;
	private CallBackInfoType infoType;
	private VersionUpdateEvent updateEvent;
	private GroupEditResult tempGroup;
	private CallTypeChange callTypeChange;
	private SktInfo sktInfo;
	private PwdInfo pwdInfo;
	private GroupListenStateInfo groupListenStateInfo;
	private EsipHeadMsg esipHeadMsg;
	private SynGroupListenStateInfo synGroupListenStateInfo;
	private AddressUrl addressUrl;
	private UserTypeInfo userTypeInfo;

	private UserChangeInMeeting userChangeInMeeting;

	public UserChangeInMeeting getUserChangeInMeeting() {
		return userChangeInMeeting;
	}

	public void setUserChangeInMeeting(UserChangeInMeeting userChangeInMeeting) {
		this.userChangeInMeeting = userChangeInMeeting;
	}

	public GroupListenStateInfo getGroupListenStateInfo() {
		return groupListenStateInfo;
	}
	public void setGroupListenStateInfo(GroupListenStateInfo groupListenStateInfo) {
		this.groupListenStateInfo = groupListenStateInfo;
	}
	public PwdInfo getPwdInfo() {
		return pwdInfo;
	}
	public void setPwdInfo(PwdInfo pwdInfo) {
		this.pwdInfo = pwdInfo;
	}
	public VersionUpdateEvent getUpdateEvent() {
		return updateEvent;
	}
	public void setUpdateEvent(VersionUpdateEvent updateEvent) {
		this.updateEvent = updateEvent;
	}
	public AddressBook getAddressbook() {
		return addressbook;
	}
	public void setAddressbook(AddressBook addressbook) {
		this.addressbook = addressbook;
	}
	public SelfInfo getSelfInfo() {
		return selfInfo;
	}
	public void setSelfInfo(SelfInfo selfInfo) {
		this.selfInfo = selfInfo;
	}
	public SipMsg getSipMsg() {
		return sipMsg;
	}
	public void setSipMsg(SipMsg sipMsg) {
		this.sipMsg = sipMsg;
	}
	public TbcpMsg getTbcpMsg() {
		return tbcpMsg;
	}
	public void setTbcpMsg(TbcpMsg tbcpMsg) {
		this.tbcpMsg = tbcpMsg;
	}
	public TextMsg getTxtMsg() {
		return txtMsg;
	}
	public void setTxtMsg(TextMsg txtMsg) {
		this.txtMsg = txtMsg;
	}
	public UserState getUserState() {
		return userState;
	}
	public void setUserState(UserState userState) {
		this.userState = userState;
	}
	public CallBackInfoType getInfoType() {
		return infoType;
	}
	public void setInfoType(CallBackInfoType infoType) {
		this.infoType = infoType;
	}
	public Resolution getResulution() {
		return resulution;
	}
	public void setResulution(Resolution resulution) {
		this.resulution = resulution;
	}
	public boolean isTwoLoading() {
		return isTwoLoading;
	}
	public void setTwoLoading(boolean isTwoLoading) {
		this.isTwoLoading = isTwoLoading;
	}
	public LocalPower getLocal() {
		return local;
	}
	public void setLocal(LocalPower local) {
		this.local = local;
	}
	public void setTempGroup(GroupEditResult tempGroup) {
		this.tempGroup = tempGroup;
	}
	public CallTypeChange getCallTypeChange() {
		return callTypeChange;
	}
	public void setCallTypeChange(CallTypeChange callTypeChange) {
		this.callTypeChange = callTypeChange;
	}
	public SktInfo getSktInfo() {
		return sktInfo;
	}
	public void setSktInfo(SktInfo sktInfo) {
		this.sktInfo = sktInfo;
	}
	public EsipHeadMsg getEsipHeadMsg() {
		return esipHeadMsg;
	}
	public void setEsipHeadMsg(EsipHeadMsg esipHeadMsg) {
		this.esipHeadMsg = esipHeadMsg;
	}
	public SynGroupListenStateInfo getSynGroupListenStateInfo() {
		return synGroupListenStateInfo;
	}
	public void setSynGroupListenStateInfo(
			SynGroupListenStateInfo synGroupListenStateInfo) {
		this.synGroupListenStateInfo = synGroupListenStateInfo;
	}
	public AddressUrl getAddressUrl() {
		return addressUrl;
	}
	public void setAddressUrl(AddressUrl addressUrl) {
		this.addressUrl = addressUrl;
	}
	public UserTypeInfo getUserTypeInfo() {
		return userTypeInfo;
	}
	public void setUserTypeInfo(UserTypeInfo userTypeInfo) {
		this.userTypeInfo = userTypeInfo;
	}
	
	
}
