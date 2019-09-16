/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.translatemanager;

import java.net.MalformedURLException;
import java.lang.reflect.*;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author sugichan
 */
public class TranslateManager extends JavaPlugin implements Listener {

    //  https://www.excite.co.jp/world/english/?before={hello}&wb_lp={ENJA}
    
    private Config config;
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        config = new Config( this );
    }
    
    @Override
    public void onDisable() {
        
    }

    public void Prt( Player player, String msg ) {
        if ( player == null ) {
            Bukkit.broadcastMessage( msg );
        } else {
            player.sendMessage( msg );
        }
    }
    
    @Override
    public boolean onCommand( CommandSender sender,Command cmd, String commandLabel, String[] args ) {
        Player player = ( sender instanceof Player ) ? ( Player )sender:( Player )null;

        if ( cmd.getName().toLowerCase().equalsIgnoreCase( "trans" ) ) {
            if ( args.length > 0 ) {
                
                String URL = "https://www.excite.co.jp/world/";

                if ( args[0].equals( "reload" ) ) {
                    config.load();
                    return true;
                }

                String RetMessage;
                String GetLang = args[0].toUpperCase();
                // Bukkit.broadcastMessage( "LANG : " + args[0] );
                // Bukkit.broadcastMessage( "MSG : " + args[1] );
                try {
                    String OrgMessage;
                    
                    if ( config.getCountry( GetLang ).equals( "Space" ) ) {
                        List GLL = config.getLangs();
                        String LM = "Language: ";
                        for ( Iterator it = GLL.iterator(); it.hasNext(); ) {
                            LM += (String) it.next() + " ";
                        }
                        Prt( player, LM );
                        return false;
                    } else {
                        OrgMessage = args[1];
                        //  Bukkit.getServer().getConsoleSender().sendMessage( "Length = " + args.length );
                        if ( args.length > 1 ) {
                            for ( int i=2; i<args.length; i++ ) {
                                Bukkit.getServer().getConsoleSender().sendMessage( "args[" + i + "] = " + args[i] );
                                OrgMessage += " " + args[i];
                            }
                        }

                        RetMessage = new TransText( URL +  config.getCountry( GetLang ) + "/", player ).connect( OrgMessage.replace( " ", "%20" ), config.getBaseLanguage() + GetLang );
                    }
                    //  翻訳文＋元の文章を括弧内に表示
                    RetMessage += ChatColor.YELLOW + " (" + OrgMessage + ")[Translate " + GetLang + "]";
                    if ( player == null ) {
                        Bukkit.broadcastMessage( "<System> " + RetMessage );
                        //  Bukkit.broadcastMessage( "<System> " + GetLang + " (Translate) " + RetMessage );
                        //  Bukkit.broadcastMessage( "<System> [ORG] " + args[1] );
                    } else {
                        player.chat( RetMessage );
                        //  player.chat( GetLang + " (Translate) " + RetMessage );
                        //  player.chat( "[ORG]" + args[1] );
                    }
                } catch ( IndexOutOfBoundsException | MalformedURLException e ) {
                    Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "First Error" );
                }
                return true;
            }
            return false;
        }
        return true;
    }

    private static Method getMethod( String name, Class<?> clazz ) {
        for ( Method m : clazz.getDeclaredMethods() ) {
            if ( m.getName().equals( name ) ) return m;
        }
        return null;
    }

    public static String getLanguage( Player p ) {
        try {
            Object ep = getMethod( "getHandle", p.getClass() ).invoke( p, ( Object[] ) null );
            Field f = ep.getClass().getDeclaredField( "locale" );
            f.setAccessible( true );
            String language = (String) f.get( ep );
            return language;
        }
        catch ( IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | InvocationTargetException t ) {
            return null;
        }
    }
 
    @EventHandler( priority=EventPriority.LOW, ignoreCancelled=true )
    public void onAsyncPlayerChatLow( AsyncPlayerChatEvent event ) throws MalformedURLException {
        //  チャットレスポンスに影響があるので、できるだけ早い段階でキャンセルする仕組みを考えている
        String getMessage = event.getMessage();
        String URL = "https://www.excite.co.jp/world/";

        if ( getMessage.contains( "[Translate" ) ) return;

        String PlayerLang = getLanguage( event.getPlayer() );
        if ( PlayerLang == null ) return;
        //  String Player2ByteLang = PlayerLang.substring( PlayerLang.indexOf( "_" ) + 1 ).toUpperCase();
        String Player2ByteLang = PlayerLang.substring( 0, 2 ).toUpperCase();

        //  翻訳指定がある場合、ここで変更
        if ( config.getPlayer( event.getPlayer().getDisplayName() ) ) Player2ByteLang = config.getLang( event.getPlayer().getDisplayName() );

        //  日本語の場合は変換不要なのでキャンセル
        //  if ( Player2ByteLang.equals( "JP" ) ) return;
        if ( Player2ByteLang.equals( config.getBaseLanguage() ) ) return;
        
        //  台湾、中国の場合、ZHとなってしまうが、TranslateAPI上はCHなので強制変換
        if ( Player2ByteLang.equals( "ZH" ) ) Player2ByteLang = "CH";

        //  日本語意外の場合は、識別国をコンソールログに表示
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "[TransMan]" + ChatColor.YELLOW + " Player Country is " + PlayerLang + "[" + Player2ByteLang + "]" );
        
        //  翻訳サイトのURL指定、変換しない国の場合は、"Space" が戻ってくる
        String TargetCountry = config.getCountry( Player2ByteLang );
        if ( TargetCountry.equals( "Space" ) ) return;
        URL += TargetCountry + "/";

        //  翻訳開始
        try {
            String BeforMessage = ChatColor.YELLOW + " (" + getMessage + ")[Translate " +  Player2ByteLang + "]";
            String TranslationMessage = new TransText( URL, event.getPlayer() ).connect( getMessage.replace( " ", "%20" ), Player2ByteLang + config.getBaseLanguage() );
            if ( !BeforMessage.contains( TranslationMessage ) ) {
                event.setMessage( TranslationMessage + BeforMessage );
                //  event.setMessage( "[ORG] " + getMessage );
                //  event.getPlayer().chat( Language + " (Translate) " + TranslationMessage );
            }
        } catch ( IndexOutOfBoundsException e ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Translate Error" );
        }

    }
}
