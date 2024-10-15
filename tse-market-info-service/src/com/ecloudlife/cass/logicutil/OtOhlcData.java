package com.ecloudlife.cass.logicutil;

import java.math.BigDecimal;

public class OtOhlcData {

    BigDecimal open = new BigDecimal(0);
    BigDecimal high = new BigDecimal(0);
    BigDecimal low = new BigDecimal(0);
    BigDecimal current = new BigDecimal(0);
    BigDecimal volume = new BigDecimal(0);
    BigDecimal subvolume = new BigDecimal(0);
    long tlong = 0;

    String time = "";
    String date = "";

    public OtOhlcData(long tlong,String date,String time, BigDecimal last,BigDecimal volume) {
      this.tlong = tlong;
      this.open = new BigDecimal(last.toString());
        this.high = new BigDecimal(last.toString());
        this.low = new BigDecimal(last.toString());
        this.current = new BigDecimal(last.toString());
        this.volume = new BigDecimal(volume.toString());
        this.time = time;
        this.date = date;
    }

    public void setTrade(BigDecimal last,BigDecimal volume){
        current = new BigDecimal(last.toString());
        this.volume = new BigDecimal(volume.toString());
        if(high.compareTo(current)==-1){
            high = new BigDecimal(last.toString());
        }
        if(low.compareTo(current)==1){
            low = new BigDecimal(last.toString());
        }
    }

    public static void main(String[] args) {
        BigDecimal aa = new BigDecimal("-");
        System.out.println(aa.toString());
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getCurrent() {
        return current;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public String getTime() {
        return time;
    }

    public BigDecimal getVolume() {
        return volume;
    }

  public String getDate() {
    return date;
  }

  public BigDecimal getSubvolume() {
    return subvolume;
  }

  public long getTlong() {
    return tlong;
  }

  public void setSubvolume(BigDecimal subvolume) {
    this.subvolume = this.subvolume.add(subvolume) ;
  }
}
