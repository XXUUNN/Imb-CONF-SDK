package com.imb.imbdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.imb.imbdemo.entity.ContactEntity;
import com.imb.imbdemo.entity.DepartmentEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddressBookActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private List<Object> dataList = new ArrayList<>();
    private MyAdapter myAdapter;

    private DepartmentEntity curDept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        setTitle("组织结构");

        recyclerView = findViewById(R.id.recyclerview);
        //找到顶级部门
        final DepartmentEntity topDept = findTopDept();
        if (topDept == null || (topDept.getDeptList() == null && topDept.getContactList() == null)) {
            //没有子部门和联系人
            Toast.makeText(this, "数据有误", Toast.LENGTH_SHORT).show();
        } else {
            if (topDept.getDeptList() != null) {
                dataList.addAll(topDept.getDeptList());
            }
            if (topDept.getContactList() != null) {
                dataList.addAll(topDept.getContactList());
            }

            curDept = topDept;

            myAdapter = new MyAdapter(dataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter.bindToRecyclerView(recyclerView);

            myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (adapter.getItemViewType(position) == 0) {
                        if (adapter.getItem(position) instanceof DepartmentEntity) {
                            DepartmentEntity item = (DepartmentEntity) adapter.getItem(position);
                            if (showDept(item)) {
                                curDept = item;
                            } else {
                                Toast.makeText(AddressBookActivity.this, "没有子部门或者人员", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

        }
    }

    private DepartmentEntity findTopDept() {
        final List<DepartmentEntity> depts = MainActivity.depts;
        for (DepartmentEntity dept : depts) {
            if (dept.getSupDept() == null) {
                //顶级部门
                return dept;
            }
        }
        return null;
    }

    public void onBackClick(View view) {
        if (curDept == null) {
            return;
        }
        if (curDept.getSupDept() == null) {
            //已经到最上层
            Toast.makeText(this, "已经到最上层了", Toast.LENGTH_SHORT).show();
            return;
        }
        //上一个部门
        DepartmentEntity sup = curDept.getSupDept();
        if (showDept(sup)) {
            //显示成功了
            curDept = sup;
        }
    }

    private boolean showDept(DepartmentEntity dept) {
        if (dept != null) {
            if (dept.getDeptList() != null || dept.getContactList() != null) {
                dataList.clear();
                if (dept.getDeptList() != null) {
                    dataList.addAll(dept.getDeptList());
                }
                if (dept.getContactList() != null) {
                    dataList.addAll(dept.getContactList());
                }
                myAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }


    private static class MyAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

        public MyAdapter(@Nullable List<Object> data) {
            super(data);
            setMultiTypeDelegate(new MultiTypeDelegate<Object>() {
                @Override
                protected int getItemType(Object o) {
                    if (o instanceof ContactEntity) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            getMultiTypeDelegate().registerItemTypeAutoIncrease(R.layout.item_dept, R.layout.item_contact);
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            final int itemViewType = helper.getItemViewType();
            if (itemViewType == 0) {
                DepartmentEntity dept = (DepartmentEntity) item;
                helper.setText(R.id.tv_name, dept.getName());
            } else {
                ContactEntity contact = (ContactEntity) item;
                helper.setText(R.id.tv_name, contact.getName());
            }
        }
    }
}
