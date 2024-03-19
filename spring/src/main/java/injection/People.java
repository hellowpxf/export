package injection;

import java.util.List;

/**
 * @description:People
 * @author:pxf
 * @data:2023/07/20
 **/
public class People {
    // 一个人有多个名字
    private List<String> names;

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "People{" +
                "names=" + names +
                '}';
    }
}
