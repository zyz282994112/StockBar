package test;

/**
 * Created by yizhou on 14-4-3.
 */
public class TxtValue {
    private String juzi;
    private Integer value;

    public TxtValue(String juzi,Integer value){this.juzi = juzi;this.value = value;}

    public String getJuzi() {
        return juzi;
    }

    public void setJuzi(String juzi) {
        this.juzi = juzi;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
