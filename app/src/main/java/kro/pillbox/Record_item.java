package kro.pillbox;

/**
 * Created by user on 2016/3/23.
 */
public class Record_item {
    String drugName;
    boolean morning, noon, evening, sleep;

    public Record_item(String drugName, boolean morning, boolean noon, boolean evening, boolean sleep) {
        this.drugName = drugName;
        this.morning = morning;
        this.noon = noon;
        this.evening = evening;
        this.sleep = sleep;

    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public boolean isEvening() {
        return evening;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }

    public boolean isNoon() {
        return noon;
    }

    public void setNoon(boolean noon) {
        this.noon = noon;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

}
