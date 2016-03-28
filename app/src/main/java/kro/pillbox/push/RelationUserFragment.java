package kro.pillbox.push;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/26.
 */
public class RelationUserFragment extends ListFragment {

    Button startMessageSend ;
    RelationCustomAdapter relationAdapter ;
    SharedPreferenceControl sp ;
    String[] PhoneNumber , UserName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.relation_user_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = new SharedPreferenceControl(getActivity());
        startMessageSend = (Button)getActivity().findViewById(R.id.startMessageSend);
        startMessageSend.setOnClickListener(onClick);

        buildAdapter();

    }

    private void buildAdapter() {
        PhoneNumber = sp.getPhoneArray("Relate_Array");
        UserName = sp.getNameArray("Relate_Array");
        relationAdapter = new RelationCustomAdapter(
               getActivity() , PhoneNumber , UserName , R.drawable.icon_person
        );
        setListAdapter(relationAdapter);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            catchCheckedUser();
            Intent intent = new Intent(getActivity(), MessageSend.class);
            intent.putExtra("Phone Number" , PhoneNumber);
            intent.putExtra("User Name", UserName);
            getActivity().startActivity(intent);
        }
    };

    private void catchCheckedUser(){
        String[] tempArray = new String[PhoneNumber.length];
        String[] tempArray2 = new String[PhoneNumber.length];
        int j = 0;
        for(int i = 0 ; i < PhoneNumber.length ; i++) {
            HashMap<String, Object> hashMap = sp.getListInformation(PhoneNumber[i]);
            if((boolean)hashMap.get("Check")){
                tempArray[j] = PhoneNumber[i];
                tempArray2[j] = UserName[i];
                j++ ;
            }
        }
        Log.d("test" , "tempArray" +tempArray.length + "");
        String[] outputArray = new String[j];
        String[] outputArray2 = new String[j];
        for(int i = 0 ; i < j ; i++){
            outputArray[i] = tempArray[i];
            outputArray2[i] = tempArray2[i];
        }
        PhoneNumber = outputArray;
        UserName = outputArray2;
        Log.d("test" , PhoneNumber.length + "");
    }


}
