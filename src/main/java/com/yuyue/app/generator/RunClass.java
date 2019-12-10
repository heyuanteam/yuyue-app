package com.yuyue.app.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * mybatis逆向工程,会把原来的代码覆盖，慎用
 */

public class RunClass {
    public static void main(String[] args) throws Exception{

        RunClass app = new RunClass();
        System.out.println(app.getClass().getResource("/").getPath());
        app.generator();
        System.out.println(System.getProperty("user.dir"));
    }

    public void generator() throws Exception{
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("mybatis-generator.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(resourceAsStream);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for(String warning:warnings){
            System.out.println(warning);
        }
    }

//        StringBuilder sb = new StringBuilder();
//        String id = "B044C53B38BA4E84B507E62402683E26";
//        String sourceId = "50D530BBA2394D25A0476182D8834ACD";
//
//        try {
//            CloseableHttpClient client = null;
//            CloseableHttpResponse response = null;
//            try {
//                AppUser appUser = loginService.getAppUserMsg("","",sourceId);
//                HttpGet httpGet = new HttpGet(Variables.sendRefundUrl + "?id="+id+"&sourceId="+sourceId);
//                httpGet.setHeader("token",loginService.getToken(appUser));
//
//                client = HttpClients.createDefault();
//                response = client.execute(httpGet);
//                HttpEntity entity = response.getEntity();
//                String result = EntityUtils.toString(entity);
//                System.out.println(result);
//            } finally {
//                if (response != null) {
//                    response.close();
//                }
//                if (client != null) {
//                    client.close();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sb.append(id).append("&").append(sourceId);
//        GouldUtils.doPost(Variables.sendRefundUrl,sb.toString(), ContentType.APPLICATION_JSON);

//        Map<String, String> params = Maps.newHashMap();
//        params.put("id", id);
//        params.put("sourceId", sourceId);
//        GouldUtils.doPost(Variables.sendRefundUrl, params);
//        HttpUtils.doPost(Variables.sendRefundUrl,sb.toString());
}
