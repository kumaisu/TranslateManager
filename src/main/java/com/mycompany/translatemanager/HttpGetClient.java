/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.translatemanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
 
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
 
/**
 *
 * @author sugichan
 */
public abstract class HttpGetClient {
    private final URL url;

    public HttpGetClient( String url ) throws MalformedURLException {
        // Bukkit.broadcastMessage( url );
        this.url = new URL( url );
    }
     
    protected abstract void run( String res );
     
    public String connect( String msg, String lang ) {
        HttpURLConnection connection = null;
        String RetMessage = "";
      
        try {
            connection = ( HttpURLConnection ) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setInstanceFollowRedirects( false );
            connection.setDoOutput(true);
            // クエリー文の生成・送信
            // 出力ストリームを取得
            try ( PrintWriter out = new PrintWriter( connection.getOutputStream() ) ) {
                // クエリー文の生成・送信
                out.print( "before=" + msg + "&wb_lp=" + lang );
            }

            // 一行ずつ読み込む
            try ( BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) ) ) {
                // 一行ずつ読み込む
                String aline;
                while (( aline = in.readLine() ) != null) {
                    if ( aline.contains( "<textarea" ) && aline.contains( "id=\"after\"" ) ) {
                        RetMessage = aline.substring( aline.indexOf( ">" ) + 1, aline.indexOf( "</textarea>" ) );
                        // Bukkit.getServer().getConsoleSender().sendMessage( retmsg );
                    }
                }
            }
            //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "Translate Read Over" );
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Access IO Exception Error" );
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return RetMessage;
    }
 
}