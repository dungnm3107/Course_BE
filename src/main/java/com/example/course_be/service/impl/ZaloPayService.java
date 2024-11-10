package com.example.course_be.service.impl;

import com.example.course_be.config.ZaloPayConfig;
import com.example.course_be.request.order.CreateOrderRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.zalopay.crypto.HMACUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZaloPayService {

    private final ZaloPayConfig zaloPayConfig;

    @Autowired
    public ZaloPayService(ZaloPayConfig zaloPayConfig) {
        this.zaloPayConfig = zaloPayConfig;
    }

    public String createOrder(CreateOrderRequest createOrderRequest) throws Exception {
        Random rand = new Random();
        int randomId = rand.nextInt(1000000);
        Map<String, Object> embedData = new HashMap<>();
        JSONArray items = new JSONArray();

        Map<String, Object> order = new HashMap<>() {{
            put("app_id", zaloPayConfig.getAppId());
            put("app_trans_id", getCurrentTimeString("yyMMdd") + "_" + randomId);
            put("app_time", System.currentTimeMillis());
            put("app_user", createOrderRequest.getUserId());
            put("amount", createOrderRequest.getAmount());
            put("description", createOrderRequest.getDescription());
            put("bank_code", "zalopayapp");
            put("item", items.toString());
            put("embed_data", new JSONObject(embedData).toString());
        }};

        // Debugging: Print the contents of the order map
        System.out.println("Order Map: " + order);

        // Ensure none of the keys are null before using them
        for (String key : order.keySet()) {
            if (order.get(key) == null) {
                throw new IllegalArgumentException("Key " + key + " is null in order map.");
            }
        }

        String data = String.join("|", order.get("app_id").toString(),
                order.get("app_trans_id").toString(),
                order.get("app_user").toString(),
                order.get("amount").toString(),
                order.get("app_time").toString(),
                order.get("embed_data").toString(),
                order.get("item").toString());
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, zaloPayConfig.getKey1(), data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(zaloPayConfig.getEndpoint());
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        return resultJsonStr.toString(); // Return result
    }


    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTime());
    }
}
