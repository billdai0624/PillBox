package kro.pillbox.push;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by Jacky on 2015/11/27.
 */
public class GetContacts {

    Context context ;
    String[] phoneArray = new String[1000] , nameArray = new String[1000];
    int k = 0 ;
    SynchronizeContacts synchronizeContacts ;

    GetContacts(Context context){
        this.context = context ;
    }

    public void getContactsInformation() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);

                    String tempArray[] = new String[pCur.getCount()] ;
                    int i = 0 ;

                    while (pCur.moveToNext()) {
                        tempArray[i]  = phoneTrim(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))) ;
                        i++;
                    }
                    pCur.close();
                    for(i = 0 ; i < tempArray.length ; i++){
                        for(int j = i + 1  ; j < tempArray.length ; j++){
                            if( tempArray[i].equals(tempArray[j]) && !tempArray[i].equals("")  ){
                                tempArray[j] = "";
                            }
                        }

                        if(!tempArray[i].equals("")) {
                            phoneArray[k] = tempArray[i];
                            nameArray[k] = name;
                            k++;
                            tempArray[i] = "";
                        }
                    }
                }
            }
        }
        cur.close();
        synchronizeContacts = new SynchronizeContacts(context , arrayExtract(nameArray) , arrayExtract(phoneArray) );
        synchronizeContacts.combineArray(arrayExtract(phoneArray));
        synchronizeContacts.sendToServlet();
    }

    private String phoneTrim(String phoneNum){
        String temp = "";
        phoneNum = phoneNum.trim();
        if(phoneNum.contains("+")){
            int a = phoneNum.indexOf("6");
            temp = "0" + phoneNum.substring(a + 1 );
        }else{
            phoneNum = phoneNum.replace("-" , " ");
            String[] splitArray = phoneNum.split(" ");
            for(String d : splitArray){
                temp = temp.concat(d);
            }
        }
        return temp ;
    }

    private String[] arrayExtract(String[] targetArray){
        String[] outputArray;
        int point = 0 ;
        for(int i = 0 ; i < targetArray.length ; i++){
            if(targetArray[i] == null || targetArray[i].isEmpty()){
                point = i ;
                break;
            }
        }
        outputArray = new String[point+1];
        System.arraycopy(targetArray , 0 , outputArray , 0 ,outputArray.length);
        return outputArray;
    }
}
