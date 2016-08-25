package com.westar.wangab.api.intf;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by wanganbang on 6/5/16.
 */
public class DeleteParams implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull(message = "filePath is not null")
    @NotEmpty(message = "filePath is not null")
    private String url;
    @NotNull(message = "uerid is not null")
    @NotEmpty(message = "uerid is not null")
    private String uerid;
    @NotNull(message = "effect is not null")
    @NotEmpty(message = "effect is not null")
    private String effect;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUerid() {
        return uerid;
    }

    public void setUerid(String uerid) {
        this.uerid = uerid;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
