package com.dtstack.dbhaswitch.utils;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class BaseDAO<T extends ModelBase> extends SqlSessionDaoSupport {

    protected Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    protected final String COUNT_COMMAND;
    protected final String DELETE_COMMAND;
    protected final String INSERT_COMMAND;
    protected final String SELECT_COMMAND;
    protected final String SQLMAP_PREFIX;

    protected SqlSessionTemplate sqlSessionTemplate;

    @Resource
    @Override
    public void setSqlSessionTemplate(@Qualifier("rdsliteSqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }


    protected final String UPDATE_COMMAND;

    public static final int MAX_PAGE_SIZE = 200;
    public static final String CONNECTOR = "-";

    public BaseDAO(String prefix) {
        this.SQLMAP_PREFIX = prefix;
        this.COUNT_COMMAND = SQLMAP_PREFIX + CONNECTOR + "count";
        this.DELETE_COMMAND = SQLMAP_PREFIX + CONNECTOR + "delete";
        this.SELECT_COMMAND = SQLMAP_PREFIX + CONNECTOR + "select";
        this.UPDATE_COMMAND = SQLMAP_PREFIX + CONNECTOR + "update";
        this.INSERT_COMMAND = SQLMAP_PREFIX + CONNECTOR + "insert";
    }

    public Integer count(T param) {
        return (Integer) this.sqlSessionTemplate.selectOne(COUNT_COMMAND, getParam(param));
    }

    public int delete(T param) {
        Map<String, Object> map = getParam(param);
        if (map.size() == 0) {
            throw new IllegalArgumentException("empty arg list for delete !?");
        }
        return this.sqlSessionTemplate.delete(DELETE_COMMAND, map);
    }

    public Map<String, Object> getParam(T param) {
        if (Objects.isNull(param)) {
            param = (T) new ModelBase();
        }
        Map<String, Object> p = new HashMap<>();
        //获取关联的所有类，本类以及所有父类
        boolean ret = true;
        Class oo = param.getClass();
        List<Class> clazzs = new ArrayList<Class>();
        while (ret) {
            clazzs.add(oo);
            oo = oo.getSuperclass();
            if (oo == null || oo == Object.class) {
                break;
            }
        }

        for (int i = 0; i < clazzs.size(); i++) {
            Field[] declaredFields = clazzs.get(i).getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                //过滤 static 和 final 类型
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    p.put(field.getName(), field.get(param));
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return p;
    }


    public Integer insert(T param) {
        return (Integer) this.sqlSessionTemplate.insert(INSERT_COMMAND, getParam(param));
    }

    public Integer insertWithId(T param) {
        return (Integer) this.sqlSessionTemplate.insert(INSERT_COMMAND, param);
    }

    public List<T> select(T param) {
        return this.select(param, 0, Integer.MAX_VALUE);
    }

    public T selectById(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        List<T> list = this.sqlSessionTemplate.selectList(SELECT_COMMAND, map);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<T> select(T param, int start, int limit) {
        Map<String, Object> map = getParam(param);
        map.put("start", start);
        map.put("limit", limit);
        return this.sqlSessionTemplate.selectList(SELECT_COMMAND, map);
    }

    public T selectOne(T param) {
        List<T> list = this.select(param, 0, 1);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }


    public int update(T param) {
        return this.sqlSessionTemplate.update(UPDATE_COMMAND, getParam(param));
    }
}
