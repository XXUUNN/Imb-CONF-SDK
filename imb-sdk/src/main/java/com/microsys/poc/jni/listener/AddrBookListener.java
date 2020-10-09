package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.AddressBook;

/**
 * AddrBookListener
 *
 * @author zhangcd
 * <p>
 * 2014-11-24
 */
public interface AddrBookListener extends BaseJniListener {

    void onRecvAddrBook(AddressBook addressBook);

}
