package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

import java.io.File;
import java.util.Collections;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;

class messageManager
{
    /*Private Variables*/

    private boolean is_popup_open = false;
    private CFAlertDialog.Builder popup_instance;
    private String data;
    private boolean isDialogDismissed = true;

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;
    private CFAlertDialog dialog_main = null;

    /*Initializations*/

    messageManager(eventObserver.eventListener event)
    {
        this.event = event;
        initialize();
    }

    private void initialize()
    {
        popup_instance = new CFAlertDialog.Builder(app_context);
    }

    private void onDismissListener()
    {
        popup_instance.onDismissListener(dialogInterface -> is_popup_open = false);
    }

    /*Helper Methods*/
    private void welcomeMessage()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(strings.WELCOME_MESSAGE_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha_v1))
                .setTextColor(app_context.getResources().getColor(R.color.blue_dark))
                .setMessage(strings.WELCOME_MESSAGE_DESC)
                .onDismissListener(dialog -> is_popup_open = false)
                .addButton(strings.WELCOME_MESSAGE_BT_1, -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (DialogInterface tempDialog, int which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.BLACK_MARKET_URL), enums.etype.welcome);
                })
                .addButton(strings.WELCOME_MESSAGE_BT_2, -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.LEAKED_DOCUMENT_URL), enums.etype.welcome);
                })
                .addButton(strings.WELCOME_MESSAGE_BT_3, -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.NEWS_URL), enums.etype.welcome);
                })
                .addButton(strings.WELCOME_MESSAGE_BT_4, -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.SOFTWARE_URL), enums.etype.welcome);
                })
                .addButton(strings.WELCOME_MESSAGE_BT_5, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.cancel_welcome);
                    tempDialog.dismiss();
                });
    }


    private void abiError()
    {
        is_popup_open = false;
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.ABI_ERROR_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> abiError())
                .setMessage(strings.ABI_ERROR_DESC)
                .addButton(strings.ABI_ERROR_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.GENESIS_UPDATE_URL + status.current_ABI));
                    app_context.startActivity(browserIntent);
                })
                .addButton(strings.ABI_ERROR_BT_2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.PLAYSTORE_URL));
                    app_context.startActivity(browserIntent);
                });
    }

    private void ratedSuccessfully()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.RATE_SUCCESS_TITLE)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.RATE_SUCCESS_DESC)
                .addButton(strings.RATE_SUCCESS_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                        tempDialog.dismiss());
    }

    private void reportedSuccessfully()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.REPORT_SUCCESS_TITLE)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.REPORT_SUCCESS_DESC)
                .addButton(strings.REPORT_SUCCESS_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                        tempDialog.dismiss());
    }

    @SuppressLint("ResourceType")
    private void bookmark()
    {
        final EditText input = new EditText(app_context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("");
        input.setBackground(ContextCompat.getDrawable(app_context, R.xml.search_back_default_popup));
        input.setPadding(40, 15, 40, 15);
        input.setHeight(80);
        input.setTextSize(17);
        input.setHint("Title...");

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setHeaderView(input)
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage("Bookmark URL | " + data + "\n")
                .addButton(strings.BOOKMARK_URL_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.replace("genesis.onion","boogle.store")+"split"+input.getText().toString()), enums.etype.bookmark);

                    tempDialog.dismiss();
                });
    }

    private void clearHistory()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.CLEAR_HISTORY_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.CLEAR_HISTORY_DESC)
                .addButton(strings.CLEAR_HISTORY_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.clear_tab);
                    tempDialog.dismiss();
                });
    }

    private void clearTabs()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.CLEAR_TAB_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.CLEAR_TAB_DESC)
                .addButton(strings.CLEAR_TAB_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.clear_tab);
                    tempDialog.dismiss();
                });
    }

    private void clearBookmark()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.clear_bookmark_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.clear_bookmark_desc)
                .addButton(strings.clear_bookmark_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.clear_bookmark);
                    tempDialog.dismiss();
                });
    }

    private void reportURL()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.REPORT_URL_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.REPORT_URL_DESC)
                .addButton(strings.REPORT_URL_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    tempDialog.dismiss();

                    final Handler handler = new Handler();
                    Runnable runnable = () ->
                    {
                        createMessage(app_context,strings.EMPTY_STR, enums.etype.reported_success);
                    };
                    handler.postDelayed(runnable, 1000);

                });

    }

    private void rateApp()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(strings.RATE_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.RATE_MESSAGE)
                .addButton(strings.RATE_POSITIVE, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.app_rated);
                    app_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine")));
                    tempDialog.dismiss();
                })
                .addButton(strings.RATE_NEGATIVE, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.app_rated);
                    tempDialog.dismiss();
                    final Handler handler = new Handler();
                    handler.postDelayed(() ->
                            createMessage(app_context,strings.EMPTY_STR, enums.etype.rate_success), 1000);
                });
    }

    private void downloadFile()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.DOWNLOAD_TITLE)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.DOWNLOAD_MESSAGE + data)
                .addButton(strings.DOWNLOAD_POSITIVE, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.download_file);
                    dialog.dismiss();
                });
    }

    @SuppressLint("ResourceAsColor")
    private void downloadFileLongPress()
    {
        File f = new File(data);

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.DOWNLOAD_LONG_PRESS_MESSAGE + f.getName())
                .setTextGravity(Gravity.LEFT)
                .addButton(strings.LONG_URL_OPTION_4, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.download_file_manual);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_OPTION_1, -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.open_link_new_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_OPTION_2, -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.open_link_current_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_OPTION_3, -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.copy_link);
                    dialog.dismiss();
                });
    }

    private void openURLLongPress()
    {
        File f = new File(data);

        int size = f.getAbsolutePath().length();
        if(size>200){
            size = 200;
        }

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.LONG_URL_MESSAGE + " | " + data.substring(0,size))
                .addButton(strings.LONG_URL_OPTION_1, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.open_link_new_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_OPTION_2, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.open_link_current_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_OPTION_3, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data), enums.etype.copy_link);
                    dialog.dismiss();
                });
    }

    private void popupDownloadFull(){

        String url = data.split("_")[0];
        String file = data.split("_")[1];
        String data = strings.LONG_URL_MESSAGE;

        File f = new File(file);
        int size = f.getAbsolutePath().length();
        if(size>200){
            size = 200;
        }

        if(!url.equals("")){
            data = strings.LONG_URL_MESSAGE + " | " + url;
        }
        else if(!file.equals("")){
            data = strings.LONG_URL_MESSAGE + " | " + file;
        }

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)

                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(data)

                .addButton(strings.LONG_URL_FULL_OPTION_1, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(url), enums.etype.open_link_new_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_FULL_OPTION_2, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(url), enums.etype.open_link_current_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_FULL_OPTION_3, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(url), enums.etype.copy_link);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_FULL_OPTION_7, -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.download_file_manual);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_FULL_OPTION_4, -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.open_link_new_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_FULL_OPTION_5, -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.open_link_current_tab);
                    dialog.dismiss();
                })
                .addButton(strings.LONG_URL_FULL_OPTION_6, -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.copy_link);
                    dialog.dismiss();
                });
    }

    private void startingOrbotInfo()
    {
            is_popup_open = true;
            popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle(strings.ORBOT_INIT_TITLE)
                    .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                    .setTextColor(app_context.getResources().getColor(R.color.black))
                    .setMessage(strings.ORBOT_INIT_DESC)
                    .onDismissListener(dialog -> is_popup_open = false)
                    .addButton(strings.ORBOT_INIT_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                            tempDialog.dismiss()).addButton(strings.ORBOT_INIT_BT_2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
            {
                dialog.dismiss();

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                        event.invokeObserver(Collections.singletonList(data), enums.etype.reload), 500);

            });
    }

    private void versionWarning()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setTitle(strings.VERSION_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.VERSION_DESC)
                .addButton(strings.VERSION_BT_1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.GENESIS_UPDATE_URL + data));
                    app_context.startActivity(browserIntent);
                });
    }

    private void torBanned()
    {
        isDialogDismissed = true;

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.BANNED_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.BANNED_DESC)
                .onDismissListener(dialog -> is_popup_open = false)
                .onDismissListener(dialog -> startHome());

                String btn_text;

                if(status.sGateway){
                    btn_text = "Disable Tor Gateway";
                }
                else {
                    btn_text = "Enable Tor Gateway";
                }

                popup_instance.addButton(btn_text, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    isDialogDismissed = false;
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(!status.sGateway), enums.etype.connect_vpn);
                });
    }

    private void startHome(){
        if(!isDialogDismissed && data==null){
            event.invokeObserver(null, enums.etype.start_home);
        }
        is_popup_open = false;
    }

    void onReset(){
        if(dialog_main!=null){
            dialog_main.dismiss();
            dialog_main = null;
        }
    }

    /*External Helper Methods*/

    void createMessage(AppCompatActivity app_context,String data, enums.etype type)
    {
        this.app_context = app_context;
        this.data = data;
        if (!is_popup_open && !app_context.isDestroyed())
        {
            is_popup_open = true;
            popup_instance = new CFAlertDialog.Builder(app_context);

            onDismissListener();
            switch (type)
            {
                case welcome:
                    welcomeMessage();
                    break;

                case abi_error:
                    abiError();
                    break;

                case rate_success:
                    ratedSuccessfully();
                    break;

                case reported_success:
                    reportedSuccessfully();
                    break;

                case bookmark:
                    bookmark();
                    break;

                case clear_history:
                    clearHistory();
                    break;

                case clear_tab:
                    clearTabs();
                    break;

                case clear_bookmark:
                    clearBookmark();
                    break;

                case report_url:
                    reportURL();
                    break;

                case rate_app:
                    rateApp();
                    break;

                case download_file:
                    downloadFile();
                    break;

                case start_orbot:
                    startingOrbotInfo();
                    break;

                case version_warning:
                    versionWarning();
                    break;

                case tor_banned:
                    torBanned();
                    break;

                case download_file_long_press:
                    downloadFileLongPress();
                    break;

                case on_long_press_url:
                    openURLLongPress();
                    break;

                case on_long_press_with_link:
                    popupDownloadFull();
                    break;
            }

            dialog_main = popup_instance.show();
        }
    }
}
