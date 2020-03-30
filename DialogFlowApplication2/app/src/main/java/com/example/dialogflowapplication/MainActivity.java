package com.example.dialogflowapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.example.dialogflowapplication.databinding.ActivityMainBinding;
import com.getstream.sdk.chat.StreamChat;
import com.getstream.sdk.chat.enums.FilterObject;
import com.getstream.sdk.chat.rest.User;
import com.getstream.sdk.chat.rest.core.Client;
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel;
import java.util.HashMap;

import static com.getstream.sdk.chat.enums.Filters.eq;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CHANNEL_TYPE = "com.example.dialogflowapplication.CHANNEL_TYPE";
    public static final String EXTRA_CHANNEL_ID = "com.example.dialogflowapplication.CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup the client using the example API key
        // normal you would call init in your Application class and not the activity
        StreamChat.init("myzp4by98ukn", this.getApplicationContext());
        Client client = StreamChat.getInstance(this.getApplication());
        HashMap<String, Object> extraData = new HashMap<>();
        extraData.put("name", "Paranoid Android");
        extraData.put("image", "https://bit.ly/2TIt8NR");
        User currentUser = new User("5e7bd5e8baa37b654f21739e", extraData);
        // User token is typically provided by your server when the user authenticates
        client.setUser(currentUser,
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWU3YmQ1ZThiYWEzN2I2NTRmMjE3MzllIn0.WbcLqSx9Pp17GnAvoOChc_gfrb-gtkO9RymKukYfEb8");

        // we're using data binding in this example
        ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // most the business logic for chat is handled in the ChannelListViewModel view model
        ChannelListViewModel viewModel = ViewModelProviders.of(this).get(ChannelListViewModel.class);
        binding.setViewModel(viewModel);
        binding.channelList.setViewModel(viewModel, this);

        // query all channels of type messaging
        FilterObject filter = eq("type", "messaging");
        viewModel.setChannelFilter(filter);

        // click handlers for clicking a user avatar or channel
        binding.channelList.setOnChannelClickListener(channel -> {
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.getType());
            intent.putExtra(EXTRA_CHANNEL_ID, channel.getId());
            startActivity(intent);
        });
        binding.channelList.setOnUserClickListener(user -> {
            // open your user profile
        });

    }
}