import com.alibaba.fastjson.JSONObject;

/**
 * @description:Text
 * @author:pxf
 * @data:2023/04/19
 **/
public class Text {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "John");
        jsonObject.put("age", 30);
        jsonObject.put("city", "New York");

        jsonObject.keySet().stream().forEach(key -> {
            System.out.println(key + " : " + jsonObject.get(key));
        });

    }
}
