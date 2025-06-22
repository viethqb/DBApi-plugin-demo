package com.gitee.freakchicken.demo.plugin;

import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.common.ResponseDto;
import com.gitee.freakchicken.dbapi.plugin.GlobalTransformPlugin;

public class AmisGlobalTransformPlugin extends GlobalTransformPlugin {
    @Override
    public void init() {

    }

    /**
     * Returns the data format specified by the AMIS framework
     */
    @Override
    public Object transform(ResponseDto data, String params) {
        JSONObject obj = new JSONObject();
        if (data.getSuccess()) {
            obj.put("status", 0);
            obj.put("msg", data.getMsg());
            obj.put("data", data.getData());
        } else {
            obj.put("status", -1);
            obj.put("msg", data.getMsg());
            obj.put("data", data.getData());
        }
        
        return obj;
    }

    @Override
    public String getName() {
        return "AMIS format conversion plugin";
    }

    @Override
    public String getDescription() {
        return "Convert the data format into the format required by the AMIS framework: {\"msg\":\"xxx\",\"status\":0,\"data\":{\"key\":\"value\"}}";
    }

    @Override
    public String getParamDescription() {
        return "No need to fill in parameters";
    }
}
