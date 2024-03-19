
import fistApp.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import transaction.Account;
import transaction.AccountService;


/**
 * @description:BankTest
 * @author:pxf
 * @data:2023/08/21
 **/
public class BankTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("springtamplate.xml");
        AccountService accountService = applicationContext.getBean("accountService", AccountService.class);
        try {
            accountService.save(new Account("act-003",3000.00) );
            System.out.println("转账成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}