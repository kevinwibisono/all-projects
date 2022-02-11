package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ConversationsAdapter;
import my.istts.finalproject.databinding.ActivityConversationsBinding;
import my.istts.finalproject.viewmodels.ConversationViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ConversationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConversationViewModel viewmodel = new ConversationViewModel(getApplication());
        ActivityConversationsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_conversations);
        binding.setViewmodel(viewmodel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbConv = findViewById(R.id.tbConv);
        tbConv.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ConversationsAdapter adapter = new ConversationsAdapter(new ConversationsAdapter.openConversation() {
            @Override            public void onOpen(String id) {
                Intent chatIntent = new Intent(ConversationsActivity.this, ChatActivity.class);
                chatIntent.putExtra("id_room", id);
                startActivity(chatIntent);
            }
        });

        RecyclerView rvConvs = findViewById(R.id.rvConvs);
        rvConvs.setAdapter(adapter);

        viewmodel.getConvsVM().observe(this, vms -> {
            adapter.setConvVM(vms);
            adapter.notifyDataSetChanged();
        });

        viewmodel.getConversations();
    }
}