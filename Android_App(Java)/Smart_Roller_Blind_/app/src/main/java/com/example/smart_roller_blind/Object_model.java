package com.example.smart_roller_blind;


import java.util.ArrayList;
import java.util.List;

public class Object_model {
    private String name;
    private boolean isOpen;
    private int position;
    private int id;
    private String sup_devise;
    private int speed_up;
    private int speed_down;
    private boolean active;
    private int length;

    private boolean active_check;
    private String image;
    private boolean auto_on;
    private int auto_threshold;

    private int sensor_now;
    private List<Alarms> alarm;

    public Object_model(String name, boolean isOpen, int position, int id,String sup_devise,int speed_up, int speed_down, boolean active, int length, boolean active_check, String image, boolean auto_on, int auto_threshold, int sensor_now, List<Alarms> alarm) {
        this.name = name;
        this.isOpen = isOpen;
        this.position = position;
        this.id = id;
        this.sup_devise = sup_devise;
        this.speed_up = speed_up;
        this.speed_down = speed_down;
        this.active = active;
        this.length = length;
        this.active_check = active_check;
        this.image = image;
        this.auto_on = auto_on;
        this.auto_threshold = auto_threshold;
        this.sensor_now = sensor_now;
        this.alarm = alarm;
    }
    public Object_model(String name, boolean isOpen, int position, int id,String sup_devise,int speed_up, int speed_down, boolean active, int length, boolean active_check, String image, boolean auto_on, int auto_threshold, int sensor_now, String alarm) {
        this.name = name;
        this.isOpen = isOpen;
        this.position = position;
        this.id = id;
        this.sup_devise = sup_devise;
        this.speed_up = speed_up;
        this.speed_down = speed_down;
        this.active = active;
        this.length = length;
        this.active_check = active_check;
        this.image = image;
        this.auto_on = auto_on;
        this.auto_threshold = auto_threshold;
        this.sensor_now = sensor_now;
        setAlarmSt(alarm);
    }

    @Override
    public String toString() {
        return "Object_model{" +
                "name='" + name + '\'' +
                ", isOpen=" + isOpen +
                ", position=" + position +
                ", id=" + id +
                ", sup_devise=" + sup_devise +
                ", speed_up=" + speed_up +
                ", speed_down=" + speed_down +
                ", active=" + active +
                ", length=" + length +
                ", image=" + image +
                ", auto_on=" + auto_on +
                ", auto_threshold=" + auto_threshold +
                ", sensor_now=" + sensor_now +
                ", alarm=" + alarm +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getisOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSup_devise() {
        return sup_devise;
    }

    public void setSup_devise(String sup_devise) {
        this.sup_devise = sup_devise;
    }

    public int getSpeed_up() {
        return speed_up;
    }

    public void setSpeed_up(int speed_up) {
        this.speed_up = speed_up;
    }

    public int getSpeed_down() {
        return speed_down;
    }

    public void setSpeed_down(int speed_down) {
        this.speed_down = speed_down;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean getActive_check() {
        return active_check;
    }

    public void setActive_check(boolean active_check) {
        this.active_check = active_check;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getAuto_on() {
        return auto_on;
    }

    public void setAuto_on(boolean auto_on) {
        this.auto_on = auto_on;
    }

    public int getAuto_threshold() {
        return auto_threshold;
    }

    public void setAuto_threshold(int auto_threshold) {
        this.auto_threshold = auto_threshold;
    }

    public int getSensor_now() {
        return sensor_now;
    }

    public void setSensor_now(int sensor_now) {
        this.sensor_now = sensor_now;
    }

    public List<Alarms> getAlarm() {
        return alarm;
    }

    public void setAlarm(List<Alarms> alarm) {
        this.alarm = alarm;
    }

    public String getAlarmStr(){
        String fin = "";
        for(int i = 0;i < this.alarm.size();i++){
            fin+=String.valueOf(this.alarm.get(i).getWD()) + "," +
                    String.valueOf(this.alarm.get(i).getH()) + "," +
                    String.valueOf(this.alarm.get(i).getM()) + "," +
                    (this.alarm.get(i).getPos() ? "1" : "0") + "," +
                    (this.alarm.get(i).getAct() ? "1" : "0");
            if(i != (this.alarm.size()-1)){
                fin+=";";
            }
        }
        return fin;
    }
    public void setAlarmSt(String alarms){
        List<Alarms> list_alarm = new ArrayList<>();
        if(!alarms.equals("")) {
            String[] alarm = alarms.split(";");///
            for (String item_s : alarm) {
                String[] item = item_s.split(",");
                list_alarm.add(new Alarms(Integer.parseInt(item[0]), Integer.parseInt(item[1]), Integer.parseInt(item[2]), Integer.parseInt(item[3]) == 1, Integer.parseInt(item[4]) == 1));
            }
        }
        this.alarm = list_alarm;
    }
    public void update_one_alarm(Alarms alarms,int position){
        this.alarm.set(position,alarms);
    }
    public void add_one_alarm(Alarms alarms){
        this.alarm.add(alarms);
    }

    public void del_one_alarm(int index){
        List<Alarms> list_alarm = new ArrayList<>();
        for(int i = 0; i<this.alarm.size();i++) {
            if(index!=i){
                list_alarm.add(this.alarm.get(i));
            }
        }
        this.alarm = list_alarm;
    }

    public String getAlarmStr_for_esp(){
        String fin = "";
        for(int i = 0;i < this.alarm.size();i++){
            if(this.alarm.get(i).getAct()) {
                if (i != 0) {
                    fin += ";";
                }
                fin += String.valueOf(this.alarm.get(i).getWD()) + "," +
                        String.valueOf(this.alarm.get(i).getH()) + "," +
                        String.valueOf(this.alarm.get(i).getM()) + "," +
                        (this.alarm.get(i).getPos() ? "1" : "0");
            }
        }
        return fin;
    }
}
