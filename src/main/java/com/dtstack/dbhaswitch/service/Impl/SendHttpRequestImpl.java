package com.dtstack.dbhaswitch.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.mapper.RemovalDataDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.RemovalData;
import com.dtstack.dbhaswitch.service.SendHttpRequestService;
import com.dtstack.dbhaswitch.service.http.params.Response;
import com.dtstack.dbhaswitch.utils.HttpTools;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SendHttpRequestImpl implements SendHttpRequestService {

    public static final String haNewMaster = "/rdslite-service/v1/rds/highavailability/switchRdsMS";

    private static final String haMonitorEvent = "/rdslite-service/v1/rds/highavailability/MonitorEvent";

    private static final String haSlaveError = "/rdslite-service/v1/rds/update";

    private static final String AddDns = "/dns/add";

    private static final String UpdateDns = "/dns/update";

    private static final String DeleteDns = "/dns/delete";

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private RemovalDataDao removalDataDao;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${rdslite.agent.host}")
    private String host;

    @Value("${rdslite.agent.hostDns}")
    private String DnsHost;

    @Value("${rdslite.agent.port}")
    private String RdsPort;

    @Value("${rdslite.agent.Dns}")
    private String DnsPort;

    private int timeout;

    public String urlRds(String path, Object... params) {
        for (Object p : params) {
            path = path.replace("%s", p.toString());
        }
        return "http://" + host + ":" + RdsPort + path;
    }

    public String urlSendDns(String path, Object... params) {
        for (Object p : params) {
            path = path.replace("%s", p.toString());
        }
        return "http://" + DnsHost + ":" + DnsPort + path;
    }

    Integer num = 0;

    @Override
    public Integer sendNewMaster(Long rdsId) {

        Instance instance = new Instance();
        instance.setRdsId(rdsId);
        List<Instance> instanceList = instanceDao.select(instance);
        List<JSONObject> instanceListTmp = new ArrayList<>();
        for (Instance instanceTmp : instanceList) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("instanceId", instanceTmp.getInstanceId());
            body.put("isMaster", instanceTmp.getJudgeMaster());
            body.put("rdsId", instanceTmp.getRdsId());
            JSONObject jsonObj = new JSONObject(body);
            instanceListTmp.add(jsonObj);
        }
        Response response = post(urlRds(haNewMaster), instanceListTmp);
        if (response.getCode() != 0) {
            logger.error("Update Rds DataBase failed,rdsId = {}", rdsId);
        } else {
            logger.info("send The HTTP request success,instance = {}", instance.getRdsCode());
        }
        return response.getCode();
    }

    @Override
    public Integer sendUpdateDns(Long rdsId) {
        Instance instanceNew = instanceDao.sendDns(rdsId);
        HashMap<String, String> header = new HashMap<>();
        header.put("content-type", "application/json");
        HashMap<String, Object> body = new HashMap<>();
        body.put("fqdn", instanceNew.getRdsUrl());
        body.put("ipaddr", instanceNew.getIp());
        JSONObject jsonObject = new JSONObject(body);
        Response response = post(urlSendDns(UpdateDns), jsonObject);
        if (response.getCode() == 0) {
            logger.info("Update Dns success,rdsId = {}", rdsId);
        } else {
            logger.error("Update Dns failed ,rdsId = {}", rdsId);
        }
        return response.getCode();
    }

    @Override
    public void sendSlaveIoError(Long rdsId, Integer IoStatus, Integer runningStatus) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", rdsId);
        /**
         * 1是异步，2是半同步
         * */
        hashMap.put("copyType", IoStatus);
        hashMap.put("copyStatus", runningStatus);
        try {
            String getData = send(urlRds(haSlaveError), null, hashMap, false, false);
            System.out.println(getData);
            logger.warn("send Slave Io Error result,", getData);
        } catch (Exception e) {
            logger.error("send Slave Io Error,", e);
        }
    }

    @Override
    public boolean sendAddDns(Long rdsId) {
        Instance instanceAdd = instanceDao.sendDns(rdsId);
        HashMap<String, String> header = new HashMap<>();
        header.put("content type", "application/json");
        HashMap<String, Object> body = new HashMap<>();

        body.put("fqdn", instanceAdd.getRdsUrl());
        body.put("ipaddr", instanceAdd.getIp());
        JSONObject jsonObject = new JSONObject(body);
        Response response = post(urlSendDns(AddDns), jsonObject);
        if (response.getCode() == 0) {
            logger.info("Add Dns success,rdsId = {}", rdsId);
            return true;
        } else {
            logger.error("Add Dns failed ,rdsId = {}", rdsId);
            logger.error(response.toString());
            return false;
        }
    }

    @Override
    public boolean sendAddRemovalDns(String updateId) {
        RemovalData removalNewMaster = new RemovalData();
        removalNewMaster.setUpdateId(updateId);
        removalNewMaster.setDbStatus(3);
        removalNewMaster.setIsDelete("N");
        removalNewMaster = removalDataDao.selectOne(removalNewMaster);

        RemovalData removalOld = new RemovalData();
        removalOld.setUpdateId(updateId);
        removalOld.setDbStatus(1);
        removalOld.setIsDelete("N");
        removalOld = removalDataDao.selectOne(removalOld);

        HashMap<String, String> header = new HashMap<>();
        header.put("content type", "application/json");
        HashMap<String, Object> body = new HashMap<>();

        body.put("fqdn", removalOld.getRdsUrl());
        body.put("ipaddr", removalNewMaster.getIp());
        JSONObject jsonObject = new JSONObject(body);
        Response response = post(urlSendDns(UpdateDns), jsonObject);
        if (response.getCode() == 0) {
            logger.info("Add Dns success,rdsId = {}", removalNewMaster.getRdsId());
            return true;
        } else {
            logger.error("Add Dns failed ,rdsId = {}", removalNewMaster.getRdsId());
            logger.error(response.toString());
            return false;
        }
    }


    @Override
    public void sendDeleteDns(Long rdsId) {
        Instance instanceDelete = instanceDao.sendDns(rdsId);
        HashMap<String, String> header = new HashMap<>();
        header.put("content-type", "application/json");
        HashMap<String, Object> body = new HashMap<>();
        body.put("fqdn", instanceDelete.getRdsUrl());
        body.put("ipaddr", instanceDelete.getIp());
        JSONObject jsonObject = new JSONObject(body);
        Response response = post(urlSendDns(DeleteDns), jsonObject);
        if (response.getCode() == 0) {
            logger.info("Delete Dns success,rdsId = {}", rdsId);
        } else {
            logger.error("Delete Dns failed ,rdsId = {}", rdsId);
        }
    }

    @Override
    public void sendMonitorEvent(Long rdsId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Instance instance = new Instance();
        instance.setRdsId(rdsId);
        instance.setJudgeMaster(1);
        instance = instanceDao.selectOne(instance);
        hashMap.put("instanceId", instance.getInstanceId());
        JSONObject jsonObj = new JSONObject(hashMap);
        Response response = post(urlRds(haMonitorEvent), jsonObj);
        if (response.getCode() != 200) {
            logger.error("send Monitor Event failed,rdsId = {}", rdsId);
        }
    }

    public Response post(String url, Object body) {
        Response resp;
        try {
            String res = HttpTools.sendPost(url, JSON.toJSONString(body), timeout);
            resp = JSON.parseObject(res, Response.class);
            logger.info("post urlRds={}, body={}, resp={}", url, body, JSON.toJSONString(resp));
            System.out.println("打印resp：" + resp);
            return resp;
        } catch (IOException e) {
            logger.warn("post error, urlRds={}, body={}", url, body);
            logger.error("post error", e);

            resp = new Response();
            resp.setMessage(e.getMessage() == null ? "" : e.getMessage());
        }
        return resp;
    }

    public static String send(String url, Map<String, String> header, Map<String, Object> body, boolean isGet, boolean isCookieStore)
            throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpRequestBase request = isGet ? new HttpGet(url) : new HttpPost(url);
        request.addHeader("content-type", "application/json;charset=utf-8");
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (body != null && !body.isEmpty()) {
            if (request instanceof HttpPost) {
                HttpPost req = (HttpPost) request;
                req.setEntity(new StringEntity(JSONObject.toJSONString(body), "utf-8"));
                request = req;
            }
        }
        HttpResponse response = null;
        if (isCookieStore) {
            HttpClientContext context = HttpClientContext.create();
            CookieStore cookieStore = new BasicCookieStore();
            context.setCookieStore(cookieStore);
            response = httpClient.execute(request, context);
        } else {
            response = httpClient.execute(request);
        }
        return EntityUtils.toString(response.getEntity(), "utf-8");
    }

}
