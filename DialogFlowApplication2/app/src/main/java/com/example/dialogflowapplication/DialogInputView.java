package com.example.dialogflowapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import com.getstream.sdk.chat.interfaces.MessageSendListener;
import com.getstream.sdk.chat.rest.Message;
import com.getstream.sdk.chat.view.MessageInputView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogInputView extends MessageInputView implements MessageSendListener {

    private final static String TAG = DialogInputView.class.getSimpleName();

    public DialogInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMessageSendListener(this);
    }

    @Override
    public void onSendMessageSuccess(Message message) {
        Log.d(TAG, "Sent message! :" + message.getText());

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    String json = String.format("{\"text\" : \"%s\"}", message.getText());
                    Log.d(TAG, String.format("Sending JSON to server... %s", json));
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
                        Request request = new Request.Builder()
                                .url("https://4c6ba554.ngrok.io/dialogflow")
                                .post(body)
                                .build();
                        try (Response response = client.newCall(request).execute()) {
                            String res = "" + response.body().string();
                            Log.d("OOPS", res);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "" + e.getMessage());
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        onSendMessageError("" + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "" + e.getMessage());
                }
            }
        });
        thread.start();
    }

    @Override
    public void onSendMessageError(String errMsg) {
        Log.d(TAG, "Failed send message! :" + errMsg);
    }
}
