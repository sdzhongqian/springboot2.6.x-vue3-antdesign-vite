package com.qingfeng.base.controller;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月18日 21:58:00
 */
public class BaseController {

    public void writeJson(HttpServletResponse response, Object object) throws IOException {

        response.setContentType("text/html;charset=utf-8");
        ObjectMapper objMapper = new ObjectMapper();
        JsonGenerator jsonGenerator = objMapper.getFactory().createGenerator(response.getOutputStream(), JsonEncoding.UTF8);
        jsonGenerator.writeObject(object);
        jsonGenerator.flush();
        jsonGenerator.close();
    }

}
