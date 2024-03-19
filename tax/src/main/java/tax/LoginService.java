package tax;

import org.apache.ibatis.session.SqlSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import utils.DateUtils;
import utils.SqlSessionUtils;

import java.util.Date;
import java.util.List;

/**
 * @description:LoginService
 * @author:pxf
 * @data:2023/04/28
 **/
@Service
public class LoginService {
    private RedisTemplate<Object,Object> rt;
    public boolean register(User user){
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        SqlSession sqlSession = SqlSessionUtils.getSession();
        List<User> userList =  sqlSession.selectList("tax.UserMapper.getUserByUsername",user.getUsername());
        boolean flag = false;
        if (userList.size()<=0){
            String  partner = "yyyy-MM-dd hh:mm:ss";
            user.setRegisterDate(DateUtils.formatDate(partner));
            try {
                sqlSession.insert("tax.UserMapper.addUser",user);
                sqlSession.commit();
            }catch (Exception e){
                sqlSession.rollback();
                e.printStackTrace();
                System.out.println("用户新增失败");
            }finally {
                SqlSessionUtils.closeSession();
            }
        }
        return flag;
    }
    @Cacheable(value = "pc", key = "'tax_all'")
    public boolean login (User user) {
        String hashedPassword = null;
        String username = user.getUsername();
         Object o = rt.opsForValue().get(username);
        if (o == null){
            // 从数据库中获取加密后的密码
            hashedPassword = getHashedPassword(username);
            if (hashedPassword == null) {
                return false;
            }else {
                rt.opsForSet().add(username,hashedPassword);
            }
        }
        String password = user.getPassword();

        // 对用户输入的密码进行加密
        String inputHashedPassword = BCrypt.hashpw(password, hashedPassword);
        // 将加密后的密码与数据库中存储的密码进行比较
        return hashedPassword.equals(inputHashedPassword);
    }
    private String getHashedPassword(String username) {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        List<User> u =  sqlSession.selectList("tax.UserMapper.getUserByUsername",username);
        SqlSessionUtils.closeSession();
        if(u.size()>0){
            return  u.get(0).getPassword();
        }
        return  null;
    }
}

