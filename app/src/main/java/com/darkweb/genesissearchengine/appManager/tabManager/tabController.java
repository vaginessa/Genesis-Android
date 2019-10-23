package com.darkweb.genesissearchengine.appManager.tabManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.List;
import java.util.Objects;

public class tabController extends AppCompatActivity
{
    /*Private Variables*/

    private tabModel mListModel;
    private homeController mHomeController;
    private activityContextManager mContextManager;

    private ImageView mEmptyListNotifier;
    private EditText mSearchBar;
    private RecyclerView mListView;
    private Button mClearButton;

    private tabViewController mtabViewController;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);
        initializeListModel();
        initializeViews();
        initializeList();
        onEditorInvoked();
    }

    public void initializeListModel(){
        mListModel = new tabModel();
        mListModel.setList(dataController.getInstance().getTab());
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setTabController(this);
        pluginController.getInstance().logEvent(strings.BOOKMARK_OPENED);
    }
    public void initializeViews(){
        mEmptyListNotifier = findViewById(R.id.empty_list);
        mSearchBar = findViewById(R.id.search);
        mListView = findViewById(R.id.listview);
        mClearButton = findViewById(R.id.clearButton);
        mtabViewController = new tabViewController(mEmptyListNotifier, mSearchBar, mListView, mClearButton,this);
        mClearButton.setText("CLEAR TABS");
    }
    public void initializeList(){
        tabAdapter adapter = new tabAdapter(mListModel.getList(),new adapterCallback());
        adapter.invokeFilter(false);
        mListView.setAdapter(adapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mtabViewController.updateIfListEmpty(mListModel.getList().size(),0);
    }

    /*View Handlers*/

    public void onEditorInvoked(){

        mSearchBar.setOnEditorActionListener((v, actionId, event) ->{
            if (actionId == EditorInfo.IME_ACTION_NEXT)
            {
                helperMethod.hideKeyboard(this);
                return true;
            }
            return false;
        });

        mSearchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                ((tabAdapter) Objects.requireNonNull(mListView.getAdapter())).setFilter(mSearchBar.getText().toString());
                ((tabAdapter) mListView.getAdapter()).invokeFilter(true);
            }
        });
    }

    public void onBackPressed(View view){
        this.finish();
    }

    public void onclearDataTrigger(View view){
        mListModel.clearList();
        ((tabAdapter) mListView.getAdapter()).invokeFilter(true );
        mtabViewController.clearList();
        dataController.getInstance().clearTabs();
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().setBool(keys.LOW_MEMORY,true);
            finish();
        }
    }

    @Override
    public void onResume()
    {
        status.sIsAppPaused = false;
        super.onResume();
    }

    @Override
    public void onPause()
    {
        status.sIsAppPaused = true;
        super.onPause();
    }

    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type.equals(enums.etype.url_triggered)){
                tabRowModel model = (tabRowModel)data.get(0);
                pluginController.getInstance().logEvent(strings.TAB_TRIGGERED);
                mHomeController.onLoadTab(model.getmSession(),model.getmHeader(),model.getmDescription(),true,model.getProgress());
                finish();
            }
            else if(e_type.equals(enums.etype.url_clear)){
                mListModel.onManualClear((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.is_empty)){
                mtabViewController.removeFromList((int)data.get(0));
                mtabViewController.updateIfListEmpty(mListModel.getList().size(),300);
            }
        }

    }

}
