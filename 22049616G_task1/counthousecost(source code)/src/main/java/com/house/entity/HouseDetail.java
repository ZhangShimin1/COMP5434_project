package com.house.entity;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author zhaodong 2022/5/23 10:0512
 * email: colinzhaodong@gmail.com
 * desc:
 */
public class HouseDetail implements WritableComparable<HouseDetail> {
    /**
     * 住宅面积
     */
    private Integer residenceSpace;
    /**
     * 住宅面积的单价
     */
    private BigDecimal unitPriceOfResidenceSpace;
    /**
     * 建筑面积
     */
    private Integer buildingSpace;
    /**
     * 建筑面积的单价
     */
    private BigDecimal unitPriceOfBuildingSpace;
    /**
     * 汇率
     */
    private BigDecimal exchangeRate;


    public HouseDetail() {
    }

    public Integer getResidenceSpace() {
        return residenceSpace;
    }

    public void setResidenceSpace(Integer residenceSpace) {
        this.residenceSpace = residenceSpace;
    }

    public BigDecimal getUnitPriceOfResidenceSpace() {
        return unitPriceOfResidenceSpace;
    }

    public void setUnitPriceOfResidenceSpace(BigDecimal unitPriceOfResidenceSpace) {
        this.unitPriceOfResidenceSpace = unitPriceOfResidenceSpace;
    }

    public Integer getBuildingSpace() {
        return buildingSpace;
    }

    public void setBuildingSpace(Integer buildingSpace) {
        this.buildingSpace = buildingSpace;
    }

    public BigDecimal getUnitPriceOfBuildingSpace() {
        return unitPriceOfBuildingSpace;
    }

    public void setUnitPriceOfBuildingSpace(BigDecimal unitPriceOfBuildingSpace) {
        this.unitPriceOfBuildingSpace = unitPriceOfBuildingSpace;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return residenceSpace + "," + unitPriceOfResidenceSpace + "," + buildingSpace + "," + unitPriceOfBuildingSpace + "," + exchangeRate;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeInt(residenceSpace);
        dataOutput.writeDouble(unitPriceOfResidenceSpace.doubleValue());
        dataOutput.writeInt(buildingSpace);
        dataOutput.writeDouble(unitPriceOfBuildingSpace.doubleValue());
        dataOutput.writeDouble(exchangeRate.doubleValue());
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.residenceSpace = dataInput.readInt();
        this.unitPriceOfResidenceSpace = BigDecimal.valueOf(dataInput.readDouble());
        this.buildingSpace = dataInput.readInt();
        this.unitPriceOfBuildingSpace = BigDecimal.valueOf(dataInput.readDouble());
        this.exchangeRate = BigDecimal.valueOf(dataInput.readDouble());
    }
    @Override
    public int compareTo(HouseDetail o) {
        return o.getResidenceSpace().compareTo(o.getBuildingSpace());
    }
}
