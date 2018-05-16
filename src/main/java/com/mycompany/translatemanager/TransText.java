/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.translatemanager;

import java.net.MalformedURLException;
import org.bukkit.entity.Player;

/**
 *
 * @author sugichan
 */
public class TransText extends HttpGetClient {
    private final Player player;
     
    public TransText(String url, Player player) throws MalformedURLException {
        super( url );
        this.player = player;
    }
 
    @Override
    protected void run( String res ) {
        player.sendMessage( res );
    }
    
}
