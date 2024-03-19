package tax;

import java.util.HashMap;


public interface TaxMapper {
   Tax selectAll(HashMap hashMap);
   int addTax(Tax tax);
}
