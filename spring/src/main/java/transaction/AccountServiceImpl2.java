package transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description:AccountServiceImpl2
 * @author:pxf
 * @data:2023/08/22
 **/
@Service("accountService2")
public class AccountServiceImpl2 implements AccountService {
    @Resource(name = "accountDao")
     private AccountDaoImpl accountDao;
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_UNCOMMITTED)
    public void save(Account act) {
        Account account =   accountDao.selectByActno("act-003");
        if (account != null){
            System.out.println("数据已经落库"+account.toString());
        }else {
            System.out.println("数据没落库");
        }
    }
}
