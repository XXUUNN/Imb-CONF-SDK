package com.imb.sdk.center; /**
 * 账户正常状态 允许使用所有功能
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author Administrator
 */
@IntDef({AccountState.STATE_NORMAL, AccountState.STATE_EXPIRED, AccountState.STATE_ONLY_LOOK})
@Retention(RetentionPolicy.SOURCE)
public @interface AccountState {
    int STATE_NORMAL = 0;

    /**
     * 账户过期 需要购买
     */
    int STATE_EXPIRED = 1;

    /**
     * 账户不允许创建会议
     */
    int STATE_ONLY_LOOK = 2;
}