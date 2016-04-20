package com.westar.wangab.api.intf;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by wanganbang on 4/19/16.
 */
public class RequestExtParam implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull(message = "ExtType is not null")
    @NotEmpty(message = "ExtType is not null")
    @Length(min = 1, max = 10, message = "ExtType is too long")
    private String extType;
    @NotNull(message = "extInfo is not null")
    @NotEmpty(message = "extInfo is not null")
    private String extInfo;

    public String getExtType() {
        return extType;
    }

    public void setExtType(String extType) {
        this.extType = extType;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
}
