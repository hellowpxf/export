package service;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import dao.Fruit;
import org.apache.ibatis.session.SqlSession;
import utils.SqlSessionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @description:FruitServiceImpl
 * @author:pxf
 * @data:2022/09/30
 **/
public class FruitServiceImpl implements FruitService {
    private static final Logger logger = LoggerFactory.getLogger(FruitServiceImpl.class);
    @Override
    public int saveFruit(Fruit fruit) {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        System.out.println(sqlSession);
        sqlSession.insert("dao.Fruit.insertFruit",fruit);
        sqlSession.commit();
        SqlSessionUtils.closeSession();
        return  0;
    }

    @Override
    public Fruit getFruit(int id) {
        return null;
    }

}
