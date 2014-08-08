/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package livestreamerjfxgui;

/**
 *
 * @author huoni
 */
public class Channel {
    private String alias = "";
    private String url = "";

    public Channel() {
    }
    
    public Channel(String alias, String url) {
        this.alias = alias;
        this.url = url;
    }

    public String getAlias() {
        return alias;
    }

    public String getUrl() {
        return url;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return alias + " (" + url + ")";
    }
    
}
