package core.tomiko.publico;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * By: El Bryant
 */

public class PrefUtil {
    private static final String NAME_REFERENCE = "preftomiko";
    private Context context;
    public static final String LOGIN_STATUS = "login_status";


    public PrefUtil(Context context) {
        this.context = context;
    }

    public void saveGenericValue(String campo, String valor) {
        SharedPreferences prefs = context.getSharedPreferences(NAME_REFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(campo, valor);
        editor.apply();
    }

    public void clearAll() {
        SharedPreferences prefs = context.getSharedPreferences(NAME_REFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

    public String getStringValue(String campo) {
        SharedPreferences prefs = context.getSharedPreferences(NAME_REFERENCE, Context.MODE_PRIVATE);
        return prefs.getString(campo, "");
    }
}