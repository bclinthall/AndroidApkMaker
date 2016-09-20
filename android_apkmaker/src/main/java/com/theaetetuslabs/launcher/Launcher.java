package com.theaetetuslabs.launcher;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.theaetetuslabs.android_apkmaker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bclinthall on 8/31/16.
 */
public enum Launcher {
    ACTION(false,
            "Action Launcher",
            "com.actionlauncher.playstore",
            null,
            new Object[][]{{"apply_icon_pack", null}},
            new int[0],
            true,
            false
    ),
    ADW(true,
            "ADW.Launcher",
            "org.adw.launcher",
            "org.adw.launcher.SET_THEME",
            new Object[][]{{"org.adw.launcher.theme.NAME", null}},
            new int[]{Intent.FLAG_ACTIVITY_NEW_TASK}
    ),
    APEX(true,
            "Apex Launcher",
            "com.anddoes.launcher",
            "com.anddoes.launcher.SET_THEME",
            new Object[][]{{"com.anddoes.launcher.THEME_PACKAGE_NAME", null}},
            new int[]{Intent.FLAG_ACTIVITY_NEW_TASK}
    ),
    ATOM(false,
            "Atom Launcher",
            "com.dlto.atom.launcher",
            "com.dlto.atom.launcher.intent.action.ACTION_VIEW_THEME_SETTINGS",
            new Object[][]{{"packageName", null}},
            new int[]{Intent.FLAG_ACTIVITY_NEW_TASK}
    ),
    AVIATE(false,
            "Yahoo Aviate Launcher",
            "com.tul.aviate",
            "com.tul.aviate.SET_THEME",
            new Object[][]{{"THEME_PACKAGE", null}},
            new int[]{Intent.FLAG_ACTIVITY_NEW_TASK}
    ),
    CYANOGENMOD(false,
            "CyanogenMod Themes",
            "org.cyanogenmod.theme.chooser",
            Intent.ACTION_MAIN,
            new Object[][]{{"pkgName", null}},
            new ComponentName("org.cyanogenmod.theme.chooser", "org.cyanogenmod.theme.chooser.ChooserActivity")
    ),
    GO(false,
            "GO Launcher",
            "com.gau.go.launcherex",
            "com.gau.go.launcherex.MyThemes.mythemeaction",
            new Object[][]{
                    {"type", 1},
                    {"pkgname", null}
            },
            new int[0],
            true,
            true

    ),
    INSPIRE(false,
            "Inspire Launcher",
            "com.bam.android.inspirelauncher",
            "com.bam.android.inspirelauncher.action.ACTION_SET_THEME",
            new String[][]{{"icon_pack_name", null}},
            new int[0],
            true,
            true
    ),
    KK(false,
            "KK Launcher",
            "com.kk.launcher",
            "com.kk.launcher.APPLY_ICON_THEME",
            new Object[][]{
                    {"com.kk.launcher.theme.EXTRA_PKG", null},
                    {"com.kk.launcher.theme.EXTRA_NAME", "IconPack - IconEffects"}
            }
    ),
    LUCID(false,
            "Lucid Launcher",
            "com.powerpoint45.launcher",
            "com.powerpoint45.action.APPLY_THEME",
            new Object[][]{{"icontheme", null}}
    ),
    NEXT_TRIAL(false,
            "Next Launcher Lite",
            "com.gtp.nextlauncher.trial",
            "com.gau.go.launcherex.MyThemes.mythemeaction",
            new Object[][]{
                    {"type", 1},
                    {"pkgname", null}
            },
            new int[0],
            true,
            true
    ),
    NEXT(false,
            "Next Launcher",
            "com.gtp.nextlauncher",
            "com.gau.go.launcherex.MyThemes.mythemeaction",
            new Object[][]{
                    {"type", 1},
                    {"pkgname", null}
            },
            new int[0],
            true,
            true
    ),
    NINE_FREE(false,
            "Nine Launcher Free",
            "com.gridappsinc.launcher.free",
            "com.gridappsinc.launcher.action.THEME",
            new Object[][]{
                    {"iconpkg", null},
                    {"launch", true}
            },
            new int[0],
            true,
            true

    ),
    NINE(false,
            "Nine Launcher",
            "com.gridappsinc.launcher",
            "com.gridappsinc.launcher.action.THEME",
            new Object[][]{
                    {"iconpkg", null},
                    {"launch", true}
            },
            new int[0],
            true,
            true

    ),
    NOVA(true,
            "Nova Launcher",
            "com.teslacoilsw.launcher",
            "com.teslacoilsw.launcher.APPLY_ICON_THEME",
            new Object[][]{{"com.teslacoilsw.launcher.extra.ICON_THEME_PACKAGE", null}},
            new int[]{Intent.FLAG_ACTIVITY_NEW_TASK}
    ),
    S(false,
            "S Launcher",
            "com.s.launcher",
            "com.s.launcher.APPLY_ICON_THEME",
            new Object[][]{
                    {"com.s.launcher.theme.EXTRA_PKG", null},
                    {"com.s.launcher.theme.EXTRA_NAME", "IconPack - IconEffects"}
            }
    ),
    SMART(false,
            "Smart Launcher",
            "ginlemon.flowerfree",
            "ginlemon.smartlauncher.setGSLTHEME",
            new Object[][]{{"package", null}}
    ),
    SMART_PRO(false,
            "Smart Launcher Pro",
            "ginlemon.flowerpro",
            "ginlemon.smartlauncher.setGSLTHEME",
            new Object[][]{{"package", null}}
    ),
    SOLO(false,
            "Solo Launcher",
            "home.solo.launcher.free",
            "home.solo.launcher.free.APPLY_THEME",
            new Object[][]{
                    {"EXTRA_PACKAGENAME", null},
                    {"EXTRA_THEMENAME", "IconPack - IconEffects"}
            },
            new int[0],
            true,
            true
    );
    boolean recommend;
    String name;
    String packageName;
    String action;
    Object[][] extras;  //String, String|Boolean|Integer
    int[] flags;
    boolean getLaunchIntent;
    boolean sendBroadcast;
    ComponentName cn;
    Launcher(boolean recommend, String name, String packageName){
        this(recommend, name, packageName, null);
    }
    Launcher(boolean recommend, String name, String packageName, String action){
        this(recommend, name, packageName, action, new String[0][0]);
    }
    Launcher(boolean recommend, String name,
             String packageName,
             String action,
             Object[][] extras){
        this(recommend, name, packageName, action, extras, new int[0]);
    }
    Launcher(boolean recommend, String name,
             String packageName,
             String action,
             Object[][] extras,
             ComponentName cn){
        this(recommend, name, packageName, action, extras);
        this.cn = cn;
    }
    Launcher(boolean recommend, String name,
             String packageName,
             String action,
             Object[][] extras,
             int[] flags){
        this(recommend, name, packageName, action, extras, flags, false, false);
    }
    Launcher(boolean recommend, String name,
             String packageName,
             String action,
             Object[][] extras,
             int[] flags,
             boolean getLaunchIntent,
             boolean sendBroadcast){
        this.recommend = recommend;
        this.name = name;
        this.packageName = packageName;
        this.action = action;
        this.extras = extras==null ? new String[0][0] : extras;
        this.flags = flags==null ? new int[0] : flags;
        this.getLaunchIntent = getLaunchIntent;
        this.sendBroadcast = sendBroadcast;
    }
    private static List<ResolveInfo> getInstalledLaunchers(PackageManager pm){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        return pm.queryIntentActivities(i, 0);
    }
    private static List<Launcher> getRecommendedLaunchers(){
        List<Launcher> recommendedLaunchers = new ArrayList<Launcher>();
        Launcher[] launchers = Launcher.values();
        for(Launcher launcher : launchers){
            if(launcher.recommend){
                recommendedLaunchers.add(launcher);
            }
        }
        return recommendedLaunchers;
    }
    public interface AfterApply{
        void afterApply();
    }
    private static boolean autoApply(final Context context, List<ResolveInfo> installedLaunchers, final AfterApply afterApply){
        final List<Launcher> autoApplyLaunchers = new ArrayList<Launcher>();
        Launcher[] launchers = Launcher.values();
        for(Launcher launcher : launchers){
            for(ResolveInfo installedLauncher : installedLaunchers){
                if(launcher.packageName.equals(installedLauncher.activityInfo.packageName)){
                    autoApplyLaunchers.add(launcher);
                }
            }
        }
        if(CYANOGENMOD.isInstalled(context.getPackageManager())){
            autoApplyLaunchers.add(CYANOGENMOD);
        }
        if(autoApplyLaunchers.size()==0){
            return false;
        }else{
            String headerTxt = context.getString(R.string.auto_apply_msg);
            headerTxt = headerTxt.replace("{{number}}", ""+(installedLaunchers.size()-1));
            getListDialog(context, autoApplyLaunchers, headerTxt, R.string.auto_apply_title, new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    autoApplyLaunchers.get(position-1).apply(view.getContext());
                    afterApply.afterApply();
                }
            }).show();
            return true;
        }
    }

    private static void noAutoApplyMsg(Context context, List<ResolveInfo> installedLaunchers, boolean showAll){
        String headerText = context.getString(R.string.no_auto_apply);
        headerText = headerText.replace("{{number}}", installedLaunchers.size()-1+"");
        getLauncher(context, showAll, headerText, R.string.auto_apply_unavailable);
    }
    private static void noCustomLauncherMsg(Context context){
        String headerText = context.getString(R.string.no_custom_launchers);
        getLauncher(context, false, headerText, R.string.no_custom_launchers_title);
    }
    private static void getLauncher(final Context context, boolean showAll, final String headerText, final int titleId){
        final List<Launcher> launchers;
        if(showAll){
            launchers = Arrays.asList(Launcher.values());
        }else {
            launchers = getRecommendedLaunchers();
        }
        Builder dialogBuilder = getListDialog(
                context,
                launchers,
                headerText,
                titleId,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        launchers.get(position-1).getFromPlayStore(context);
                    }
                });
        if(!showAll) {
            dialogBuilder.setNeutralButton(R.string.show_more, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getLauncher(context, true, headerText, titleId);
                }
            });
        }
        dialogBuilder.show();
    }
    public static void launcherApplyOrGet(Context context, AfterApply afterApply){
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> installedLaunchers = getInstalledLaunchers(pm);
        if(autoApply(context, installedLaunchers, afterApply)){
            ;
        }else if(installedLaunchers.size()>4){
            noAutoApplyMsg(context, installedLaunchers, true);
        }else if(installedLaunchers.size()>1){
            noAutoApplyMsg(context, installedLaunchers, false);
        }else{
            noCustomLauncherMsg(context);
        }
    }

    public boolean isInstalled(PackageManager pm){
        log(name + ", " + action);
        try{
            pm.getApplicationInfo(packageName, 0);
            log("installed");
            return true;
        } catch (NameNotFoundException e) {
            log("not installed");
            return false;
        }
    }
    public void apply(Context context){
        Intent activityIntent;
        if(getLaunchIntent){
            activityIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        }else{
            activityIntent = new Intent(action);
        }
        Intent extrasIntent;
        Intent broadcastIntent = null;
        if(sendBroadcast){
            broadcastIntent = new Intent(action);
            extrasIntent = broadcastIntent;
        }else{
            extrasIntent = activityIntent;
        }
        for(Object[] extra : extras){
            String key = (String)extra[0];
            if(extra[1]==null){
                long myUserSerialNumber = 0;
                if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
                    UserHandle myUserHandle = android.os.Process.myUserHandle();
                    UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                    if (um != null) {
                        myUserSerialNumber = um.getSerialNumberForUser(myUserHandle);
                    }
                }
                String packageName = "com.theaetetuslabs.iconeffects.iconpack" + myUserSerialNumber;
                extrasIntent.putExtra(key, packageName);
            }else if(extra[1] instanceof String){
                extrasIntent.putExtra(key, (String) extra[1]);
            }else if(extra[1] instanceof Boolean){
                extrasIntent.putExtra(key, (boolean) extra[1]);
            }else if(extra[1] instanceof Integer){
                extrasIntent.putExtra(key, (int) extra[1]);
            }
        }
        for(int i : flags){
            extrasIntent.addFlags(i);
        }
        if(sendBroadcast){
            context.sendBroadcast(broadcastIntent);
        }
        if(cn!=null){
            activityIntent.setComponent(cn);
        }
        context.startActivity(activityIntent);
    }
    public void getFromPlayStore(Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(intent);

    }
    public void open(Context context){
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
    }
    private static Builder getListDialog(Context context, List<Launcher> launcherList, String headerTxt, int titleId, OnItemClickListener onClick){
        ArrayAdapter<Launcher> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, launcherList);
        ListView listView = getListView(context);
        TextView header = getHeaderView(context);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onClick);
        header.setText(headerTxt);
        return new Builder(context)
                .setTitle(titleId)
                .setView(listView)
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
    }
    private static ListView getListView(Context context){
        ListView listView = new ListView(context);
        listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return listView;
    }
    private static TextView getHeaderView(Context context){
        TextView textView = new TextView(context);
        textView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        float scale = context.getResources().getDisplayMetrics().density;
        int padding = (int)(16*scale + 0.5f);
        textView.setPadding(padding, padding, padding, padding);
        return textView;
    }
    private static void log(String msg){

        Log.d("LAUNCHERCHECKER", msg);
    }
    public String toString(){
        return name;
    }
}