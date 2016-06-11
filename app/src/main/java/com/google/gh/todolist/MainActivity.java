package com.google.gh.todolist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class  MainActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private CustExpandableListAdapter adapter;
    Context mContext = null;

    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, Phone.CONTACT_ID };

    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;


    /**联系人名称**/
    private ArrayList<String> mContactsName = new ArrayList<String>();

    /**联系人号码**/
    private ArrayList<String> mContactsNumber = new ArrayList<String>();

    /**联系人头像**/
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

    /**联系人短信**/
    public static ArrayList<String> mContactSms = new ArrayList<String>();

    private String number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        try{
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");

// 获取 gDefault 这个字段, 想办法替换它
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

// 4.x以上的gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的字段
            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

// ActivityManagerNative 的gDefault对象里面原始的 IActivityManager对象
            Object rawIActivityManager = mInstanceField.get(gDefault);
            Toast.makeText(this,gDefaultField.toString()+"\n"+gDefault.toString()+"\n"+rawIActivityManager.toString(),Toast.LENGTH_LONG).show();
        }catch (Exception e){

        }

        listView= (ExpandableListView) findViewById(R.id.expand_list);
        /**得到手机通讯录联系人信息**/
        getPhoneContacts();
        adapter  = new CustExpandableListAdapter();
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,getString(R.string.app_name));
                shortcutIntent.putExtra("duplicate", false);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClass(getApplicationContext(), MainActivity.class);

                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(MainActivity.this,
                                R.mipmap.ic_launcher));
                sendBroadcast(shortcutIntent);
                Toast.makeText(MainActivity.this,"桌面图标已创建",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, Phone.NUMBER + " asc");


        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if(photoid > 0 ) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsPhonto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    /**得到手机SIM卡联系人人信息**/
    private void getSIMContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);

                //Sim卡中没有联系人头像

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }

            phoneCursor.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CustExpandableListAdapter implements ExpandableListAdapter {

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return mContactsName.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.group_item,null);
                viewHolder = new ViewHolder();
                viewHolder.contact_icon = (ImageView) convertView.findViewById(R.id.contact_icon);
                viewHolder.contact_name = (TextView) convertView.findViewById(R.id.contact_name);
                viewHolder.contact_number = (TextView) convertView.findViewById(R.id.contact_number);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.contact_icon.setImageBitmap(mContactsPhonto.get(groupPosition));
            viewHolder.contact_name.setText(mContactsName.get(groupPosition));
            viewHolder.contact_number.setText(mContactsNumber.get(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.child_item,null);
            }

            convertView.findViewById(R.id.text_call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+mContactsNumber.get(groupPosition)));
                    startActivity(intent);
                }
            });

            convertView.findViewById(R.id.text_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!number.equals(mContactsNumber.get(groupPosition))){
                        mContactSms.clear();
                        getSmsFromPhone(mContactsNumber.get(groupPosition));
                    }
                    number = mContactsNumber.get(groupPosition);
                    startActivity(new Intent(MainActivity.this,SmsActivity.class));
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }

        class ViewHolder {
            ImageView contact_icon;
            TextView contact_number;
            TextView contact_name;
        }
    }

    private Uri SMS_INBOX = Uri.parse("content://sms/");

    public void getSmsFromPhone(String num) {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[] { "body" };//"_id", "address", "person",, "date", "type"
        String where = " address = "+ num;
      //          "AND date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = null;
        try{
            cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
            if (null == cur)
                return;
            while (cur.moveToNext()) {
//            String number = cur.getString(cur.getColumnIndex("address"));//手机号
//            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
                String body = cur.getString(cur.getColumnIndex("body"));
                mContactSms.add(body);
                //这里我是要获取自己短信服务号码中的验证码~~
//            Pattern pattern = Pattern.compile(" [a-zA-Z0-9]{10}");
//            Matcher matcher = pattern.matcher(body);
//            if (matcher.find()) {
//                String res = matcher.group().substring(1, 11);
//            }
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
        }finally {
            if (null!=cur){
                cur.close();
            }
        }
    }

}
