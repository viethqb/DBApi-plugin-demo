package com.gitee.freakchicken.demo.plugin;

import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.plugin.TransformPlugin;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * md5 field encryption plugin
 */
public class EncryptTransformerPlugin extends TransformPlugin {

    @Override
    public void init() {
        super.logger.info("EncryptTransformerPlugin init ...");
    }

    /**
     * MD5 encryption
     * @param data             Result data returned after execution of the executor
     * @param localPluginParam Plugin local parameters
     * @return
     */
    @Override
    public Object transform(Object data, String localPluginParam) {
        List<JSONObject> list = (List<JSONObject>) data;
        if (StringUtils.isNoneBlank(localPluginParam)) {
            String[] columns = localPluginParam.split(";");
            list.stream().forEach(t -> {
                for (String column : columns) {
                    t.put(column, DigestUtils.md5Hex(t.getString(column)));
                }
            });
        }
        return list;
    }


    /**
     * Plugin name, used to display on the page to prompt the user
     *
     * @return
     */
    @Override
    public String getName() {
        return "Field encryption plugin";
    }

    /**
     * 插件功能描述，用于在页面上显示，提示用户
     *
     * @return
     */
    @Override
    public String getDescription() {
        return "MD5 encryption of fields";
    }

    /**
     * Plugin parameter description, used to display on the page and prompt the user
     *
     * @return
     */
    @Override
    public String getParamDescription() {
        return "Fill in the fields to be encrypted in the plugin parameters. Use English semicolons to separate multiple fields. If you do not fill in the fields, you do not need to encrypt any fields.";
    }
}
