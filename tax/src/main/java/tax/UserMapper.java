package tax;

import java.util.HashMap;

/**
 * @description:UserMapper
 * @author:pxf
 * @data:2023/04/28
 **/
public interface UserMapper {
    int addUser(User user);
    Tax selectAll(HashMap hashMap);
    User getUserByUsername(String name);


}
