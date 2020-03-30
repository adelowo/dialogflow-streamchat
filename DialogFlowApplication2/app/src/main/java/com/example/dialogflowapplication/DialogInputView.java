package com.example.dialogflowapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.getstream.sdk.chat.rest.Message;
import com.getstream.sdk.chat.view.MessageInputView;
import com.getstream.sdk.chat.interfaces.MessageSendListener;

public class DialogInputView  extends MessageInputView implements MessageSendListener {

    private final static String TAG = DialogInputView.class.getSimpleName();

    public DialogInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSendMessage(Message message) {
        Log.d("OOps", message.getText());
        super.viewModel.sendMessage(message);
        Log.d(TAG, super.viewModel.client().getUserId());
        Log.d(TAG, "Sent");
    }

    @Override
    public void onSendMessageSuccess(Message message) {
        super.onSendMessage(message);
    }

    @Override
    public void onSendMessageError(String errMsg) {
        Log.d(TAG, "Failed send message! :" + errMsg);
    }
}
