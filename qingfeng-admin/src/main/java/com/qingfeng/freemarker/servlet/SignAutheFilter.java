package com.qingfeng.freemarker.servlet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingfeng.common.entity.Authen;
import com.qingfeng.common.service.IAuthenService;
import com.qingfeng.framework.servlet.FilterConfig;
import com.qingfeng.utils.IpUtils;
import com.qingfeng.utils.Json;
import com.qingfeng.utils.MD5;
import com.qingfeng.utils.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @ProjectName SignAutheFilter
 * @author qingfeng
 * @version 1.0.0
 * SpringBoot使用拦截器实现签名认证(鉴权)
 * @WebFilter注解指定要被过滤的URL
 * 一个URL会被多个过滤器过滤时,还可以使用@Order(x)来指定过滤request的先后顺序,x数字越小越先过滤
 * @createTime 2021/8/26 14:35
 */
@WebFilter(urlPatterns = { "/port/*" },filterName = "securityRequestFilter")
public class SignAutheFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(SignAutheFilter.class);

    @Autowired
    private IAuthenService authenService;

    @Value("${permittedIps}")
    private String permittedIps;
//    @Value("${appId}")
//    private String appId;
//    @Value("${appSecret}")
//    private String appSecret;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String msg = "";
        try {
            String authorization = request.getHeader("Authorization");
            System.out.println("授权信息： " + authorization);
            String[] info = authorization.split(",");

            // 获取客户端ip
            String ip = IpUtils.getIpAddr(request);
            System.out.println("请求IP： " + ip);

            /*
             * 读取请求体中的数据(字符串形式)
             * 注:由于同一个流不能读取多次;如果在这里读取了请求体中的数据,那么@RequestBody中就不能读取到了
             *    会抛出异常并提示getReader() has already been called for this request
             * 解决办法:先将读取出来的流数据存起来作为一个常量属性.然后每次读的时候,都需要先将这个属性值写入,再读出.
             *        即每次获取的其实是不同的流,但是获取到的数据都是一样的.
             *        这里我们借助HttpServletRequestWrapper类来实现
             *      注:此方法涉及到流的读写、耗性能;
             */
            MyRequestWrapper mrw = new MyRequestWrapper(request);
            String bodyString = mrw.getBody();
            System.out.println("请求body信息： " + bodyString);

            // 获取几个相关的字符
            // 由于authorization类似于
            // cardid="1234554321",timestamp="9897969594",signature="a69eae32a0ec746d5f6bf9bf9771ae36"
            // 这样的,所以逻辑是下面这样的
            int cardidIndex = info[0].indexOf("=") + 2;
            String appid = info[0].substring(cardidIndex, info[0].length() - 1);
            System.out.println(" appId 信息： " + appid);
            int timestampIndex = info[1].indexOf("=") + 2;
            String timestamp = info[1].substring(timestampIndex, info[1].length() - 1);
            int signatureIndex = info[2].indexOf("=") + 2;
            String signature = info[2].substring(signatureIndex, info[2].length() - 1);

            //根据appid查询秘钥
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("appid",appid);
            Authen authen = authenService.getOne(queryWrapper);

            String tmptString = "";
            if(Verify.verifyIsNotNull(authen)){
                System.out.println(timestamp+","+authen.getAppSecret()+","+bodyString);
                tmptString = MD5.md5Upper(timestamp+","+authen.getAppSecret()+","+bodyString);
                System.out.println(signature +":"+ tmptString);
            }else{
                msg = "应用ID(appId)不存在。";
            }

            // 判断该ip是否合法
            boolean containIp = false;
            if(permittedIps.contains("0.0.0.0")){
                containIp = true;
            }else{
                for (String string : permittedIps.split(",")) {
                    if (string.equals(ip)) {
                        containIp = true;
                        break;
                    }
                }
            }

            // 再判断Authorization内容是否正确,进而判断是否最终放行
            boolean couldPass = containIp && tmptString.equals(signature);
            if (couldPass) {
                // 放行
                System.out.println("====================放行================");
                chain.doFilter(mrw, response);
                return;
            }else{
                msg = "签名错误或请求IP未授权。";
            }

            ServletOutputStream out = response.getOutputStream();
            Json json = new Json();
            json.setSuccess(false);
            json.setMsg("403 Forbidden:"+msg);
            response.setContentType("text/html;charset=utf-8");
            ObjectMapper objMapper = new ObjectMapper();
            JsonGenerator jsonGenerator = objMapper.getJsonFactory()
                    .createJsonGenerator(response.getOutputStream(),
                            JsonEncoding.UTF8);

            jsonGenerator.writeObject(json);
            jsonGenerator.flush();
            jsonGenerator.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Json json = new Json();
            json.setSuccess(false);
            json.setMsg("403 Forbidden:"+e.getMessage());
            response.setContentType("text/html;charset=utf-8");
            ObjectMapper objMapper = new ObjectMapper();
            JsonGenerator jsonGenerator = objMapper.getJsonFactory()
                    .createJsonGenerator(response.getOutputStream(),
                            JsonEncoding.UTF8);

            jsonGenerator.writeObject(json);
            jsonGenerator.flush();
            jsonGenerator.close();
        }
    }

    @Override
    public void destroy() {

    }

}

/**
 * 辅助类 ---> 变相使得可以多次通过(不同)流读取相同数据
 *
 * @author qingfeng
 * @createTime 2021/8/26 14:35
 */
class MyRequestWrapper extends HttpServletRequestWrapper {

    private final String body;

    public String getBody() {
        return body;
    }

    public MyRequestWrapper(final HttpServletRequest request) throws IOException {
        super(request);
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        body = sb.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            /*
             * 重写ServletInputStream的父类InputStream的方法
             */
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}