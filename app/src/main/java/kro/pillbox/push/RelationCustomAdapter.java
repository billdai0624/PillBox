package kro.pillbox.push;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/11/25.
 */
public class RelationCustomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    String[] PhoneNumber , UserName;
    SharedPreferenceControl sp ;
    Context context ;
    int imageId ;
    Holder holder;

    RelationCustomAdapter(Context context , String[] PhoneNumber , String[] UserName , int imageId){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.PhoneNumber = PhoneNumber;
        this.UserName = UserName;
        this.imageId = imageId ;
        sp = new SharedPreferenceControl(context);
    }

    @Override
    public int getCount() {
        return PhoneNumber.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.relation_user_list_item, parent, false);
            holder.relationUserName = (TextView) convertView.findViewById(R.id.relationUserName);
            holder.relationUserPhone = (TextView) convertView.findViewById(R.id.relationUserPhone);
            holder.relationUserImg = (ImageView) convertView.findViewById(R.id.relationUserImg);
            holder.noticeOpenSwitch = (Switch) convertView.findViewById(R.id.noticeOpenSwitch);
            holder.userChooseCheckbox = (CheckBox) convertView.findViewById(R.id.userChooseCheckbox);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder)convertView.getTag();
        }


        holder.noticeOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    storeHashMap(PhoneNumber[position], UserName[position], imageId, true, getHashMapBoolean(PhoneNumber[position], "Check"));
                else
                    storeHashMap(PhoneNumber[position], UserName[position], imageId, false, getHashMapBoolean(PhoneNumber[position], "Check"));
            }
        });

        holder.userChooseCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    storeHashMap(PhoneNumber[position], UserName[position], imageId, getHashMapBoolean(PhoneNumber[position], "Switch"), true);
                else
                    storeHashMap(PhoneNumber[position], UserName[position], imageId, getHashMapBoolean(PhoneNumber[position], "Switch"), false);
            }
        });

        holder.relationUserName.setText("Name : " + UserName[position]);
        holder.relationUserPhone.setText("Phone number : " + PhoneNumber[position]);
        holder.relationUserImg.setImageResource(imageId);
        initial(PhoneNumber[position]);
        return convertView;
    }

    private void storeHashMap(String phoneNumber , String userName , int ImageId , boolean Switch , boolean Check){
        HashMap<String , Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Phone Number", phoneNumber);
        hashMap.put("User Name", userName);
        hashMap.put("Image", ImageId);
        hashMap.put("Switch", Switch);
        hashMap.put("Check", Check);
        sp.storeListInformation(hashMap);

    }

    private boolean getHashMapBoolean(String phoneNumber , String key){
        HashMap<String , Object> hashMap = sp.getListInformation(phoneNumber);
        return (boolean)hashMap.get(key);
    }

    private void initial(String phoneNumber){
        HashMap<String , Object> hashMap = sp.getListInformation(phoneNumber);

        if((boolean)hashMap.get("Switch"))
            holder.noticeOpenSwitch.setChecked(true);
        else
            holder.noticeOpenSwitch.setChecked(false);

        if((boolean)hashMap.get("Check"))
            holder.userChooseCheckbox.setChecked(true);
        else
            holder.userChooseCheckbox.setChecked(false);


    }


    class Holder{
        TextView relationUserName , relationUserPhone ;
        ImageView relationUserImg ;
        Switch noticeOpenSwitch ;
        CheckBox userChooseCheckbox ;
    }

}
