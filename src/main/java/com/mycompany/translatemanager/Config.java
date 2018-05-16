/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.translatemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author sugichan
 */
public class Config {

    private final Plugin plugin;
    private FileConfiguration config = null;

    //  変換するプレイヤー名
    private List< String > LangPlayer;
    //  US,JPなど
    private Map< String, String > Language;
    //  english,japanese など
    private Map< String, String > Country;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info( "Config Loading now..." );
        load();
    }
    
    /*
     * 設定をロードします
     */
    public void load() {
        //  ワーク変数
        List< String > getstr;
        
        // 設定ファイルを保存
        plugin.saveDefaultConfig();
        if (config != null) { // configが非null == リロードで呼び出された
            plugin.getLogger().info( "Config Reloading now..." );
            plugin.reloadConfig();
        }
        config = plugin.getConfig();

        LangPlayer = new ArrayList<>();
        Language = new HashMap<>();
        getstr = ( List< String > ) config.getList( "Translate" );
        for( int i = 0; i<getstr.size(); i++ ) {
            String[] param = getstr.get( i ).split(",");
            LangPlayer.add( param[0] );
            Language.put( param[0], param[1] );
        }

        Country = new HashMap<>();
        getstr = ( List< String > ) config.getList( "Country" );
        for( int i = 0; i<getstr.size(); i++ ) {
            String[] param = getstr.get( i ).split(",");
            Country.put( param[0], param[1] );
        }
    }

    public void Status() {
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "=== Translate Status ===" );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "========================" );
    }

    public boolean getPlayer( String name ) {
        return LangPlayer.contains( name );
    }
    
    public String getLang( String PlayerName ) {
        if ( Language.containsKey( PlayerName ) ) return Language.get( PlayerName );
        return "EN";
    }
    
    public String getCountry( String sd ) {
        if ( Country.containsKey( sd ) ) return Country.get( sd );
        return "Space";
    }
    
}
