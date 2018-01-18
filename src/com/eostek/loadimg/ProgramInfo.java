
package com.eostek.loadimg;

/**
 * 单个节目业务类
 */
public class ProgramInfo {
    // TODO add others
    private String logoUri;

    private String videoName;

    private int definition;
    
    public ProgramInfo(){
        
    }
    
    public ProgramInfo(String logoUri,String videoName){
        this.logoUri=logoUri;
        this.videoName=videoName;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getDefinition() {
        return definition;
    }

    public void setDefinition(int definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "[videoName: " + videoName + ", logoUri: "+ logoUri +"]";
    }
}
