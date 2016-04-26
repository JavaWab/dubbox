package com.westar.wangab.api.impl.user;

import com.westar.wangab.api.intf.IUserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/25.
 */
@Path("users")
public class UserService implements IUserService {
    @Path("getUser")
    @GET
    @Override
    public Map<String, String> get() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "wanganbang");

        return map;
    }
}
