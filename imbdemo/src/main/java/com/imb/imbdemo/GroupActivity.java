package com.imb.imbdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.imb.imbdemo.entity.ContactEntity;
import com.imb.imbdemo.entity.ContactGroupEntity;
import com.imb.sdk.Poc;
import com.imb.sdk.addressbook.AddressBookSyncByHttp;
import com.imb.sdk.listener.PocAddressBookChangeListener;
import com.imb.sdk.listener.PocGroupEditListener;
import com.imb.sdk.util.GroupOperationHelper;
import com.microsys.poc.jni.entity.GroupOperatingResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Object> dataList = new ArrayList<>();
    private MyAdapter myAdapter;

    private ContactGroupEntity curGroup;
    private Handler handler;
    private SharedPreferences configSp;
    private String num;
    private String syncHttpUrl;
    private PocAddressBookChangeListener pocAddressBookChangeListener;
    private PocGroupEditListener pocGroupEditListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        setTitle("工作组");

        configSp = Sp.getSp(this);
        num = configSp.getString(Sp.POC_NUM, null);
        String pocServer = configSp.getString(Sp.POC_SERVER, null);
        this.syncHttpUrl = AddressBookSyncByHttp.getSyncAddressBookUrl(pocServer);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList.addAll(MainActivity.groups);
        myAdapter = new MyAdapter(dataList);
        myAdapter.bindToRecyclerView(recyclerView);
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Object obj = adapter.getItem(position);
                if (obj instanceof ContactGroupEntity) {
                    ContactGroupEntity item = (ContactGroupEntity) obj;
                    showGroup(item);
                }
            }
        });
        myAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Object obj = adapter.getItem(position);
                if (obj instanceof ContactGroupEntity) {
                    ContactGroupEntity contactGroupEntity = (ContactGroupEntity) obj;
                    final String number = contactGroupEntity.getNumber();
                    new AlertDialog.Builder(GroupActivity.this).setMessage("是否删除工作组")
                            .setNegativeButton("否", null)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GroupOperationHelper.deleteGroup(number);
                                }
                            }).show();

                } else {
                    ContactEntity contactEntity = (ContactEntity) obj;
                    final String number = contactEntity.getNumber();
                    new AlertDialog.Builder(GroupActivity.this).setMessage("是否删除工作组中的人员")
                            .setNegativeButton("否", null)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (TextUtils.equals(number, num)) {
                                        // TODO: 2020/11/9 主动过滤掉删除自己的操作
                                        Toast.makeText(GroupActivity.this, "不能删除自己", Toast.LENGTH_SHORT).show();
                                    } else {
                                        GroupOperationHelper.deleteMember(curGroup.getNumber(), number);
                                    }
                                }
                            }).show();
                }
                return true;
            }
        });

        observeAddressBook();
        observeGroupOperation();

    }

    private void observeGroupOperation() {
        pocGroupEditListener = new PocGroupEditListener() {
            @Override
            protected void onGroupOperatingResult(final GroupOperatingResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String operation;
                        switch (result.type) {
                            case CLOSE:
                                operation = "删除组";
                                break;
                            case CREATE:
                                operation = "创建组";
                                break;
                            case USERDEL:
                                operation = "删除人员";
                                break;
                            case USERADD:
                                operation = "添加人员到组";
                                break;
                            case NAMEMOD:
                                operation = "组明更新";
                                break;
                            default:
                                operation = "";
                        }
                        String optResult = result.isOk ? " 操作成功" : " 操作失败";
                        Toast.makeText(GroupActivity.this, operation + optResult, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        Poc.registerListener(pocGroupEditListener);
    }

    private void unObserveGroupOperation() {
        Poc.unregisterListener(pocGroupEditListener);
    }

    private void observeAddressBook() {
        handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AddressBookSyncByHttp.getAddressBook(num, syncHttpUrl, new AddressBookSyncByHttp.Callback() {
                    @Override
                    public void callback(boolean isOk, String msg) {
                        if (!isOk) {
                            return;
                        }
                        if (msg == null) {
                            return;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.contacts.clear();
                                MainActivity.groups.clear();
                                MainActivity.depts.clear();
                                AddressBookHandle.handleContent(msg, MainActivity.contacts, MainActivity.groups, MainActivity.depts);
                                refreshUI();
                            }
                        });
                    }
                });
            }
        };
        pocAddressBookChangeListener = new PocAddressBookChangeListener() {
            @Override
            protected void onUpdateAddressBook() {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 1000);
            }
        };
        Poc.registerListener(pocAddressBookChangeListener);
    }

    private void refreshUI() {
        dataList.clear();
        if (curGroup != null) {
            for (ContactGroupEntity group : MainActivity.groups) {
                if (TextUtils.equals(group.getNumber(), curGroup.getNumber())) {
                    dataList.addAll(group.getContactsList());
                    curGroup = group;
                    setTitle("工作组:" + curGroup.getName() + "_" + curGroup.getNumber());
                }
            }
        }
        if (dataList.isEmpty()) {
            dataList.addAll(MainActivity.groups);
        }
        myAdapter.notifyDataSetChanged();
    }

    private void unObserveAddressBook() {
        Poc.unregisterListener(pocAddressBookChangeListener);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void showGroup(ContactGroupEntity entity) {
        if (entity != null) {
            dataList.clear();
            dataList.addAll(entity.getContactsList());
            myAdapter.notifyDataSetChanged();
            curGroup = entity;
            setTitle("工作组:" + curGroup.getName() + "_" + curGroup.getNumber());
        }
    }

    public void onBackClick(View view) {
        if (curGroup != null) {
            dataList.clear();
            dataList.addAll(MainActivity.groups);
            myAdapter.notifyDataSetChanged();
            curGroup = null;
            setTitle("工作组");
        }
    }

    public void onAddGroupClick(View view) {
        new GroupAddWindow(this).show(findViewById(android.R.id.content));
    }

    public void onEditGroupClick(View view) {
        if (curGroup != null) {
            new GroupEditWindow(this, curGroup).show(findViewById(android.R.id.content));
        } else {
            Toast.makeText(this, "先选择组", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        unObserveGroupOperation();
        unObserveAddressBook();
        super.onDestroy();
    }

    private static class MyAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

        public MyAdapter(@Nullable List<Object> data) {
            super(data);

            setMultiTypeDelegate(new MultiTypeDelegate<Object>() {
                @Override
                protected int getItemType(Object o) {
                    if (o instanceof ContactGroupEntity) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            getMultiTypeDelegate().registerItemTypeAutoIncrease(R.layout.item_contact, R.layout.item_group);
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            if (item instanceof ContactGroupEntity) {
                ContactGroupEntity contactGroupEntity = (ContactGroupEntity) item;
                helper.setText(R.id.tv_name, contactGroupEntity.getName() + "_" + contactGroupEntity.getNumber() + "_创建者：" + ((ContactGroupEntity) item).getCreatorNum());
            } else {
                ContactEntity entity = (ContactEntity) item;
                helper.setText(R.id.tv_name, entity.getName() + "_" + entity.getNumber());
            }
        }
    }

    private static class GroupAddWindow {
        private PopupWindow window;
        private ArrayList<ContactEntity> contactEntities;

        public GroupAddWindow(final Context context) {
            this.window = new PopupWindow(context);
            View contentView = LayoutInflater.from(context).inflate(R.layout.layout_window_add_group, null
            );
            window.setContentView(contentView);
            window.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            window.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            window.setFocusable(true);
            final SharedPreferences sp = Sp.getSp(context);
            final String myNum = sp.getString(Sp.POC_NUM, null);
            RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerview);
            final EditText editText = (EditText) contentView.findViewById(R.id.edit_group_name);
            Button button = (Button) contentView.findViewById(R.id.btn_confirm);

            List<ContactEntity> contacts = MainActivity.contacts;
            contactEntities = new ArrayList<>(contacts);
            for (int i = 0; i < contactEntities.size(); i++) {
                final ContactEntity contactEntity = contactEntities.get(i);
                if (TextUtils.equals(contactEntity.getNumber(), myNum)) {
                    //是自己。不放在选择列表里 ，添加的是其他人才能创建组，自己是必须添加的
                    contactEntities.remove(i);
                    break;
                }
            }
            GroupAddAdapter groupAddAdapter = new GroupAddAdapter(contactEntities);
            final HashMap<Long, ContactEntity> selected = new HashMap<>();
            groupAddAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    if (checkBox.isChecked()) {
                        final ContactEntity contactEntity = contactEntities.get(position);
                        selected.remove(contactEntity.getDid());
                        checkBox.setChecked(false);
                    } else {
                        final ContactEntity contactEntity = contactEntities.get(position);
                        selected.put(contactEntity.getDid(), contactEntity);
                        checkBox.setChecked(true);
                    }
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            groupAddAdapter.bindToRecyclerView(recyclerView);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String s = editText.getText().toString();
                    if (TextUtils.isEmpty(s)) {
                        Toast.makeText(context, "请输入名字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selected.isEmpty()) {
                        Toast.makeText(context, "请选择成员", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> collect = new ArrayList<>();
                    Collection<ContactEntity> values = selected.values();
                    for (ContactEntity value : values) {
                        collect.add(value.getNumber());
                    }
//                    List<String> collect = selected.values().stream().map(new Function<ContactEntity, String>() {
//                        @Override
//                        public String apply(ContactEntity contactEntity) {
//                            return contactEntity.getNumber();
//                        }
//                    }).collect(Collectors.toList());
                    GroupOperationHelper.createDynamicGroup(s, collect, myNum);
                    window.dismiss();
                }
            });
        }

        private void show(View anchor) {
            window.showAtLocation(anchor, Gravity.CENTER, 0, 0);
        }


        private static class GroupAddAdapter extends BaseQuickAdapter<ContactEntity, BaseViewHolder> {

            public GroupAddAdapter(@Nullable List<ContactEntity> data) {
                super(R.layout.item_group_select_member, data);
            }

            @Override
            protected void convert(BaseViewHolder helper, ContactEntity item) {
                helper.setText(R.id.tv_name, item.getName() + "_" + item.getNumber());
            }
        }
    }

    private static class GroupEditWindow {
        private PopupWindow window;
        private ArrayList<ContactEntity> contactEntities;

        public GroupEditWindow(final Context context, ContactGroupEntity groupEntity) {
            this.window = new PopupWindow(context);
            View contentView = LayoutInflater.from(context).inflate(R.layout.layout_window_edit_group, null
            );
            window.setContentView(contentView);
            window.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            window.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            window.setFocusable(true);
            final SharedPreferences sp = Sp.getSp(context);
            final String myNum = sp.getString(Sp.POC_NUM, null);
            RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerview);
            final EditText editText = (EditText) contentView.findViewById(R.id.edit_group_name);
            Button button = (Button) contentView.findViewById(R.id.btn_confirm);
            editText.setText(groupEntity.getName());
            editText.setSelection(groupEntity.getName().length());

            List<ContactEntity> contacts = MainActivity.contacts;
            contactEntities = new ArrayList<>();
            //去除已经在组里的了
            List<ContactEntity> old = groupEntity.getContactsList();
            final HashMap<Long, ContactEntity> map = new HashMap<>();
            for (int i = 0; i < old.size(); i++) {
                final ContactEntity entity = old.get(i);
                map.put(entity.getDid(), entity);
            }
            for (int i = 0; i < contacts.size(); i++) {
                ContactEntity contactEntity = contacts.get(i);
                if (!map.containsKey(contactEntity.getDid())) {
                    contactEntities.add(contactEntity);
                }
            }
            GroupAddAdapter groupAddAdapter = new GroupAddAdapter(contactEntities);
            final HashMap<Long, ContactEntity> selected = new HashMap<>();
            groupAddAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    if (checkBox.isChecked()) {
                        final ContactEntity contactEntity = contactEntities.get(position);
                        selected.remove(contactEntity.getDid());
                        checkBox.setChecked(false);
                    } else {
                        final ContactEntity contactEntity = contactEntities.get(position);
                        selected.put(contactEntity.getDid(), contactEntity);
                        checkBox.setChecked(true);
                    }
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            groupAddAdapter.bindToRecyclerView(recyclerView);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String s = editText.getText().toString();
                    if (TextUtils.isEmpty(s)) {
                        Toast.makeText(context, "请输入名字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selected.isEmpty() && TextUtils.equals(s, groupEntity.getName())) {
                        Toast.makeText(context, "请选择成员或者更新组名", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!selected.isEmpty()) {
                        ArrayList<String> collect = new ArrayList<>();
                        Collection<ContactEntity> values = selected.values();
                        for (ContactEntity value : values) {
                            collect.add(value.getNumber());
                        }
                        GroupOperationHelper.addMembers(groupEntity.getNumber(), groupEntity.getType(),
                                collect, myNum);
                    }

                    if (!TextUtils.equals(groupEntity.getName(), s)) {
                        GroupOperationHelper.updateGroupName(groupEntity.getNumber(), s);
                    }

                    window.dismiss();
                }
            });
        }

        private void show(View anchor) {
            window.showAtLocation(anchor, Gravity.CENTER, 0, 0);
        }


        private static class GroupAddAdapter extends BaseQuickAdapter<ContactEntity, BaseViewHolder> {

            public GroupAddAdapter(@Nullable List<ContactEntity> data) {
                super(R.layout.item_group_select_member, data);
            }

            @Override
            protected void convert(BaseViewHolder helper, ContactEntity item) {
                helper.setText(R.id.tv_name, item.getName() + "_" + item.getNumber());
            }
        }
    }
}
