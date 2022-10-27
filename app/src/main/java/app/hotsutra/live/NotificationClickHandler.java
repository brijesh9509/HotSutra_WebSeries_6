package app.hotsutra.live;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationClickHandler implements OneSignal.OSNotificationOpenedHandler {
    Context context2;

    public NotificationClickHandler(Context context) {
        context2 = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult result) {
        OSNotificationAction.ActionType actionType = result.getAction().getType();

        JSONObject data = result.getNotification().getAdditionalData();
        String customKey;
        String id = null;
        String type = null;
        String openType = null;
        String webUrl = null;

        try {
            id= data.getString("id");
            type = data.getString("vtype");
            openType = data.getString("open");
            webUrl = data.getString("url");
            //Toast.makeText(context2, "id "+ openType, Toast.LENGTH_SHORT).show();
            Log.d("noti:", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent;
        if (openType.equalsIgnoreCase("web")) {

            intent = new Intent(context2, TermsActivity.class);
            intent.putExtra("url", webUrl);
            intent.putExtra("title", result.getNotification().getTitle());

        } else {
            intent = new Intent(context2, DetailsActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("vType", type);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context2.startActivity(intent);

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.getAction().getActionId());
        }
    }
}
