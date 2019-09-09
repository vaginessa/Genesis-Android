package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;

public class messageManager
{
    /*Private Variables*/

    private boolean is_popup_open = false;
    private CFAlertDialog.Builder popup_instance;
    private String data;

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

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
                .setTitle(strings.welcome_message_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.blue_dark))
                .setMessage(strings.welcome_message_desc)
                .onDismissListener(dialog -> is_popup_open = false)
                .addButton(strings.welcome_message_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    dialog.dismiss();
                    event.invokeObserver(constants.blackMarket, enums.eventType.welcome);
                })
                .addButton(strings.welcome_message_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    dialog.dismiss();
                    event.invokeObserver(constants.leakedDocument, enums.eventType.welcome);
                })
                .addButton(strings.welcome_message_bt3, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    dialog.dismiss();
                    event.invokeObserver(constants.news, enums.eventType.welcome);
                })
                .addButton(strings.welcome_message_bt4, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    dialog.dismiss();
                    event.invokeObserver(constants.softwares, enums.eventType.welcome);
                })
                .addButton(strings.welcome_message_bt5, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.eventType.cancel_welcome);
                    dialog.dismiss();
                });

    }


    private void abiError()
    {
        is_popup_open = false;
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.abi_error_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> abiError())
                .setMessage(strings.abi_error_desc)
                .addButton(strings.abi_error_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.updateUrl + status.current_ABI));
                    app_context.startActivity(browserIntent);
                })
                .addButton(strings.abi_error_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.playstoreUrl));
                    app_context.startActivity(browserIntent);
                });
    }

    private void ratedSuccessfully()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.rate_success_title)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.rate_success_desc)
                .addButton(strings.rate_success_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                        dialog.dismiss());

    }

    private void reportedSuccessfully()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.report_success_title)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.report_success_desc)
                .addButton(strings.report_success_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                        dialog.dismiss());

    }

    @SuppressLint("ResourceType")
    private void bookmark()
    {

        final EditText input = new EditText(app_context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("");
        input.setBackground(ContextCompat.getDrawable(app_context, R.xml.search_back_default));
        input.setPadding(40, 15, 40, 15);
        input.setHeight(80);
        input.setTextSize(17);
        input.setHint("Enter Bookmark Title");

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setHeaderView(input)
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage("Bookmark URL | " + data + "\n")
                .addButton(strings.bookmark_url_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(data.replace("genesis.onion","boogle.store")+"split"+input.getText().toString(),enums.eventType.bookmark);
                    dialog.dismiss();
                })
                .addButton(strings.bookmark_url_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                        dialog.dismiss());


    }

    private void clearHistory()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.clear_history_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.clear_history_desc)
                .addButton(strings.clear_history_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.eventType.clear_history);
                    dialog.dismiss();
                })
                .addButton(strings.clear_history_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> dialog.dismiss());
    }

    private void clearBookmark()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.clear_bookmark_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.clear_bookmark_desc)
                .addButton(strings.clear_bookmark_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.eventType.clear_bookmark);
                    dialog.dismiss();
                })
                .addButton(strings.clear_bookmark_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> dialog.dismiss());
    }

    private void reportURL()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.report_url_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.report_url_desc)
                .addButton(strings.report_url_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    dialog.dismiss();
                    createMessage(app_context,strings.emptyStr, enums.popup_type.reported_success);
                })
                .addButton(strings.report_url_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                        dialog.dismiss());

    }

    private void rateApp()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(strings.rate_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(strings.rate_message)
                .addButton(strings.rate_positive, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.eventType.app_rated);
                    app_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine")));
                    dialog.dismiss();
                })
                .addButton(strings.rate_negative, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.eventType.app_rated);
                    dialog.dismiss();
                    createMessage(app_context,strings.emptyStr, enums.popup_type.rate_success);
                });
    }

    private void downloadFile()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.download_title)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.download_message + data)
                .addButton(strings.download_positive, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.eventType.download_file);
                    dialog.dismiss();
                })
                .addButton(strings.download_negative, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                        dialog.dismiss());

    }

    private void startingOrbotInfo()
    {
        //if (!is_popup_open)
        //{
            is_popup_open = true;
            popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle(strings.orbot_init_title)
                    .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                    .setTextColor(app_context.getResources().getColor(R.color.black))
                    .setMessage(strings.orbot_init_desc)
                    .onDismissListener(dialog -> is_popup_open = false)
                    .addButton(strings.orbot_init_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                            dialog.dismiss()).addButton(strings.orbot_init_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
            {
                dialog.dismiss();

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    //if (!data.equals(strings.emptyStr))
                    //{
                    //    event.invokeObserver(data, enums.eventType.welcome);
                    //} else
                    //{
                        event.invokeObserver(data, enums.eventType.reload);
                    //}
                }, 500);

            });

        //}
    }

    private void versionWarning()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setTitle(strings.version_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.version_desc)
                .addButton(strings.version_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.updateUrl + data));
                    app_context.startActivity(browserIntent);
                });

    }

    boolean isDialogDismissed = true;
    private void torBanned()
    {
        isDialogDismissed = true;
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.banned_title)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(strings.banned_desc)
                .onDismissListener(dialog -> startHome())
                .addButton(strings.banned_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    isDialogDismissed = false;
                    dialog.dismiss();
                    event.invokeObserver(true, enums.eventType.connect_vpn);
                })
                .addButton(strings.banned_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    isDialogDismissed = false;
                    dialog.dismiss();
                    event.invokeObserver(false, enums.eventType.connect_vpn);
                });
    }

    private void startHome(){
        if(!isDialogDismissed && data==null){
            event.invokeObserver(null, enums.eventType.start_home);
        }
        is_popup_open = false;
    }

    /*External Helper Methods*/

    void createMessage(AppCompatActivity app_context,String data, enums.popup_type type)
    {
        this.app_context = app_context;
        this.data = data;
        if (!is_popup_open)
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
            }

            popup_instance.show();
        }
    }
}
