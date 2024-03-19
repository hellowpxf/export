package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;


/**
 * @description:SqlSessionUtils
 * @author:pxf
 * @data:2022/09/30
 **/
public class SqlSessionUtils {

    // 工具类的构造方法一般都是私有化的。
    // 工具类中所有的方法都是静态的，直接采用类名即可调用。不需要new对象。
    // 为了防止new对象，构造方法私有化。
    private SqlSessionUtils(){}

    private static   ThreadLocal<SqlSession> sqlSessionThreadLocal = new ThreadLocal<>();




    private static SqlSessionFactory sqlSessionFactory;

    // 类加载时执行
    // SqlSessionUtil工具类在进行第一次加载的时候，解析mybatis-config.xml文件。创建SqlSessionFactory对象。
    static {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    /*public static SqlSession openSession(){
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // SqlSessionFactory对象：一个SqlSessionFactory对应一个environment，一个environment通常是一个数据库。
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }*/

    /**
     * 获取会话对象。
     * @return 会话对象
     */
    public static SqlSession openSession(){
        return sqlSessionFactory.openSession();
    }

    public static SqlSession getSession (){
        //获取当前线程下的session
      SqlSession  sqlSession = sqlSessionThreadLocal.get();
        if (sqlSession == null) {
            //如果session为null，则从SessionFactory中获取一个session放入ThreadLocal
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            sqlSessionThreadLocal.set(sqlSession);
            System.out.println("ThreadLocal内存地址"+sqlSessionThreadLocal);
        }

        return  sqlSession;
    }

    public static void closeSession(){
        SqlSession session = sqlSessionThreadLocal.get();
        if(session !=null){
            session.close();
            //如果session不为null，则关闭session，并清空ThreadLocal
            sqlSessionThreadLocal.remove();
        }
    }
}
